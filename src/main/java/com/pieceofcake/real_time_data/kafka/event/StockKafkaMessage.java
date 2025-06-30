package com.pieceofcake.real_time_data.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockKafkaMessage {
    private String stockCode;  // 종목 코드
    private String rawJson;    // 원본 JSON 데이터
}
