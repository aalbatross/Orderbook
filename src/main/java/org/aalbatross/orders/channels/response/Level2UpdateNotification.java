package org.aalbatross.orders.channels.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Level2UpdateNotification extends Level2Response {
    private LocalDateTime time;
    private List<List<String>> changes;

    @Builder
    public Level2UpdateNotification(String type, String productId, LocalDateTime time, List<List<String>> changes) {
        super(type, productId);
        this.time = time;
        this.changes = changes;
    }
}
