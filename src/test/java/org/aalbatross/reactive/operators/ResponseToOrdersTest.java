package org.aalbatross.reactive.operators;

import org.aalbatross.orderbook.entities.OrderType;
import org.aalbatross.orders.channels.response.Level2Response;
import org.aalbatross.orders.channels.response.Level2SnapshotResponse;
import org.aalbatross.orders.channels.response.Level2UpdateNotification;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseToOrdersTest {


    @Test
    public void testSnapshotPositive() {
        var asks = List.of(List.of("10101.10", "0.45054140"));
        var bids = List.of(List.of("10102.55", "0.57753524"));

        var snapshotResponse = Level2SnapshotResponse.builder().productId("XYZ").type("snapshot").asks(asks).bids(bids).build();
        var orders = new ResponseToOrders().apply(snapshotResponse);
        Assert.assertFalse(orders.isEmpty());
        Assert.assertEquals(2, orders.size());

        var onlySells = orders.stream().filter(order -> order.getOrderType().equals(OrderType.SELL)).collect(Collectors.toList());
        Assert.assertEquals(1, onlySells.size());
        Assert.assertEquals(10101.10d, onlySells.get(0).getPrice(), 0.0d);
        Assert.assertEquals(0.45054140d, onlySells.get(0).getSize(), 0.0d);

        var onlyBuys = orders.stream().filter(order -> order.getOrderType().equals(OrderType.BUY)).collect(Collectors.toList());
        Assert.assertEquals(1, onlyBuys.size());
        Assert.assertEquals(10102.55d, onlyBuys.get(0).getPrice(), 0.0d);
        Assert.assertEquals(0.57753524d, onlyBuys.get(0).getSize(), 0.0d);
    }

    @Test
    public void testUpdatePositive() {
        var changes = List.of(List.of("buy", "10102.55", "0.57753524"), List.of("sell", "10101.10", "0.45054140"));

        var updateNotification = Level2UpdateNotification.builder().productId("XYZ").type("l2update").time(LocalDateTime.now()).changes(changes).build();
        var orders = new ResponseToOrders().apply(updateNotification);
        Assert.assertFalse(orders.isEmpty());
        Assert.assertEquals(2, orders.size());

        var onlySells = orders.stream().filter(order -> order.getOrderType().equals(OrderType.SELL)).collect(Collectors.toList());
        Assert.assertEquals(1, onlySells.size());
        Assert.assertEquals(10101.10d, onlySells.get(0).getPrice(), 0.0d);
        Assert.assertEquals(0.45054140d, onlySells.get(0).getSize(), 0.0d);
    }

    @Test
    public void otherTypes() {
        var response = new Level2Response();
        response.setType("abc");
        response.setProductId("zad");
        var orders = new ResponseToOrders().apply(response);
        Assert.assertEquals(0, orders.size());
    }

    @Test
    public void nullTypes() {
        var response = new Level2Response();
        var orders = new ResponseToOrders().apply(response);
        Assert.assertEquals(0, orders.size());
    }
}
