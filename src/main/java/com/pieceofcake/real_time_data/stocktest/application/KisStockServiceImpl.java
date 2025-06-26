package com.pieceofcake.real_time_data.stocktest.application;

import com.pieceofcake.real_time_data.stocktest.externalWebSocketClient.ExternalWebSocketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KisStockServiceImpl implements StockService{
    private final ExternalWebSocketClient client;
    private final String stockQuotesURL = "ws://ops.koreainvestment.com:31000/tryitout/H0STASP0";
    private final String stockExecutionPriceURL = "ws://ops.koreainvestment.com:31000/tryitout/H0STCNT0";
    @Value("${kis.websocket.approval-key}")
    private String stockApprovalKey; // 발급받은 key

    // 실시간 주식에 사용하는 상위 20개 코드 리스트
    private final List<String> stockCodes = List.of(
            "005930", "000660", "373220", "207940", "005380",
            "005935", "000270", "068270", "005490", "105560",
            "035420", "006400", "051910", "028260", "055550",
            "012330", "003670", "035720", "247540", "009830");


    @Override
    public void connectStockQuoteData() { // header, body 넣고 socket과 연결
        client.connect("stock-quotes", stockQuotesURL)
                .thenAccept(webSocketSession -> {
//                    for(String stockCode:stockCodes){
                        client.sendMessage("stock-quotes", "{\n" +
                                "         \"header\":\n" +
                                "         {\n" +
                                "                  \"approval_key\": \""+stockApprovalKey+"\",\n" +
                                "                  \"custtype\":\"P\",\n" +
                                "                  \"tr_type\":\"1\",\n" +
                                "                  \"content-type\":\"utf-8\"\n" +
                                "         },\n" +
                                "         \"body\":\n" +
                                "         {\n" +
                                "                  \"input\":\n" +
                                "                  {\n" +
                                "                           \"tr_id\":\"H0STASP0\",\n" +
//                                "                           \"tr_key\":\""+stockCode+"\"\n" + // for문으로 여러개 받을 수 있음
                                "                           \"tr_key\":\""+"005930"+"\"\n" + // for문으로 여러개 받을 수 있음
                                "                  }\n" +
                                "         }\n" +
                                "}");
//                    }
                });

    }

    @Override
    public void connectStockExecutionPriceData() { // header, body 넣고 socket과 연결
        client.connect("stock-executionPrice", stockExecutionPriceURL)
                .thenAccept(webSocketSession -> {
//                    for(String stockCode:stockCodes){
                    client.sendMessage("stock-executionPrice", "{\n" +
                            "         \"header\":\n" +
                            "         {\n" +
                            "                  \"approval_key\": \""+stockApprovalKey+"\",\n" +
                            "                  \"custtype\":\"P\",\n" +
                            "                  \"tr_type\":\"1\",\n" +
                            "                  \"content-type\":\"utf-8\"\n" +
                            "         },\n" +
                            "         \"body\":\n" +
                            "         {\n" +
                            "                  \"input\":\n" +
                            "                  {\n" +
                            "                           \"tr_id\":\"H0STASP0\",\n" +
//                                "                           \"tr_key\":\""+stockCode+"\"\n" + // for문으로 여러개 받을 수 있음
                            "                           \"tr_key\":\""+"005930"+"\"\n" + // for문으로 여러개 받을 수 있음
                            "                  }\n" +
                            "         }\n" +
                            "}");
//                    }
                });

    }
}
