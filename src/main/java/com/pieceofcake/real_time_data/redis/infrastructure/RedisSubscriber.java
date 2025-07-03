package com.pieceofcake.real_time_data.redis.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate; // 웹소켓 송출용
    private final ObjectMapper objectMapper;               // JSON 파싱용

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            JsonNode json = objectMapper.readTree(payload);

            if (channel.startsWith("piece.orderbook.")) {
                String pieceUuid = channel.substring("piece.orderbook.".length());
                String destination = "/topic/quotes.internal." + pieceUuid;

                messagingTemplate.convertAndSend(destination, json);
                log.info("[RedisSubscriber] 호가정보 송출: destination={}, payload={}", destination, payload);

            } else if (channel.startsWith("piece.match.")) {
                String pieceUuid = channel.substring("piece.match.".length());
                String destination = "/topic/market-price.internal." + pieceUuid;

                messagingTemplate.convertAndSend(destination, GetRealTimeMarketPriceResponseDto.redisToDto(json));
                log.info("[RedisSubscriber] 체결정보 송출: destination={}, payload={}", destination, payload);

            } else {
                log.warn("[RedisSubscriber] 처리하지 않는 채널 수신: channel={}", channel);
            }
        } catch (Exception e) {
            log.error("[RedisSubscriber] payload 파싱 실패", e);
        }
    }
}
