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
    @Schema(description = "전일 대비 부호 (1:상승, 2:하락, 3:보합)")
    private String prdyVrssSign;
    @Schema(description = "전일 대비 가격 (절대값)")
    private Long prdyVrss;
    @Schema(description = "전일 대비율(%)")
    private Double prdyCrt;

    @Builder
    public GetPieceMarketPriceResponseVo(Long stckPrpr, Long stckOprc, Long stckHgpr, Long stckLwpr,
                                         String prdyVrssSign, Long prdyVrss, Double prdyCrt) {
        this.stckPrpr = stckPrpr;
        this.stckOprc = stckOprc;
        this.stckHgpr = stckHgpr;
        this.stckLwpr = stckLwpr;
        this.prdyVrssSign = prdyVrssSign;
        this.prdyVrss = prdyVrss;
        this.prdyCrt = prdyCrt;
    }
}
