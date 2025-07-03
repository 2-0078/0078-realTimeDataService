package com.pieceofcake.real_time_data.kisapi.vo.out;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetPeriodMarketPriceResponseVo {
    private String stckBsopDate;    // 주식 영업일자
    private Long stckOprc;  // 주식 시가
    private Long stckClpr;  // 주식 종가
    private Long stckHgpr;  // 주식 최고가
    private Long stckLwpr;  // 주식 최저가
    private Integer acmlVol;    // 누적 거래량
    private Long acmlTrPbmn; // 누적 거래 대금

    @Builder
    public GetPeriodMarketPriceResponseVo(String stckBsopDate, Long stckOprc, Long stckClpr, Long stckHgpr, Long stckLwpr, Integer acmlVol, Long acmlTrPbmn) {
        this.stckBsopDate = stckBsopDate;
        this.stckOprc = stckOprc;
        this.stckClpr = stckClpr;
        this.stckHgpr = stckHgpr;
        this.stckLwpr = stckLwpr;
        this.acmlVol = acmlVol;
        this.acmlTrPbmn = acmlTrPbmn;
    }
}
