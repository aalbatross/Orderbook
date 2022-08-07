package org.aalbatross.orders.channels.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Level2Response {
    private String type;
    @JsonProperty("product_id")
    private String productId;
}
