package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.kisapi.dto.out.GetIntradayChartListResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPeriodMarketPriceListResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPieceMarketPriceResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPieceQuotesResponseDto;

public interface KisApiService {
    GetPieceMarketPriceResponseDto getPieceMarketPrice(String pieceProductUuid);

    GetPieceQuotesResponseDto getPieceQuotes(String pieceProductUuid);

    GetIntradayChartListResponseDto getTodayIntradayChart(String pieceProductUuid, String time);

    GetIntradayChartListResponseDto getDailyIntradayChart(String pieceProductUuid, String time, String date);

    GetPeriodMarketPriceListResponseDto getPeriodMarketPrice(String pieceProductUuid, String startDate, String endDate, String divCode);
}
