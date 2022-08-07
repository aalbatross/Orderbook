package org.aalbatross.reactive.subscribers;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import org.aalbatross.orderbook.entities.Order;
import org.aalbatross.orderbook.entities.OrderType;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ProductOrderSubscriberTest {

    @Test
    public void simpleTest() {
        var subs = new ProductOrderSubscriber("XYZ");
        Flowable.just(Order.builder().orderType(OrderType.BUY).price(23.0d).size(1.0d).build())
                .subscribe(subs);

        var book = subs.book();
        Assert.assertEquals("XYZ", book.getProductId());

        final var standardOut = System.out;
        final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        book.display();
        Assert.assertFalse(outputStreamCaptor.toString(StandardCharsets.UTF_8).isEmpty());
        Assert.assertTrue(outputStreamCaptor.toString(StandardCharsets.UTF_8).startsWith("Orderbook: productId: XYZ Frequency: buy: 1 sell: 0"));
        System.setOut(standardOut);
    }

    @Test
    public void errorOnSubscriptionTest() {
        final var standardOut = System.out;
        final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        var subs = new ProductOrderSubscriber("XYZ");
        FlowableOnSubscribe<Order> publisher = (emitter) -> {
            emitter.onError(new RuntimeException("Bullshit"));
        };
        Flowable.create(publisher, BackpressureStrategy.LATEST).subscribe(subs);
        Assert.assertFalse(outputStreamCaptor.toString(StandardCharsets.UTF_8).isEmpty());
        Assert.assertTrue(outputStreamCaptor.toString(StandardCharsets.UTF_8).contains("Error while receiving subscription Bullshit"));
        System.setOut(standardOut);
    }
}
