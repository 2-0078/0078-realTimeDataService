package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.websocket.externalWebSocketClient.ExternalWebSocketClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KisRealTimeKisRealTimeStockServiceImpl implements KisRealTimeStockService {
    private final ExternalWebSocketClient client;
    private final String stockQuotesURL = "ws://ops.koreainvestment.com:31000/tryitout/H0STASP0";
    private final String realStockQuotesURL = "ws://ops.koreainvestment.com:21000/tryitout/H0STASP0";
    private final String stockExecutionPriceURL = "ws://ops.koreainvestment.com:31000/tryitout/H0STCNT0";
    // 실시간 주식에 사용하는 상위 20개 코드 리스트
    private final List<String> stockCodes = List.of(
            "005930", "000660", "373220", "207940", "005380",
            "005935", "000270", "068270", "005490", "105560");
    @Value("${kis.websocket.approval-key}")
    private String stockApprovalKey; // 발급받은 key
    @Value("${kis.websocket.real-approval-key}")
    private String realStockApprovalKey; // 발급받은 key
//            ,
//            "035420", "006400", "051910", "028260", "055550",
//            "012330", "003670", "035720", "247540", "009830");

//    @PostConstruct
//    public void initRealTimeConnection() {
//        System.out.println("✅ 실시간 주식 데이터 WebSocket 연결 시작");
//        connectStockQuoteData();            // 호가
//        connectStockExecutionPriceData();  // 체결
//    }

    @PostConstruct
    public void autoReconnectIfMarketOpen() {
        connectStockQuoteData();
//        LocalTime now = LocalTime.now();
//        LocalTime marketOpen = LocalTime.of(8, 55);
//        LocalTime marketClose = LocalTime.of(19, 35);
//
//        if (!now.isBefore(marketOpen) && !now.isAfter(marketClose)) {
//            log.info("🕐 서버 재시작 감지 – 장중 시간, 자동 재연결 시도");
//            connectStockQuoteData();
//        } else {
//            log.info("🕐 서버 재시작 감지 – 장외 시간, 연결 생략");
//        }
    }

    // ✅ 장 시작: 08시 55분
    @Scheduled(cron = "0 55 8 * * MON-FRI")
    public void connectStockQuoteData() {
        log.info("📡 [START] 실시간 주식 호가 데이터 연결 시도");

        if (alreadyConnected()) {
            log.info("✅ 이미 연결된 상태이므로 재연결 생략");
            return;
        }

        client.connect("stock-quotes", realStockQuotesURL)
                .thenAccept(session -> {
                    for (String stockCode : stockCodes) {
                        client.subscribeTrKey("stock-quotes", stockCode, "H0STASP0", realStockApprovalKey);
                    }
                    log.info("✅ [SUBSCRIBE 완료] {}개 종목", stockCodes.size());
                })
                .exceptionally(ex -> {
                    log.error("❌ WebSocket 연결 실패", ex);
                    return null;
                });
    }

    // ✅ 장 종료: 19시 35분 -> 원래는 15시
    @Scheduled(cron = "0 35 19 * * MON-FRI")
    public void stopStockQuoteSubscription() {
        log.info("📴 [STOP] 실시간 주식 호가 데이터 연결 종료");
        client.disconnect("stock-quotes");
    }

    private boolean alreadyConnected() {
        return client.isConnected("stock-quotes");
    }

//    @Override
//    public void connectStockQuoteData() { // header, body 넣고 socket과 연결
////        client.connect("stock-quotes", stockQuotesURL)
//        client.connect("stock-quotes", realStockQuotesURL)  // 모의
//                .thenAccept(webSocketSession -> {
//                    for(String stockCode:stockCodes){
////                        client.subscribeTrKey("stock-quotes", stockCode, "H0STASP0", stockApprovalKey);   // 모의
//                        client.subscribeTrKey("stock-quotes", stockCode, "H0STASP0", realStockApprovalKey);
//                    }
//                });
//
//    }
//
//    @Override
//    public void connectStockExecutionPriceData() { // header, body 넣고 socket과 연결
//        client.connect("stock-executionPrice", stockExecutionPriceURL)
//                .thenAccept(webSocketSession -> {
////                    for(String stockCode:stockCodes){
//                    client.sendMessage("stock-executionPrice", "{\n" +
//                            "         \"header\":\n" +
//                            "         {\n" +
//                            "                  \"approval_key\": \""+stockApprovalKey+"\",\n" +
//                            "                  \"custtype\":\"P\",\n" +
//                            "                  \"tr_type\":\"1\",\n" +
//                            "                  \"content-type\":\"utf-8\"\n" +
//                            "         },\n" +
//                            "         \"body\":\n" +
//                            "         {\n" +
//                            "                  \"input\":\n" +
//                            "                  {\n" +
//                            "                           \"tr_id\":\"H0STASP0\",\n" +
////                                "                           \"tr_key\":\""+stockCode+"\"\n" + // for문으로 여러개 받을 수 있음
//                            "                           \"tr_key\":\""+"005930"+"\"\n" + // for문으로 여러개 받을 수 있음
//                            "                  }\n" +
//                            "         }\n" +
//                            "}");
////                    }
//                });
//
//    }
}
