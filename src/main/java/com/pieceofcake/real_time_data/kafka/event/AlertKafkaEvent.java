package com.pieceofcake.real_time_data.kafka.event;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class AlertKafkaEvent {
    private String key;
    private String message;
    private String memberUuid;            // 공용 알람이면 null
    private Boolean commonAlert = true;   // 공용 알람이면 null or true

    @Builder
    public AlertKafkaEvent(
            String key,
            String message,
            String memberUuid,
            boolean commonAlert) {
        this.key = key;
        this.message = message;
        this.memberUuid = memberUuid;
        this.commonAlert = commonAlert;
    }
}
