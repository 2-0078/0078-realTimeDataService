package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;

import java.util.List;

public interface KisMarketPriceEntityService {
    void saveKisMarketPrice(List<GetRealTimeMarketPriceResponseDto> dto);
}
