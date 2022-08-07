package org.aalbatross.orders.channels.requests;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Level2Unsubscribe {
    private final String type = "unsubscribe";
    private final List<String> productIds;
    private final List<String> channels = List.of("level2");

    public Level2Unsubscribe(List<String> productIds) {
        this.productIds = productIds;
    }

}
