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
    private final Map<String, Set<String>> subscribedKeysMap = new ConcurrentHashMap<>(); // ✅ tr_key 중복 방지용
    private final KafkaStockProducer kafkaStockProducer;

    public CompletableFuture<WebSocketSession> connect(String key, String endpoint) {
        disconnect(key); // ✅ 먼저 끊기

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

    // ✅ 중복 구독 방지 로직 포함
    public void subscribeTrKey(String key, String trKey, String trId, String approvalKey) {
        Set<String> subscribedKeys = subscribedKeysMap.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet());

        if (subscribedKeys.contains(trKey)) {
            log.info("⛔ 이미 구독된 종목 [{}]: {}", key, trKey);
            return;
        }

        String json = buildSubscriptionMessage(trKey, trId, approvalKey);
        sendMessage(key, json);
        subscribedKeys.add(trKey);
        log.info("✅ 구독 완료 [{}]: {}", key, trKey);
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
        Set<String> subscribedKeys = subscribedKeysMap.get(key);

        try {
            if (sessions != null && subscribedKeys != null) {
                for (String trKey : subscribedKeys) {
                    String approvalKey = "your-real-approval-key";  // 필요시 외부에서 주입받도록 변경
                    String trId = "H0STASP0";                        // 고정값이거나 파라미터로 교체 가능
                    String unsubscribeMsg = buildUnsubscribeMessage(trKey, trId, approvalKey);
                    sendMessage(key, unsubscribeMsg);
                    log.info("🚫 [{}] 구독 해제 메시지 전송됨: {}", key, trKey);
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
        Set<String> subscribedKeys = subscribedKeysMap.get(key);
        if (subscribedKeys == null || !subscribedKeys.contains(trKey)) {
            log.info("⚠️ 구독되지 않은 종목 [{}]: {}", key, trKey);
            return;
        }

        String json = buildUnsubscribeMessage(trKey, trId, approvalKey);
        sendMessage(key, json);
        subscribedKeys.remove(trKey);
        log.info("🚫 구독 취소 [{}]: {}", key, trKey);
    }

    public boolean isConnected(String key) {
        Set<WebSocketSession> sessions = sessionMap.get(key);
        return sessions != null && sessions.stream().anyMatch(WebSocketSession::isOpen);
    }



    public void webSocketClientHandleMessage(String key, String payload) {
        // 여기서 메시지 전처리, 로깅 등 가능
        try {
            // JSON일 경우: pingpong 혹은 구독 성공 메시지 필터링
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

            // ─────────────────────────────
            // 실시간 응답 처리 (raw string)
            // ─────────────────────────────
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

            String stockCode = data[0]; // 첫 건의 종목코드

            switch (key) {
                case "stock-quotes":
                    kafkaStockProducer.sendQuotesRealTimeData(payload, stockCode);
                    break;
                case "stock-executionPrice":
//                    kafkaStockProducer.sendExecutionRealTimeData(payload, stockCode);
                    break;
                default:
                    log.warn("❓ 알 수 없는 WebSocket 키: {}", key);
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
                "    \"tr_type\": \"2\",\n" +  // 🔁 구독 해제는 "2"
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