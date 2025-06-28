package com.pieceofcake.real_time_data.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.real_time_data.kisapi.mapper.PieceProductStockMapper;
import com.pieceofcake.real_time_data.stocktest.websocket.StockQuotes;
import com.pieceofcake.real_time_data.stocktest.websocket.StockWebSocketHandler;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaStockConsumer {
    private final SimpMessagingTemplate messagingTemplate;
    private final StockWebSocketHandler stockWebSocketHandler;
    private final PieceProductStockMapper mapper;


    @KafkaListener(topics = "quotes-stock-data", groupId = "stock-quotes-group",  containerFactory = "realTimeDataEventListener")
    public void consumeQuotesRealTimeData(String message){
        log.info("kafka quotes-stock-data consume {}", message);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(message);

            String stockCode = root.path("stockCode").asText();
            String rawJson = root.path("rawJson").asText();

            // 종목코드에 매핑된 productUuid들 가져오기
            Set<String> pieceUuids = mapper.getPieceProductUuidsByStockCode(stockCode);

            // rawJson → DTO 변환
            GetRealTimeQuotesResponseDto dto = GetRealTimeQuotesResponseDto.toDto(stockCode, rawJson);
            String json = new ObjectMapper().writeValueAsString(dto);
            log.info("🔍 직렬화 확인: {}", json);
            log.info("StockCode: {} , ProductUuid: {}", stockCode, pieceUuids);
            // 해당 productUuid에 대해 WebSocket 전송
            for (String pieceUuid : pieceUuids) {
                messagingTemplate.convertAndSend("/topic/quotes." + pieceUuid, dto);
                log.info("📤 WebSocket 전송: /topic/quotes.{} → {}", pieceUuid, dto);
            }

        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패", e);
        }
    }

    @KafkaListener(topics = "execution-stock-data", groupId = "execution-stock-group",  containerFactory = "realTimeDataEventListener")
    public void consumeExecutionRealTimeData(String message){
        log.info("kafka execution-stock-data consume {}", message);
        stockWebSocketHandler.broadcast(StockQuotes.toDto(message));
    }
}