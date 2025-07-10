package com.pieceofcake.real_time_data.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pieceofcake.real_time_data.kafka.event.AlertKafkaEvent;
import com.pieceofcake.real_time_data.kisapi.application.KisEntityServiceImpl;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaStockConsumer {
    private final KisEntityServiceImpl kisEntityService;
    private final StringRedisTemplate redisTemplate;
    private final KafkaStockProducer kafkaStockProducer;

    @KafkaListener(topics = "quotes-stock-data", groupId = "stock-quotes-group", containerFactory = "realTimeDataEventListener")
    public void consumeQuotesRealTimeData(String message) {
        log.info("kafka quotes-stock-data consume {}", message);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(message);

            String stockCode = root.path("stockCode").asText();
            String rawJson = root.path("rawJson").asText();

            // Redis에서 stockCode로 pieceProductUuid set 조회
            Set<String> pieceUuids = redisTemplate.opsForSet().members("stock:piece:" + stockCode);
            if (pieceUuids == null || pieceUuids.isEmpty()) {
                log.warn("⚠️ stockCode={}에 대한 매핑 정보가 없습니다", stockCode);
                return;
            }

            // rawJson → DTO 변환
            GetRealTimeQuotesResponseDto dto = GetRealTimeQuotesResponseDto.toDto(stockCode, rawJson);
            log.info("🔍 Quotes DTO 변환 완료: {}", objectMapper.writeValueAsString(dto));
            log.info("✅ 관련 pieceUuids: {}", pieceUuids);

            // MongoDB에 Reactive 저장
            kisEntityService.saveKisQuotesInfo(dto)
                    .doOnNext(saved -> log.info("✅ MongoDB 저장 완료: {}", saved))
                    .subscribe();

        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패", e);
        }
    }

    @KafkaListener(topics = "market-price-stock-data", groupId = "market-price-stock-group", containerFactory = "realTimeDataEventListener")
    public void consumeExecutionRealTimeData(String message) {
        log.info("kafka market-price-stock-data consume {}", message);

        try {
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            JsonNode root = objectMapper.readTree(message);

            String stockCode = root.path("stockCode").asText();
            String rawJson = root.path("rawJson").asText();

            // 🔥 Redis에서 stockCode로 pieceProductUuid set 조회
            Set<String> pieceUuids = redisTemplate.opsForSet().members("stock:piece:" + stockCode);
            if (pieceUuids == null || pieceUuids.isEmpty()) {
                log.warn("⚠️ stockCode={}에 대한 매핑 정보가 없습니다", stockCode);
                return;
            }

            // rawJson → List<DTO> 변환
            List<GetRealTimeMarketPriceResponseDto> dtoList = GetRealTimeMarketPriceResponseDto.toDto(stockCode, rawJson);
            if (dtoList.isEmpty()) {
                log.warn("⚠️ 변환된 데이터가 없습니다: {}", rawJson);
                return;
            }

            // MongoDB에 Reactive 저장
            kisEntityService.saveKisMarketPrice(dtoList)
                    .doOnNext(saved -> log.info("✅ MongoDB 저장 완료: {}", saved))
                    .doOnComplete(() -> {
                        // Kafka 이벤트 발행
                        pieceUuids.forEach(pieceUuid -> {
                            AlertKafkaEvent event = AlertKafkaEvent.builder()
                                    .key(pieceUuid)
                                    .message("실시간 시세 데이터 반영 완료") // 적절한 메시지로 수정 가능
                                    .memberUuid(null) // 공용 알림
                                    .commonAlert(true)
                                    .build();
                            kafkaStockProducer.updatePiecePriceAlertEvent(event);
                        });
                    })
                    .subscribe();



        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패", e);
        }
    }
}