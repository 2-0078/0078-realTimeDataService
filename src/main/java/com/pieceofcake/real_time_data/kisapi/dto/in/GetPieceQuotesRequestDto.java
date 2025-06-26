package com.pieceofcake.real_time_data.kisapi.dto.in;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetPieceQuotesRequestDto {
    private String pieceProductUuid;

    @Builder
    public GetPieceQuotesRequestDto(String pieceProductUuid) {
        this.pieceProductUuid = pieceProductUuid;
    }
}
