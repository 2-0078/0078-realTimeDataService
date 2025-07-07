package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.websocket.externalWebSocketClient.ExternalWebSocketClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KisRealTimeStockServiceImpl implements KisRealTimeStockService {
    private final ExternalWebSocketClient client;
    private final String stockRealTimeURL = "ws://ops.koreainvestment.com:21000";
//    private final String stockRealTimeURL = "ws://ops.koreainvestment.com:31000";

    // 실시간 주식에 사용하는 상위 20개 코드 리스트
    private final List<String> stockCodes = List.of(
            "005930", "000660", "373220", "207940", "005380",
            "005935", "000270", "068270", "005490", "105560");
    //            ,
//            "035420", "006400", "051910", "028260", "055550",
//            "012330", "003670", "035720", "247540", "009830");


//    @Value("${kis.websocket.approval-key}")
@Value("${kis.websocket.real-approval-key}")
    private String realStockApprovalKey; // 발급받은 key

    @PostConstruct
    public void autoReconnectIfMarketOpen() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        DayOfWeek dayOfWeek = nowDateTime.getDayOfWeek();

        LocalTime now = LocalTime.now();
        LocalTime marketOpen = LocalTime.of(23, 55);
        LocalTime marketClose = LocalTime.of(7, 5);

        boolean isWeekday = dayOfWeek != DayOfWeek.SATURDAY;

        boolean inMarketHours;
        if (marketOpen.isBefore(marketClose)) {
            inMarketHours = !now.isBefore(marketOpen) && !now.isAfter(marketClose);
        } else {
            inMarketHours = !now.isBefore(marketOpen) || !now.isAfter(marketClose);
        }

        if (isWeekday && inMarketHours) {
            log.info("🕐 서버 재시작 감지 – 장중 시간(UTC), 자동 재연결 시도");
            connectRealTimeData();
        } else {
            log.info("🕐 서버 재시작 감지 – 장외 시간(UTC), 연결 생략");
        }
    }

    @Scheduled(cron = "0 55 23 * * SUN-THU")
    public void connectRealTimeData() {
        log.info("📡 [START] 실시간 주식 WebSocket 연결 시도 (호가 + 체결)");

        if (alreadyConnected()) {
            log.info("✅ 이미 연결된 상태이므로 재연결 생략");
            return;
        }

        client.connect("stock-realtime", realStockApprovalKey, stockRealTimeURL)
                .thenAccept(session -> {
                    for (String stockCode : stockCodes) {
                        client.subscribeTrKey("stock-realtime", stockCode, "H0STASP0", realStockApprovalKey);  // 호가
                        client.subscribeTrKey("stock-realtime", stockCode, "H0STCNT0", realStockApprovalKey);  // 체결
                    }
                    log.info("✅ [SUBSCRIBE 완료] 호가/체결 {}개 종목", stockCodes.size());
                })
                .exceptionally(ex -> {
                    log.error("❌ WebSocket 연결 실패", ex);
                    return null;
                });
    }

    @Scheduled(cron = "0 5 7 * * MON-FRI")
    public void stopRealTimeSubscription() {
        log.info("📴 [STOP] 실시간 데이터 연결 종료");
        client.disconnect("stock-realtime", realStockApprovalKey);
    }

    private boolean alreadyConnected() {
        return client.isConnected("stock-realtime");
    }
}
