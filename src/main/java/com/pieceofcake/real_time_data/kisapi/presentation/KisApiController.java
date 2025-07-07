package com.pieceofcake.real_time_data.kisapi.presentation;

import com.pieceofcake.real_time_data.common.entity.BaseResponseEntity;
import com.pieceofcake.real_time_data.kisapi.application.KisApiServiceImpl;
import com.pieceofcake.real_time_data.kisapi.application.sse.KisApiSseEventService;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetIntradayChartListResponseVo;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetPeriodMarketPriceListResponseVo;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetPieceMarketPriceResponseVo;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetPieceQuotesResponseVo;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RequestMapping("/api/v1/kis-api")
@RequiredArgsConstructor
@RestController
public class KisApiController {

    private final KisApiServiceImpl kisApiService;
    private final KisApiSseEventService kisApiSseEventService;

    @Operation(
            summary = "주식 시세 단건 조회",
            description = """
                    한국투자의 현재 시세 정보를 조회합니다.
                    
                    반환되는 정보는 다음과 같습니다:
                    - stckPrpr: 현재가(체결가)
                    - stckOprc: 주식 시가
                    - stckHgpr: 주식 최고가
                    - stckLwpr: 주식 최저가
                    - prdyVrssSign: 전일 대비 부호 (1: 상한, 2: 상승, 3: 보합, 4: 하한, 5: 하락)
                    - prdyVrss: 전일 대비 가격
                    - prdyCrt: 전일 대비율(%)
                    """,
            parameters = {
                    @Parameter(
                            name = "pieceProductUuid",
                            description = "조회할 조각 상품의 UUID",
                            in = ParameterIn.PATH,
                            required = true,
                            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
                    )
            }
    )

    @GetMapping("/market-price/{pieceProductUuid}")
    public BaseResponseEntity<GetPieceMarketPriceResponseVo> getPieceMarketPrice(@PathVariable String pieceProductUuid) {
        return new BaseResponseEntity<>(kisApiService.getPieceMarketPrice(pieceProductUuid).toVo());
    }

    @Operation(summary = "주식 호가 단건 조회")
    @GetMapping("/quotes/{pieceProductUuid}")
    public BaseResponseEntity<GetPieceQuotesResponseVo> getPieceQuotes(@PathVariable String pieceProductUuid) {
        return new BaseResponseEntity<>(kisApiService.getPieceQuotes(pieceProductUuid).toVo());
    }

    @Operation(summary = "주식 당일 분봉 조회")
    @GetMapping("/today-intraday/{pieceProductUuid}")
    public BaseResponseEntity<GetIntradayChartListResponseVo> getTodayIntradayChart(
            @PathVariable String pieceProductUuid,
            @RequestParam String time) {
        return new BaseResponseEntity<>(kisApiService.getTodayIntradayChart(pieceProductUuid, time).toVo());
    }

    @Operation(summary = "주식 일별 분봉 조회")
    @GetMapping("/daily-intraday/{pieceProductUuid}")
    public BaseResponseEntity<GetIntradayChartListResponseVo> getDailyIntradayChart(
            @PathVariable String pieceProductUuid,
            @RequestParam String time,
            @RequestParam String date) {
        return new BaseResponseEntity<>(kisApiService.getDailyIntradayChart(pieceProductUuid, time, date).toVo());
    }

    @Operation(summary = "주식 기간별 시세 조회")
    @GetMapping("/period/{pieceProductUuid}")
    public BaseResponseEntity<GetPeriodMarketPriceListResponseVo> getPeriodMarketPriceInfo(
            @PathVariable String pieceProductUuid,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String divCode) {
        return new BaseResponseEntity<>(kisApiService.getPeriodMarketPrice(pieceProductUuid, startDate, endDate, divCode).toVo());
    }

    @Operation(
            summary = "한국투자 거래 호가 조회 SSE API",
            description = "Server-Sent Events를 사용하여 한국투자의 실시간 호가 정보 업데이트를 스트리밍하는 API입니다.\n\n" +
                    "- path variable로 조각 상품 UUID를 받아 주식코드와 맵핑 후 한국투자 거래의 호가 정보 업데이트 이벤트를 실시간으로 제공합니다.\n" +
                    "- 클라이언트는 이 엔드포인트에 연결하여 가격 변동을 실시간으로 모니터링할 수 있습니다.\n\n" +
                    "- 응답 데이터 스키마:\n\n" +
                    "            - stockcode: mapping된 주식 코드" +
                    "            - askp: 매도 호가 가격 리스트 (체결가 기준 위로 10단계 가격)\n\n" +
                    "              예) [1010, 1015, 1020, ...]\n\n" +
                    "            - bidp: 매수 호가 가격 리스트 (체결가 기준 아래로 10단계 가격)\n\n" +
                    "              예) [1005, 1000, 995, ...]\n\n" +
                    "            - askpRsqn: 각 매도 호가 가격에 대응하는 매도 잔량 리스트\n\n" +
                    "              예) [0, 8, 0, ...]\n\n" +
                    "            - bidRsqn: 각 매수 호가 가격에 대응하는 매수 잔량 리스트\n\n" +
                    "              예) [0, 0, 0, ...]"
    )
    @GetMapping(value = "/sse/quotes-update/{pieceProductUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<GetRealTimeQuotesResponseDto>> streamKisTradeQuotes(
            @PathVariable("pieceProductUuid") String pieceProductUuid) {
        return kisApiSseEventService.getKisQuotesUpdatesByPieceProductUuid(pieceProductUuid)
                .map(event -> ServerSentEvent.<GetRealTimeQuotesResponseDto>builder()
                        .event("quotes-update")
                        .data(event)
                        .build());
    }

    @Operation(
            summary = "한국투자 거래 체결 조회 SSE API",
            description = "Server-Sent Events를 사용하여 한국투다의 실시간 현재가(체결가) 업데이트를 스트리밍하는 API입니다.\n\n" +
                    "- path variable로 조각 상품 UUID를 받아 주식코드와 맵핑 후 한국투자 거래의 현재가 업데이트 이벤트를 실시간으로 제공합니다.\n" +
                    "- 클라이언트는 이 엔드포인트에 연결하여 가격 변동을 실시간으로 모니터링할 수 있습니다.\n\n" +
                    "- 응답 데이터 스키마:\n\n" +
                    "            - stockCode: 주식 종목코드\n\n" +
                    "              예) \"000270\"\n\n" +
                    "            - stckPrpr: 현재가(체결가)\n\n" +
                    "              예) 99100\n\n" +
                    "            - stckOprc: 시가\n\n" +
                    "              예) 98900\n\n" +
                    "            - stckHgpr: 고가\n\n" +
                    "              예) 100000\n\n" +
                    "            - stckLwpr: 저가\n\n" +
                    "              예) 98300\n\n" +
                    "            - cntgVol: 체결 거래량\n\n" +
                    "              예) 1\n\n" +
                    "            - date: 체결 시각\n\n" +
                    "              예) \"2025-07-07T14:49:15\""
    )
    @GetMapping(value = "/sse/market-price-update/{pieceProductUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<GetRealTimeMarketPriceResponseDto>> streamKisTradeMarketPrice(
            @PathVariable("pieceProductUuid") String pieceProductUuid) {
        return kisApiSseEventService.getKisMatchedUpdatesByPieceProductUuid(pieceProductUuid)
                .map(event -> ServerSentEvent.<GetRealTimeMarketPriceResponseDto>builder()
                        .event("market-price-update")
                        .data(event)
                        .build());
    }
}
