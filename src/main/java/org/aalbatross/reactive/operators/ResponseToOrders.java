package org.aalbatross.reactive.operators;

import org.aalbatross.orderbook.entities.Order;
import org.aalbatross.orderbook.entities.OrderType;
import org.aalbatross.orders.channels.response.Level2Response;
import org.aalbatross.orders.channels.response.Level2SnapshotResponse;
import org.aalbatross.orders.channels.response.Level2UpdateNotification;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResponseToOrders implements Function<Level2Response, List<Order>> {

    private final Function<Level2SnapshotResponse, List<Order>> snapshotResponseToOrders = response -> {
        var buyOrders = response.getBids().stream().map(fields ->
                Order.builder()
                        .orderType(OrderType.BUY)
                        .price(Double.valueOf(fields.get(0)))
                        .size(Double.valueOf(fields.get(1)))
                        .build()
        ).collect(Collectors.toList());
        var sellOrders = response.getAsks().stream().map(fields -> Order.builder()
                .orderType(OrderType.SELL)
                .price(Double.valueOf(fields.get(0)))
                .size(Double.valueOf(fields.get(1)))
                .build()).collect(Collectors.toList());
        return Arrays.asList(buyOrders, sellOrders).stream().flatMap(Collection::stream).collect(Collectors.toList());
    };
    private final Function<Level2UpdateNotification, List<Order>> updateNotificationToOrders = response -> {
        return response.getChanges().stream().map(change ->
                Order.builder()
                        .orderType(OrderType.valueOf(change.get(0).toUpperCase(Locale.ROOT)))
                        .price(Double.valueOf(change.get(1)))
                        .size(Double.valueOf(change.get(2)))
                        .build()
        ).collect(Collectors.toList());
    };

    @Override
    public List<Order> apply(Level2Response response) {
        return Optional.ofNullable(response).map(resp -> resp.getType())
                .map(type -> {
                    if (type.equals("snapshot")) {
                        return snapshotResponseToOrders.apply((Level2SnapshotResponse) response);
                    } else if (type.equals("l2update")) {
                        return updateNotificationToOrders.apply((Level2UpdateNotification) response);
                    } else {
                        return new ArrayList<Order>();
                    }
                }).orElse(new ArrayList<>());
    }

}
