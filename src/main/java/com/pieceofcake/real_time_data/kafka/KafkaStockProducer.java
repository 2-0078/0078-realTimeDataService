package com.pieceofcake.real_time_data.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.real_time_data.kafka.event.StockKafkaMessage;
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
            StockKafkaMessage payload = new StockKafkaMessage(stockCode, rawJson);
            String jsonMessage = objectMapper.writeValueAsString(payload);
            realTimeDatakafkaTemplate.send("quotes-stock-data", jsonMessage);
            log.info("📤 Quotes Kafka 전송 완료: {}", jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("❌ Quotes Kafka 메시지 직렬화 실패", e);
        }
    }

    public void sendMarketPriceRealTimeData(String rawJson, String stockCode) {
        try {
            StockKafkaMessage payload = new StockKafkaMessage(stockCode, rawJson);
            String jsonMessage = objectMapper.writeValueAsString(payload);
            realTimeDatakafkaTemplate.send("market-price-stock-data", jsonMessage);
            log.info("📤 Market Price Kafka 전송 완료: {}", jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("❌ Market Price Kafka 메시지 직렬화 실패", e);
        }
    }
}
