package com.pieceofcake.real_time_data.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pieceofcake.real_time_data.kisapi.application.KisMarketPriceEntityServiceImpl;
import com.pieceofcake.real_time_data.kisapi.application.sse.KisApiSseEventServiceImpl;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaStockConsumer {
    private final SimpMessagingTemplate messagingTemplate;
    private final KisMarketPriceEntityServiceImpl kisMarketPriceEntityService;
    private final StringRedisTemplate redisTemplate;
    private final KisApiSseEventServiceImpl kisApiSseEventService;

    // websocket
//    private final PieceProductStockMapper mapper;
//    @KafkaListener(topics = "quotes-stock-data", groupId = "stock-quotes-group", containerFactory = "realTimeDataEventListener")
//    public void consumeQuotesRealTimeData(String message) {
//        log.info("kafka quotes-stock-data consume {}", message);
//
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode root = objectMapper.readTree(message);
//
//            String stockCode = root.path("stockCode").asText();
//            String rawJson = root.path("rawJson").asText();
//
//            // 종목코드에 매핑된 productUuid들 가져오기
//            Set<String> pieceUuids = mapper.getPieceProductUuidsByStockCode(stockCode);
//
//            // rawJson → DTO 변환
//            GetRealTimeQuotesResponseDto dto = GetRealTimeQuotesResponseDto.toDto(stockCode, rawJson);
//            String json = new ObjectMapper().writeValueAsString(dto);
//            log.info("🔍 직렬화 확인: {}", json);
//            log.info("StockCode: {} , ProductUuid: {}", stockCode, pieceUuids);
//            // 해당 productUuid에 대해 WebSocket 전송
//            for (String pieceUuid : pieceUuids) {
//                messagingTemplate.convertAndSend("/topic/quotes." + pieceUuid, dto);
//                log.info("📤 WebSocket 전송: /topic/quotes.{} → {}", pieceUuid, dto);
//            }
//
//        } catch (Exception e) {
//            log.error("❌ Kafka 메시지 처리 실패", e);
//        }
//    }

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
            String json = new ObjectMapper().writeValueAsString(dto);
            log.info("🔍 직렬화 확인: {}", json);
            log.info("StockCode: {} , ProductUuid: {}", stockCode, pieceUuids);

            // 각 pieceUuid에 대해 SSE로 송출
            for (String pieceUuid : pieceUuids) {
                kisApiSseEventService.emitQuotesEvent(pieceUuid, dto);
                log.info("📤 Quotes SSE 송출: pieceUuid={} → {}", pieceUuid, dto);
            }

        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패", e);
        }
    }

//    @KafkaListener(topics = "market-price-stock-data", groupId = "market-price-stock-group", containerFactory = "realTimeDataEventListener")
//    public void consumeExecutionRealTimeData(String message) {
//        log.info("kafka market-price-stock-data consume {}", message);
//
//        try {
//            ObjectMapper objectMapper = new ObjectMapper()
//                    .registerModule(new JavaTimeModule())
//                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//            JsonNode root = objectMapper.readTree(message);
//
//            String stockCode = root.path("stockCode").asText();
//            String rawJson = root.path("rawJson").asText();
//
//            // 종목코드에 매핑된 productUuid들 가져오기
//            Set<String> pieceUuids = mapper.getPieceProductUuidsByStockCode(stockCode);
//
//            // rawJson → List<DTO> 변환
//            List<GetRealTimeMarketPriceResponseDto> dtoList = GetRealTimeMarketPriceResponseDto.toDto(stockCode, rawJson);
//            if (dtoList.isEmpty()) {
//                log.warn("⚠️ 변환된 데이터가 없습니다: {}", rawJson);
//                return;
//            }
//
//            kisMarketPriceEntityService.saveKisMarketPrice(dtoList);
//
//            for (GetRealTimeMarketPriceResponseDto dto : dtoList) {
//                String json = objectMapper.writeValueAsString(dto);
//                log.info("🔍 MarketPrice 직렬화 확인: {}", json);
//                log.info("StockCode: {} , ProductUuids: {}", dto.getStockCode(), pieceUuids);
//
//                for (String pieceUuid : pieceUuids) {
//                    messagingTemplate.convertAndSend("/topic/market-price." + pieceUuid, dto);
//                    log.info("📤 WebSocket 전송: /topic/market-price.{} → {}", pieceUuid, dto);
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("❌ Kafka 메시지 처리 실패", e);
//        }
//    }

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

            kisMarketPriceEntityService.saveKisMarketPrice(dtoList);

            for (GetRealTimeMarketPriceResponseDto dto : dtoList) {
                String json = objectMapper.writeValueAsString(dto);
                log.info("🔍 MarketPrice 직렬화 확인: {}", json);
                log.info("StockCode: {} , ProductUuids: {}", dto.getStockCode(), pieceUuids);

                for (String pieceUuid : pieceUuids) {
                    kisApiSseEventService.emitMarketPriceEvent(pieceUuid, dto);
                    log.info("📤 Market price SSE 송출: pieceUuid={} → {}", pieceUuid, dto);
                }
            }

        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패", e);
        }
    }
}