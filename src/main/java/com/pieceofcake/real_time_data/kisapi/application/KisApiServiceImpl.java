package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.kisapi.dto.out.GetIntradayChartListResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPeriodMarketPriceListResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPieceMarketPriceResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPieceQuotesResponseDto;
import com.pieceofcake.real_time_data.kisapi.external.KisApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class KisApiServiceImpl implements KisApiService {

    private final KisApiClient kisApiClient;
    private final RedisTemplate<String, String> redisTemplate;

    private <T> T executeSingleQuery(String pieceProductUuid, Function<String, T> kisApiCall) {
        return kisApiCall.apply(resolveStockCode(pieceProductUuid));
    }

    @Override
    public GetPieceMarketPriceResponseDto getPieceMarketPrice(String pieceProductUuid) {
        return executeSingleQuery(
                pieceProductUuid,
                stockCode -> GetPieceMarketPriceResponseDto.toDto(kisApiClient.getKisStockMarketPriceInfo(stockCode))
        );
    }

    @Override
    public GetPieceQuotesResponseDto getPieceQuotes(String pieceProductUuid) {
        return executeSingleQuery(
                pieceProductUuid,
                stockCode -> GetPieceQuotesResponseDto.toDto(kisApiClient.getKisQuotesInfo(stockCode))
        );
    }

    @Override
    public GetIntradayChartListResponseDto getTodayIntradayChart(String pieceProductUuid, String time) {
        return executeSingleQuery(
                pieceProductUuid,
                stockCode -> GetIntradayChartListResponseDto.toDto(kisApiClient.getKisTodayIntradayChartInfo(stockCode, time))
        );
    }

    @Override
    public GetIntradayChartListResponseDto getDailyIntradayChart(String pieceProductUuid, String time, String date) {
        return executeSingleQuery(
                pieceProductUuid,
                stockCode -> GetIntradayChartListResponseDto.toDto(kisApiClient.getKisDailyIntradayChartInfo(stockCode, time, date))
        );
    }

    @Override
    public GetPeriodMarketPriceListResponseDto getPeriodMarketPrice(String pieceProductUuid, String startDate, String endDate, String divCode) {
        return executeSingleQuery(
                pieceProductUuid,
                stockCode -> GetPeriodMarketPriceListResponseDto.toDto(kisApiClient.getKisPeriodMarketPriceInfo(stockCode, startDate, endDate, divCode))
        );
    }

    public String resolveStockCode(String pieceProductUuid) {
        String redisKey = "piece:stock:" + pieceProductUuid;
        String stockCode = redisTemplate.opsForValue().get(redisKey);
        if (stockCode == null) {
            throw new IllegalArgumentException("Redis 매핑 없음: " + pieceProductUuid);
        }
        return stockCode;
    }
}
