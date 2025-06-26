package com.pieceofcake.real_time_data.stocktest.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaStockProducer {
    private final KafkaTemplate<String, String> realTimeDatakafkaTemplate;

    public void sendQuotesRealTimeData(String json) {
//        kafkaTemplate.send("stock-data", json);
        log.info("📤 QuotesRealTimeData Kafka 전송 완료: {}", json);
        CompletableFuture<SendResult<String, String>> future =
                realTimeDatakafkaTemplate.send("stock-data", json);
    }

    public void sendExecutionRealTimeData(String json) {
        log.info("📤 ExecutionRealTimeData Kafka 전송 완료: {}", json);
        CompletableFuture<SendResult<String, String>> future =
                realTimeDatakafkaTemplate.send("execution-stock-data", json);
    }
}
