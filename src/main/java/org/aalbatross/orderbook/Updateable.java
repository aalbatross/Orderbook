package org.aalbatross.orderbook;

import org.aalbatross.orderbook.entities.Order;

public interface Updateable {
    void update(Order order);
}
