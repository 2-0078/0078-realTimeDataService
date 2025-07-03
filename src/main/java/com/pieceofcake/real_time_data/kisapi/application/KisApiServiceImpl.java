package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.kisapi.dto.out.GetIntradayChartListResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPeriodMarketPriceListResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPieceMarketPriceResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPieceQuotesResponseDto;
import com.pieceofcake.real_time_data.kisapi.external.KisApiClient;
import com.pieceofcake.real_time_data.kisapi.mapper.PieceProductStockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class KisApiServiceImpl implements KisApiService {

    private final PieceProductStockMapper pieceProductStockMapper;
    private final KisApiClient kisApiClient;


    private <T> T executeSingleQuery(String pieceProductUuid, Function<String, T> kisApiCall) {
        String stockCode = pieceProductStockMapper.getOrAssignStockCodeForSingleQuery(pieceProductUuid);
        return kisApiCall.apply(stockCode);
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
}
