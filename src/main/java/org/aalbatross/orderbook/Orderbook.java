package org.aalbatross.orderbook;

import org.aalbatross.orderbook.entities.Order;
import org.aalbatross.orderbook.entities.OrderType;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Orderbook implements Displayable, Updateable {
    private final String productId;
    private final Deque<Order> buyOrders = new ConcurrentLinkedDeque<>();
    private final Deque<Order> sellOrders = new ConcurrentLinkedDeque<>();

    public Orderbook(String productId) {
        Objects.requireNonNull(productId, "Orderbook cannot be initialized, ProductId cannot be null !!.");
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    List<Order> top10Buys() {
        Queue<Order> minHeap = new PriorityQueue<>((o1, o2) -> Double.valueOf(o1.getPrice()).compareTo(o2.getPrice()));
        Collection<Order> buyCollection = Collections.unmodifiableCollection(buyOrders);
        List<Order> buys = new ArrayList<>();
        for (Order order : buyCollection) {
            minHeap.offer(order);
            if (minHeap.size() > 10)
                minHeap.poll();
        }
        while (!minHeap.isEmpty())
            buys.add(minHeap.poll());
        Collections.reverse(buys);
        return buys;
    }

    List<Order> top10Sells() {
        Queue<Order> maxHeap = new PriorityQueue<>((o1, o2) -> Double.valueOf(o2.getPrice()).compareTo(o1.getPrice()));
        Collection<Order> sellCollection = Collections.unmodifiableCollection(sellOrders);
        List<Order> sells = new ArrayList<>();
        for (Order order : sellCollection) {
            maxHeap.offer(order);
            if (maxHeap.size() > 10)
                maxHeap.poll();
        }
        while (!maxHeap.isEmpty())
            sells.add(maxHeap.poll());
        Collections.reverse(sells);
        return sells;
    }

    int buySize() {
        return buyOrders.size();
    }

    int sellSize() {
        return sellOrders.size();
    }

    @Override
    public void display() {
        System.out.println(String.format("Orderbook: productId: %s Frequency: buy: %d sell: %d", productId, buyOrders.size(), sellOrders.size()));
        var topBuyOrders = top10Buys();
        var topSellOrders = top10Sells();
        for (int i = 0; i < topBuyOrders.size(); i++) {
            Optional<Order> buyItem = Optional.empty();
            Optional<Order> sellItem = Optional.empty();

            if (topBuyOrders.size() - 1 >= i)
                buyItem = Optional.of(topBuyOrders.get(i));
            if (topSellOrders.size() - 1 >= i)
                sellItem = Optional.of(topSellOrders.get(i));

            System.out.println("| " + buyItem.map(item -> item.toString()).orElse("") + " | " + sellItem.map(item -> item.toString()).orElse("") + " |");
        }
    }

    @Override
    public void update(Order order) {
        if (order.getOrderType().equals(OrderType.BUY))
            buyOrders.offer(order);
        if (order.getOrderType().equals(OrderType.SELL))
            sellOrders.offer(order);
    }
}
