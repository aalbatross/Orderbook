package org.aalbatross.orders.channels.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Level2SnapshotResponse extends Level2Response {
    private List<List<String>> bids;
    private List<List<String>> asks;

    @Builder
    public Level2SnapshotResponse(String type, String productId, List<List<String>> bids, List<List<String>> asks) {
        super(type, productId);
        this.bids = bids;
        this.asks = asks;
    }
}
