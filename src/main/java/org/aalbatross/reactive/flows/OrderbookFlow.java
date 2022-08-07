package org.aalbatross.reactive.flows;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.NonNull;
import org.aalbatross.reactive.source.CoinbaseLevel2OrderSource;
import org.aalbatross.reactive.subscribers.ProductOrderSubscriber;

public class OrderbookFlow implements Flow {
    private final String productId;
    private final String name;
    private final CoinbaseLevel2OrderSource level2OrderSource;
    private final ProductOrderSubscriber subscriber;

    public OrderbookFlow(@NonNull String productId, @NonNull String name) {
        this.productId = productId;
        this.name = name;
        this.level2OrderSource = new CoinbaseLevel2OrderSource(productId);
        this.subscriber = new ProductOrderSubscriber(productId);
    }

    @Override
    public String name() {
        return name;
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public synchronized void start() {
        Flowable.create(level2OrderSource, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }

    public synchronized void display() {
        subscriber.book().display();
    }

    @Override
    public synchronized void stop() {
        level2OrderSource.stop();
    }

    @Override
    public synchronized boolean isRunning() {
        return level2OrderSource.isRunning();
    }
}
