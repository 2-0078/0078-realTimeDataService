package com.pieceofcake.real_time_data.kisapi.presentation;

import com.pieceofcake.real_time_data.common.entity.BaseResponseEntity;
import com.pieceofcake.real_time_data.common.entity.BaseResponseStatus;
import com.pieceofcake.real_time_data.kisapi.application.KisApiService;
import com.pieceofcake.real_time_data.kisapi.application.KisApiServiceImpl;
import com.pieceofcake.real_time_data.kisapi.dto.in.GetPieceMarketPriceRequestDto;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetIntradayChartListResponseVo;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetPeriodMarketPriceListResponseVo;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetPieceMarketPriceResponseVo;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetPieceQuotesResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/kis-api")
@RequiredArgsConstructor
@RestController
public class KisApiController {

    private final KisApiServiceImpl kisApiService;

    @Operation(summary = "주식 시세 단건 조회")
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
        return new BaseResponseEntity<>(kisApiService.getPeriodMarketPrice(pieceProductUuid,startDate, endDate, divCode).toVo());
    }
}
