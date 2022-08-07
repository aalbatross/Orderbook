package org.aalbatross.reactive.subscribers;

import io.reactivex.rxjava3.core.FlowableSubscriber;
import org.aalbatross.orderbook.Orderbook;
import org.aalbatross.orderbook.entities.Order;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductOrderSubscriber implements FlowableSubscriber<Order> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductOrderSubscriber.class);
    private final String productId;
    private final Orderbook book;
    private Subscription subscription;

    public ProductOrderSubscriber(String productId) {
        this.productId = productId;
        this.book = new Orderbook(productId);
    }

    public Orderbook book() {
        return book;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        LOGGER.debug("Orderbook initialised");
        subscription.request(1L);
    }

    @Override
    public void onNext(Order order) {
        book.update(order);
        subscription.request(1L);
    }

    @Override
    public void onError(Throwable throwable) {
        LOGGER.error("Error while receiving subscription {}", throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        LOGGER.info("Subscription completed.");
    }
}
