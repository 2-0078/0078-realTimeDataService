package com.pieceofcake.real_time_data.stocktest.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class StockWebSocketHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<WebSocketSession, Set<Integer>> clientFilters = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Set<String>> stockClientFilters = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    // 새 연결이 들어오면 세션 저장
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        clientFilters.put(session, new HashSet<>());
        log.info("✅ {} 연결 성공", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage message) throws Exception {    // 서버로 메시지를 보낼 때마다 호출
        JsonNode node = mapper.readTree(message.getPayload().toString());
        if ("subscribe".equals(node.get("type").asText())) {
            Set<String> codes = new HashSet<>();
            for (JsonNode codeNode : node.get("codes")) {
                codes.add(codeNode.asText());
            }
            stockClientFilters.put(session, codes);
            log.info("✅ {} 구독 코드 등록됨: {}", session.getId(), codes);
        }
    }

    // 연결 해제되면 세션 제거
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        stockClientFilters.remove(session);
    }

    // Kafka Consumer가 이 메서드를 호출하여 모든 세션에 브로드캐스트
    public void broadcast(StockQuotes data) {
        try {
            String json = new ObjectMapper().writeValueAsString(data);
            String code = data.getMksc_shrn_iscd(); // 종목 코드

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    Set<String> subscribedCodes = stockClientFilters.get(session);

                    // 필터 자체를 안한경우, 전체 허용, 명시적 필터
                    if (!stockClientFilters.containsKey(session) || subscribedCodes == null || subscribedCodes.contains(code)) {
                        session.sendMessage(new TextMessage(json));
                        log.info("📤 [{}] 전송됨 -> {}", code, session.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ broadcast 실패", e);
        }
    }

    // Kafka Consumer가 이 메서드를 호출하여 모든 세션에 브로드캐스트
    public void broadcast(StockExecutions data) {
        try {
            String json = new ObjectMapper().writeValueAsString(data);
            String code = data.getMksc_shrn_iscd(); // 종목 코드

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    Set<String> subscribedCodes = stockClientFilters.get(session);

                    // 필터 자체를 안한경우, 전체 허용, 명시적 필터
                    if (!stockClientFilters.containsKey(session) || subscribedCodes == null || subscribedCodes.contains(code)) {
                        session.sendMessage(new TextMessage(json));
                        log.info("📤 [{}] 전송됨 -> {}", code, session.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ broadcast 실패", e);
        }
    }

    // Kafka Consumer가 이 메서드를 호출하여 모든 세션에 브로드캐스트
    public void broadcast(String data) {
        try {
            String json = new ObjectMapper().writeValueAsString(data);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(data));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}