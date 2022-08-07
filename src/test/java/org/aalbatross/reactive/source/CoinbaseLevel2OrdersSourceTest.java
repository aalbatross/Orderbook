package org.aalbatross.reactive.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.aalbatross.orderbook.entities.Order;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

public class CoinbaseLevel2OrdersSourceTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testSnapshotNormalFlow() throws Throwable {
        var src = spy(CoinbaseLevel2OrderSource.class);
        doNothing().when(src).subscribeProductId();
        doNothing().when(src).init();
        doNothing().when(src).unsubscribe();

        var testSubscriber = new TestSubscriber<Order>();
        var flowable = Flowable.create(src, BackpressureStrategy.LATEST);
        flowable.subscribeOn(Schedulers.io()).subscribe(testSubscriber);

        String message = "{\n" +
                "  \"type\":\"snapshot\",\n" +
                "  \"product_id\": \"BTC-USD\",\n" +
                "  \"bids\": [[\"10101.10\", \"0.45054140\"]],\n" +
                "  \"asks\": [[\"10102.55\", \"0.57753524\"]]\n" +
                "}";

        src.onMessage(message, true);
        Assert.assertTrue(src.isRunning());
        src.stop();
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(2);
    }

    @Test
    public void testUpdateNormalFlow() throws Throwable {
        var src = spy(CoinbaseLevel2OrderSource.class);
        doNothing().when(src).subscribeProductId();
        doNothing().when(src).init();
        doNothing().when(src).unsubscribe();

        var testSubscriber = new TestSubscriber<Order>();
        var flowable = Flowable.create(src, BackpressureStrategy.LATEST);
        flowable.subscribeOn(Schedulers.io()).subscribe(testSubscriber);

        String message = "{\n" +
                "  \"type\":\"l2update\",\n" +
                "  \"product_id\": \"BTC-USD\",\n" +
                "  \"changes\": [\n" +
                "    [\n" +
                "      \"buy\",\n" +
                "      \"22356.270000\",\n" +
                "      \"0.00000000\"\n" +
                "    ],\n" +
                "    [\n" +
                "      \"buy\",\n" +
                "      \"22356.300000\",\n" +
                "      \"1.00000000\"\n" +
                "    ]\n" +
                "  ],\n" +
                "  \"time\": \"2022-08-04T15:25:05.010758Z\"\n" +
                "}";

        src.onMessage(message, true);
        src.stop();
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(2);
    }

    @Test
    public void testUnknownEventFlow() throws Throwable {
        var src = spy(CoinbaseLevel2OrderSource.class);
        doNothing().when(src).subscribeProductId();
        doNothing().when(src).init();
        doNothing().when(src).unsubscribe();

        var testSubscriber = new TestSubscriber<Order>();
        var flowable = Flowable.create(src, BackpressureStrategy.LATEST);
        flowable.subscribeOn(Schedulers.io()).subscribe(testSubscriber);

        String message = "{\n" +
                "  \"type\":\"abc\",\n" +
                "  \"product_id\": \"BTC-USD\",\n" +
                "  \"changes\": [\n" +
                "    [\n" +
                "      \"buy\",\n" +
                "      \"22356.270000\",\n" +
                "      \"0.00000000\"\n" +
                "    ],\n" +
                "    [\n" +
                "      \"buy\",\n" +
                "      \"22356.300000\",\n" +
                "      \"1.00000000\"\n" +
                "    ]\n" +
                "  ],\n" +
                "  \"time\": \"2022-08-04T15:25:05.010758Z\"\n" +
                "}";

        src.onMessage(message, true);
        src.stop();
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(0);
    }

    @Test
    public void testNonJsonEventFlow() throws Throwable {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectCause(CoreMatchers.is(JsonProcessingException.class));
        var src = spy(CoinbaseLevel2OrderSource.class);
        doNothing().when(src).subscribeProductId();
        doNothing().when(src).init();
        doNothing().when(src).unsubscribe();

        var testSubscriber = new TestSubscriber<Order>();
        var flowable = Flowable.create(src, BackpressureStrategy.LATEST);
        flowable.subscribeOn(Schedulers.io()).subscribe(testSubscriber);

        String message = "some random data";

        src.onMessage(message, true);
    }
}
