package com.pieceofcake.real_time_data.kisapi.application.sse;

import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KisApiWebFluxEventService {

    Flux<GetRealTimeMarketPriceResponseDto> getMarketPricesByPieceProductUuid(String pieceProductUuid);

    Flux<GetRealTimeMarketPriceResponseDto> getNewMarketPricesByPieceProductUuid(String pieceProductUuid);

    Flux<GetRealTimeQuotesResponseDto> getNewQuotesByPieceProductUuid(String pieceProductUuid);

    Mono<String> resolveStockCode(String pieceProductUuid);
}
