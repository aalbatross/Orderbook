package org.aalbatross.orderbook.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Order {
    private final OrderType orderType;
    private final double price;
    private final double size;
}
