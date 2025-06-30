package com.pieceofcake.real_time_data.websocket.controller;


import com.pieceofcake.real_time_data.kisapi.mapper.PieceProductStockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductStockRegisterController {

    private final PieceProductStockMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트가 STOMP를 통해 실시간 종목 구독 요청을 보낼 때 호출됩니다.
     * 예: /app/register/{pieceProductUuid}
     */
    @MessageMapping("/register-quotes/{pieceProductUuid}")
    public void registerProduct(@DestinationVariable String pieceProductUuid) {
        String stockCode = mapper.getOrAssignStockCode(pieceProductUuid, true);
        log.info("📌 상품 UUID {} → 종목코드 {} 매핑 완료", pieceProductUuid, stockCode);

        // 👉 매핑된 종목코드를 클라이언트에 전송
        String destination = String.format("/topic/register/%s", pieceProductUuid);
        messagingTemplate.convertAndSend(destination, stockCode);
    }

    @MessageMapping("/register-market/{pieceProductUuid}")
    public void registerMarketProduct(@DestinationVariable String pieceProductUuid) {
        String stockCode = mapper.getOrAssignStockCode(pieceProductUuid, false); // market 전용
        log.info("📌 [시세] 상품 UUID {} → 종목코드 {} 매핑 완료", pieceProductUuid, stockCode);

        String destination = String.format("/topic/register/%s", pieceProductUuid);
        messagingTemplate.convertAndSend(destination, stockCode);
    }

}