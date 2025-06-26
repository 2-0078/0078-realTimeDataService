package com.pieceofcake.real_time_data.kisapi.vo.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetPieceMarketPriceResponseVo {
    @Schema(description = "현재가(체결가)")
    private Long stckPrpr;
    @Schema(description = "주식 시가")
    private Long stckOprc;
    @Schema(description = "주식 최고가")
    private Long stckHgpr;
    @Schema(description = "주식 최저가")
    private Long stckLwpr;

    @Builder
    public GetPieceMarketPriceResponseVo(Long stckPrpr, Long stckOprc, Long stckHgpr, Long stckLwpr) {
        this.stckPrpr = stckPrpr;
        this.stckOprc = stckOprc;
        this.stckHgpr = stckHgpr;
        this.stckLwpr = stckLwpr;
    }
}
