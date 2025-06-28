package com.pieceofcake.real_time_data.websocket.handler;


import com.pieceofcake.real_time_data.kisapi.mapper.PieceProductStockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompEventListener {

    private final PieceProductStockMapper mapper;
    private final ConcurrentHashMap<String, Set<String>> sessionToProductMap = new ConcurrentHashMap<>();


    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String subscriptionId = accessor.getSubscriptionId(); // sub-piece-{uuid}
        log.info("🛑 구독 해제 감지: {}", subscriptionId);

        if (subscriptionId != null && subscriptionId.startsWith("sub-piece-")) {
            // "sub-piece-" 다음이 uuid
            String pieceProductUuid = subscriptionId.substring("sub-piece-".length());
            log.info("🗑 매핑 제거 대상 UUID: {}", pieceProductUuid);

            mapper.removeSubscriberForPieceUuid(pieceProductUuid);
        } else {
            log.warn("⚠️ 예상치 못한 subscriptionId 형식: {}", subscriptionId);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        log.info("🔌 클라이언트 연결 종료 감지 - 세션ID: {}", sessionId);

        Set<String> products = sessionToProductMap.get(sessionId);
        if (products != null) {
            for (String productUuid : products) {
                log.info("🛑 연결 종료로 매핑 제거 - session: {}, uuid: {}", sessionId, productUuid);
                mapper.removeSubscriberForPieceUuid(productUuid);
            }
            assert sessionId != null;
            sessionToProductMap.remove(sessionId);
        } else {
            log.info("ℹ️ 연결 종료 감지 - 구독 기록 없음: sessionId={}", sessionId);
        }
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination(); // ex) /topic/piece.product-123

        if (destination != null && destination.startsWith("/topic/quotes.")) {
            String productUuid = destination.substring("/topic/quotes.".length());
            sessionToProductMap
                    .computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet())
                    .add(productUuid);
            log.info("✅ 구독 기록 - sessionId: {}, productUuid: {}", sessionId, productUuid);
        }
    }
}