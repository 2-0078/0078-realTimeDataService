package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.kisapi.dto.out.GetPieceMarketPriceResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPieceQuotesResponseDto;

public interface KisApiService {
    GetPieceMarketPriceResponseDto getPieceMarketPrice(String pieceProductUuid);
    GetPieceQuotesResponseDto getPieceQuotes(String pieceProductUuid);
}
