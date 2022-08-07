package org.aalbatross.reactive.flows;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum OrderbookFlowManager {
    INSTANCE;
    private final Map<String, OrderbookFlow> flowStore = new ConcurrentHashMap<>();

    public void createNewOrderbook(String productId) {
        var productKey = productId.toUpperCase(Locale.ROOT);
        if (flowStore.containsKey(productKey))
            throw new RuntimeException("Flow already running");
        flowStore.computeIfAbsent(productKey, (key) -> {
            var flow = new OrderbookFlow(productKey, key + UUID.randomUUID());
            flow.start();
            return flow;
        });
    }

    public void stopOrderbook(String productId) {
        var productKey = productId.toUpperCase(Locale.ROOT);
        if (flowStore.containsKey(productKey)) {
            flowStore.get(productKey).stop();
            flowStore.remove(productKey);
        } else {
            throw new RuntimeException("Create new Orderbook for " + productKey + " to stop it.");
        }
    }

    public void displayOrderBook(String productId) {
        var productKey = productId.toUpperCase(Locale.ROOT);
        if (flowStore.containsKey(productKey)) {
            flowStore.get(productKey).display();
        } else {
            throw new RuntimeException("Create new Orderbook for " + productKey + " to display it.");
        }
    }

    public Set<String> list() {
        return flowStore.keySet();
    }

    public void closeAll() {
        flowStore.entrySet().forEach(entry -> {
            entry.getValue().stop();
            flowStore.remove(entry.getKey());
        });
    }

}
