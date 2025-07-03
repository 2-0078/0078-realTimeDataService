package com.pieceofcake.real_time_data.kisapi.vo.out;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetPeriodMarketPriceListResponseVo {
    private List<GetPeriodMarketPriceResponseVo> periodMarketPriceList;

    @Builder
    public GetPeriodMarketPriceListResponseVo(List<GetPeriodMarketPriceResponseVo> periodMarketPriceList) {
        this.periodMarketPriceList = periodMarketPriceList;
    }
}
