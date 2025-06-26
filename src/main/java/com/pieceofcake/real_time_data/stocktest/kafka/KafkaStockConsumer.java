package com.pieceofcake.real_time_data.stocktest.kafka;

import com.pieceofcake.real_time_data.stocktest.websocket.StockQuotes;
import com.pieceofcake.real_time_data.stocktest.websocket.StockWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaStockConsumer {
    private final StockWebSocketHandler stockWebSocketHandler;

    @KafkaListener(topics = "stock-data", groupId = "stock-group",  containerFactory = "realTimeDataEventListener")
    public void consumeQuotesRealTimeData(String message){
        log.info("kafka quotes-stock-data consume {}", message);
        stockWebSocketHandler.broadcast(StockQuotes.toDto(message));
    }

    @KafkaListener(topics = "execution-stock-data", groupId = "execution-stock-group",  containerFactory = "realTimeDataEventListener")
    public void consumeExecutionRealTimeData(String message){
        log.info("kafka execution-stock-data consume {}", message);
        stockWebSocketHandler.broadcast(StockQuotes.toDto(message));
    }
}