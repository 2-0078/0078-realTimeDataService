package com.pieceofcake.real_time_data.kisapi.dto.in;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetPieceMarketPriceRequestDto {
    private String pieceProductUuid;

    @Builder
    public GetPieceMarketPriceRequestDto(String pieceProductUuid) {
        this.pieceProductUuid = pieceProductUuid;
    }
}
