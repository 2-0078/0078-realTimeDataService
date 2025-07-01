package com.pieceofcake.real_time_data.websocket.externalWebSocketClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.real_time_data.kafka.KafkaStockProducer;
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
    // ✅ key → set of trKey|trId로 구독 관리
    private final Map<String, Set<String>> subscribedKeysMap = new ConcurrentHashMap<>();
    private final KafkaStockProducer kafkaStockProducer;

    public CompletableFuture<WebSocketSession> connect(String key, String approvalKey, String endpoint) {
        if (isConnected(key)) {
            log.info("✅ 이미 연결된 상태 [{}], 재연결 생략", key);
            return CompletableFuture.completedFuture(null);
        }
        disconnect(key, approvalKey);

        WebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<WebSocketSession> future = new CompletableFuture<>();

        client.execute(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                log.info("🔗 연결됨: {}", key);
                sessionMap.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(session);
                future.complete(session);
            }

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
                    if (sessions.isEmpty()) sessionMap.remove(key);
                }
                log.info("🔌 연결 종료됨: {}", key);
            }

            @Override public boolean supportsPartialMessages() { return false; }
        }, String.valueOf(URI.create(endpoint)));

        return future;
    }

    public void subscribeTrKey(String key, String trKey, String trId, String approvalKey) {
        String uniqueKey = trKey + "|" + trId; // ✅ trKey와 trId 조합으로 관리
        Set<String> subscribedKeys = subscribedKeysMap.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet());

        if (subscribedKeys.contains(uniqueKey)) {
            log.info("⛔ 이미 구독된 종목 [{}]: {}", key, uniqueKey);
            return;
        }

        String json = buildSubscriptionMessage(trKey, trId, approvalKey);
        sendMessage(key, json);
        subscribedKeys.add(uniqueKey);
        log.info("✅ 구독 완료 [{}]: {}", key, uniqueKey);
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
                        log.error("❌ 메시지 전송 실패", e);
                    }
                }
            }
        } else {
            log.info("⚠️ 세션 없음 또는 종료됨: {}", key);
        }
    }

    public void disconnect(String key, String approvalKey) {
        Set<WebSocketSession> sessions = sessionMap.get(key);
        Set<String> subscribedKeys = subscribedKeysMap.get(key);

        try {
            if (sessions != null && subscribedKeys != null) {
                for (String uniqueKey : subscribedKeys) {
                    String[] parts = uniqueKey.split("\\|");
                    String trKey = parts[0];
                    String trId = parts[1];
                    String unsubscribeMsg = buildUnsubscribeMessage(trKey, trId, approvalKey);
                    sendMessage(key, unsubscribeMsg);
                    log.info("🚫 [{}] 구독 해제 메시지 전송됨: {}", key, uniqueKey);
                }
                subscribedKeys.clear();
            }

            if (sessions != null) {
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.close();
                        log.info("🛑 세션 종료: {}", key);
                    }
                }
                sessionMap.remove(key);
            }
        } catch (IOException e) {
            log.error("❌ WebSocket 종료 중 오류", e);
        }
    }

    public void unsubscribeTrKey(String key, String trKey, String trId, String approvalKey) {
        String uniqueKey = trKey + "|" + trId;
        Set<String> subscribedKeys = subscribedKeysMap.get(key);

        if (subscribedKeys == null || !subscribedKeys.contains(uniqueKey)) {
            log.info("⚠️ 구독되지 않은 종목 [{}]: {}", key, uniqueKey);
            return;
        }

        String json = buildUnsubscribeMessage(trKey, trId, approvalKey);
        sendMessage(key, json);
        subscribedKeys.remove(uniqueKey);
        log.info("🚫 구독 취소 [{}]: {}", key, uniqueKey);
    }

    public boolean isConnected(String key) {
        Set<WebSocketSession> sessions = sessionMap.get(key);
        return sessions != null && sessions.stream().anyMatch(WebSocketSession::isOpen);
    }

    public void webSocketClientHandleMessage(String key, String payload) {
        try {
            if (payload.startsWith("{")) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(payload);
                String trId = root.path("header").path("tr_id").asText();

                if ("PINGPONG".equals(trId)) {
                    log.info("💤 PINGPONG 무시");
                    return;
                }
                if ("OPSP0000".equals(root.path("body").path("msg_cd").asText())) {
                    log.info("✅ 구독 성공 메시지 무시");
                    return;
                }
            }

            String[] parts = payload.split("\\|");
            if (parts.length < 4) {
                log.warn("❗ 잘못된 메시지 구조: {}", payload);
                return;
            }

            String[] data = parts[3].split("\\^");
            if (data.length < 1) {
                log.warn("❗ 데이터 필드 부족: {}", payload);
                return;
            }

            String stockCode = data[0];

            // 🔑 tr_id로 구분해서 분기
            String trIdFromMessage = parts[1]; // parts[1]이 트랜잭션 ID ex) H0STASP0 or H0STCNT0
            switch (trIdFromMessage) {
                case "H0STASP0": // 호가
                    kafkaStockProducer.sendQuotesRealTimeData(payload, stockCode);
                    break;
                case "H0STCNT0": // 체결가
                    kafkaStockProducer.sendMarketPriceRealTimeData(payload, stockCode);
                    break;
                default:
                    log.warn("❓ 알 수 없는 tr_id: {}", trIdFromMessage);
                    break;
            }

        } catch (Exception e) {
            log.error("❌ WebSocket 수신 메시지 처리 실패", e);
        }
    }

    private String buildSubscriptionMessage(String trKey, String trId, String approvalKey) {
        return "{\n" +
                "  \"header\": {\n" +
                "    \"approval_key\": \"" + approvalKey + "\",\n" +
                "    \"custtype\": \"P\",\n" +
                "    \"tr_type\": \"1\",\n" +
                "    \"content-type\": \"utf-8\"\n" +
                "  },\n" +
                "  \"body\": {\n" +
                "    \"input\": {\n" +
                "      \"tr_id\": \"" + trId + "\",\n" +
                "      \"tr_key\": \"" + trKey + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    private String buildUnsubscribeMessage(String trKey, String trId, String approvalKey) {
        return "{\n" +
                "  \"header\": {\n" +
                "    \"approval_key\": \"" + approvalKey + "\",\n" +
                "    \"custtype\": \"P\",\n" +
                "    \"tr_type\": \"2\",\n" +
                "    \"content-type\": \"utf-8\"\n" +
                "  },\n" +
                "  \"body\": {\n" +
                "    \"input\": {\n" +
                "      \"tr_id\": \"" + trId + "\",\n" +
                "      \"tr_key\": \"" + trKey + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }
}