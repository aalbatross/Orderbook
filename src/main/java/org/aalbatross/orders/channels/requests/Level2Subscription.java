package org.aalbatross.orders.channels.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Level2Subscription {
    private final String type = "subscribe";
    @JsonProperty("product_ids")
    private final List<String> productIds;
    private final List<String> channels = List.of("level2");

    public Level2Subscription(List<String> productIds) {
        this.productIds = productIds;
    }
}
