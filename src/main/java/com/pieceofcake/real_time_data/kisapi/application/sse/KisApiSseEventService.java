package com.pieceofcake.real_time_data.kisapi.application.sse;

import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import reactor.core.publisher.Flux;

public interface KisApiSseEventService {
    Flux<GetRealTimeMarketPriceResponseDto> getKisMatchedUpdatesByPieceProductUuid(String pieceProductUuid);

    Flux<GetRealTimeQuotesResponseDto> getKisQuotesUpdatesByPieceProductUuid(String pieceProductUuid);
}
