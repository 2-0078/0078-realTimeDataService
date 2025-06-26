package com.pieceofcake.real_time_data.stocktest.externalWebSocketClient;

import com.pieceofcake.real_time_data.stocktest.kafka.KafkaStockProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalWebSocketClient {
    private final Map<String, Set<WebSocketSession>> sessionMap = new ConcurrentHashMap<>();
    private final KafkaStockProducer kafkaStockProducer;

    public CompletableFuture<WebSocketSession> connect(String key, String endpoint) {
        WebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<WebSocketSession> future = new CompletableFuture<>();

        client.execute(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                log.info("🔗 연결됨: {}", key);
                sessionMap.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(session);
                future.complete(session);
            }

//            @Override
//            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
//                log.info("📥 수신 [{}]: {}", key, message.getPayload());
//            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                log.info("📥 수신 [{}]: {}", key, message.getPayload());
                webSocketClientHandleMessage(key, message.getPayload().toString());
            }

            @Override public void handleTransportError(WebSocketSession session, Throwable exception) {}

            @Override public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                Set<WebSocketSession> sessions = sessionMap.get(key);
                if (sessions != null) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        sessionMap.remove(key);
                    }
                }

                log.info("🔌 연결 종료됨: {}", key);
            }

            @Override public boolean supportsPartialMessages() { return false; }

        }, String.valueOf(URI.create(endpoint)));

        return future;
    }

    public void sendMessage(String key, String json) {
        Set<WebSocketSession> sessions = sessionMap.get(key);

        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(json));
                        log.info("📤 전송 [{}]: {}", key, json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            log.info("⚠️ 세션 없음 또는 종료됨: {}", key);
        }
    }

    public void disconnect(String key) {
        Set<WebSocketSession> sessions = sessionMap.get(key);
        try {
            if (sessions != null) {
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.close();
                        log.info("🛑 수동 종료: {}", key);
                    }
                }
                sessionMap.remove(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void webSocketClientHandleMessage(String key, String payload) {
        // 여기서 메시지 전처리, 로깅 등 가능

        switch (key) {
            case "stock-executionPrice":
                // 처리 로직
                break;
            case "stock-quotes":
                kafkaStockProducer.sendQuotesRealTimeData(payload);
                break;
            default:
                // 기본 처리
                break;
        }


    }


}