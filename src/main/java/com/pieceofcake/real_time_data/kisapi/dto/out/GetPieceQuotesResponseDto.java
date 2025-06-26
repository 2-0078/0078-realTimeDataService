package com.pieceofcake.real_time_data.kisapi.dto.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetPieceQuotesResponseVo;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GetPieceQuotesResponseDto {
    private List<Long> askp;
    private List<Long> bidp;
    private List<Integer> askpRsqn;
    private List<Integer> bidRsqn;

    @Builder
    public GetPieceQuotesResponseDto(List<Long> askp, List<Long> bidp, List<Integer> askpRsqn, List<Integer> bidRsqn) {
        this.askp = askp;
        this.bidp = bidp;
        this.askpRsqn = askpRsqn;
        this.bidRsqn = bidRsqn;
    }

    public static GetPieceQuotesResponseDto toDto(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            JsonNode output = root.path("output1");

            List<Long> askp = new ArrayList<>();
            List<Long> bidp = new ArrayList<>();
            List<Integer> askpRsqn = new ArrayList<>();
            List<Integer> bidRsqn = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                askp.add(output.path("askp" + i).asLong());
                bidp.add(output.path("bidp" + i).asLong());
                askpRsqn.add(output.path("askp_rsqn" + i).asInt());
                bidRsqn.add(output.path("bidp_rsqn" + i).asInt());
            }

            return GetPieceQuotesResponseDto.builder()
                    .askp(askp)
                    .bidp(bidp)
                    .askpRsqn(askpRsqn)
                    .bidRsqn(bidRsqn)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }

    public GetPieceQuotesResponseVo toVo() {
        return GetPieceQuotesResponseVo.builder()
                .askp(askp)
                .bidp(bidp)
                .askpRsqn(askpRsqn)
                .bidRsqn(bidRsqn)
                .build();
    }
}
