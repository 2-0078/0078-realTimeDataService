package com.pieceofcake.real_time_data.kisapi.vo.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetPieceQuotesResponseVo {
    @Schema(description = "매도호가 리스트")
    private List<Long> askp;
    @Schema(description = "매수호가 리스트")
    private List<Long> bidp;
    @Schema(description = "매도호가 잔량 리스트")
    private List<Integer> askpRsqn;
    @Schema(description = "매수호가 잔량 리스트")
    private List<Integer> bidRsqn;

    @Builder
    public GetPieceQuotesResponseVo(List<Long> askp, List<Long> bidp, List<Integer> askpRsqn, List<Integer> bidRsqn) {
        this.askp = askp;
        this.bidp = bidp;
        this.askpRsqn = askpRsqn;
        this.bidRsqn = bidRsqn;
    }
}
