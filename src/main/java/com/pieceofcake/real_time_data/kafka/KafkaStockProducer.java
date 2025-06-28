package com.pieceofcake.real_time_data.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.real_time_data.kafka.event.StockQuoteKafkaMessage;
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
    private final ObjectMapper objectMapper;

    public void sendQuotesRealTimeData(String rawJson, String stockCode) {
        try {
            StockQuoteKafkaMessage payload = new StockQuoteKafkaMessage(stockCode, rawJson);
            String jsonMessage = objectMapper.writeValueAsString(payload);
            realTimeDatakafkaTemplate.send("quotes-stock-data", jsonMessage);
            log.info("📤 Kafka 전송 완료: {}", jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("❌ Kafka 메시지 직렬화 실패", e);
        }
    }

    public void sendExecutionRealTimeData(String json) {
        log.info("📤 ExecutionRealTimeData Kafka 전송 완료: {}", json);
        CompletableFuture<SendResult<String, String>> future =
                realTimeDatakafkaTemplate.send("real-time-stock-data", json);
    }
}
