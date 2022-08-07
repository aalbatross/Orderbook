package org.aalbatross.reactive.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import jakarta.websocket.*;
import org.aalbatross.orderbook.entities.Order;
import org.aalbatross.orders.channels.requests.Level2Subscription;
import org.aalbatross.orders.channels.requests.Level2Unsubscribe;
import org.aalbatross.orders.channels.response.Level2Response;
import org.aalbatross.orders.channels.response.Level2SnapshotResponse;
import org.aalbatross.orders.channels.response.Level2UpdateNotification;
import org.aalbatross.reactive.operators.ResponseToOrders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@ClientEndpoint
public class CoinbaseLevel2OrderSource extends Endpoint implements FlowableOnSubscribe<Order>, Source {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoinbaseLevel2OrderSource.class);
    private static final String ENDPOINT = "wss://ws-feed.exchange.coinbase.com";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String productId;
    private final Decoder decoder = new Decoder();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final CountDownLatch latch = new CountDownLatch(1);
    private Session session;
    private FlowableEmitter<Order> emitter;
    private StringBuffer sb = new StringBuffer();

    CoinbaseLevel2OrderSource() {
        this("mock");
    }

    public CoinbaseLevel2OrderSource(String productId) {
        this.productId = productId;
        MAPPER.registerModule(new JSR310Module());
    }

    void init() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, URI.create(ENDPOINT));
            container.setDefaultMaxTextMessageBufferSize(Integer.MAX_VALUE);
            container.setDefaultMaxBinaryMessageBufferSize(Integer.MAX_VALUE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        LOGGER.info("opening connection");
        this.session = session;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        LOGGER.info("closing subscription {} with reason {}.", productId, reason);
        isRunning.set(false);
        if (this.session != null) {
            try {
                session.close(reason);
            } catch (IOException e) {
                onError(new RuntimeException(e));
            }
        }
        this.session = null;
        latch.countDown();
        emitter.onComplete();
    }

    @OnError
    public final void onError(Throwable throwable) {
        emitter.onError(throwable);
    }

    @OnMessage
    public void onMessage(String message, boolean last) {
        LOGGER.debug("Received messages from the coinbase {}", message);
        sb.append(message);
        if (last) {
            decoder.apply(sb.toString())
                    .map(response -> new ResponseToOrders().apply(response))
                    .stream().flatMap(Collection::stream)
                    .forEachOrdered(order -> {
                        if (emitter != null)
                            emitter.onNext(order);
                    });
            sb = new StringBuffer();
        }
    }

    private void sendMessage(String message) {
        if (this.session != null) {
            LOGGER.info("Sending message to the coinbase {}", message);
            this.session.getAsyncRemote().sendText(message);
        }
    }

    void subscribeProductId() throws JsonProcessingException {
        var request = Level2Subscription.builder().productIds(List.of(productId)).build();
        var subMsg = MAPPER.writeValueAsString(request);
        sendMessage(subMsg);
    }

    void unsubscribe() throws JsonProcessingException {
        var request = Level2Unsubscribe.builder().productIds(List.of(productId)).build();
        var subMsg = MAPPER.writeValueAsString(request);
        sendMessage(subMsg);
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<Order> emitter) throws Throwable {
        init();
        this.emitter = emitter;
        isRunning.set(true);
        subscribeProductId();
        LOGGER.info("Subscribed to new product {}", productId);
        try {
            latch.await();
        } catch (InterruptedException ex) {/*Ignore*/}
        if (emitter != null)
            emitter.onComplete();
    }

    @Override
    public void start() {
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public void stop() {
        try {
            unsubscribe();
            onClose(session, new CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE, "Stopping the consumer"));
        } catch (JsonProcessingException e) {
            emitter.onError(new RuntimeException(e));
        }
    }

    static class Decoder implements Function<String, Optional<Level2Response>> {
        @Override
        public Optional<Level2Response> apply(String s) {
            try {
                var node = MAPPER.readTree(s);
                if (node.has("type") && node.get("type").asText().equals("snapshot")) {
                    return Optional.of(MAPPER.treeToValue(node, Level2SnapshotResponse.class));
                } else if (node.has("type") && node.get("type").asText().equals("l2update")) {
                    return Optional.of(MAPPER.treeToValue(node, Level2UpdateNotification.class));
                } else {
                    LOGGER.warn("Unknown event received : {}", s);
                    return Optional.empty();
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
