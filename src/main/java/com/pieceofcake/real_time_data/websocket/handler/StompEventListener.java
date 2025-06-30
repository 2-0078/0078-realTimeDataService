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
        String subscriptionId = accessor.getSubscriptionId(); // sub-quotes-{uuid} 또는 sub-market-{uuid}
        log.info("🛑 구독 해제 감지: {}", subscriptionId);

        if (subscriptionId == null) {
            log.warn("⚠️ subscriptionId가 null입니다.");
            return;
        }

        if (subscriptionId.startsWith("sub-quotes-")) {
            String pieceProductUuid = subscriptionId.substring("sub-quotes-".length());
            log.info("🗑 [호가] 매핑 제거 대상 UUID: {}", pieceProductUuid);
            mapper.removeSubscriberForPieceUuid(pieceProductUuid, true);

        } else if (subscriptionId.startsWith("sub-market-")) {
            String pieceProductUuid = subscriptionId.substring("sub-market-".length());
            log.info("🗑 [시세] 매핑 제거 대상 UUID: {}", pieceProductUuid);
            mapper.removeSubscriberForPieceUuid(pieceProductUuid, false);

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
                // 두 매퍼 모두에서 제거
                mapper.removeSubscriberForPieceUuid(productUuid, true);
                mapper.removeSubscriberForPieceUuid(productUuid, false);
            }
            sessionToProductMap.remove(sessionId);
        } else {
            log.info("ℹ️ 연결 종료 감지 - 구독 기록 없음: sessionId={}", sessionId);
        }
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination(); // 예: /topic/quotes.{uuid} 또는 /topic/market-price.{uuid}

        if (destination == null) {
            log.warn("⚠️ 구독 destination이 null입니다.");
            return;
        }

        String productUuid = null;
        String subscriptionType = null;
        boolean isQuotes = false;

        if (destination.startsWith("/topic/quotes.")) {
            productUuid = destination.substring("/topic/quotes.".length());
            subscriptionType = "quotes";
            isQuotes = true;
        } else if (destination.startsWith("/topic/market-price.")) {
            productUuid = destination.substring("/topic/market-price.".length());
            subscriptionType = "market";
            isQuotes = false;
        }

        if (productUuid != null && subscriptionType != null) {
            sessionToProductMap
                    .computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet())
                    .add(productUuid);

            log.info("✅ [{}] 구독 기록 - sessionId: {}, productUuid: {}", subscriptionType, sessionId, productUuid);
        } else {
            log.warn("⚠️ 예상치 못한 destination 형식: {}", destination);
        }
    }
}