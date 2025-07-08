package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.kisapi.entity.KisMarketPrice;
import com.pieceofcake.real_time_data.kisapi.entity.KisQuotes;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface KisEntityService {
    Flux<KisMarketPrice> saveKisMarketPrice(List<GetRealTimeMarketPriceResponseDto> dto);

    Mono<KisQuotes> saveKisQuotesInfo(GetRealTimeQuotesResponseDto dto);
}
