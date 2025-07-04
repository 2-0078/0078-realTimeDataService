package com.pieceofcake.real_time_data.kisapi.dto.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetPieceMarketPriceResponseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetPieceMarketPriceResponseDto {
    private Long stckPrpr;
    private Long stckOprc;
    private Long stckHgpr;
    private Long stckLwpr;
    private String prdyVrssSign;
    private Long prdyVrss;
    private Double prdyCrt;

    @Builder
    public GetPieceMarketPriceResponseDto(Long stckPrpr, Long stckOprc, Long stckHgpr, Long stckLwpr,
                                          String prdyVrssSign, Long prdyVrss, Double prdyCrt) {
        this.stckPrpr = stckPrpr;
        this.stckOprc = stckOprc;
        this.stckHgpr = stckHgpr;
        this.stckLwpr = stckLwpr;
        this.prdyVrssSign = prdyVrssSign;
        this.prdyVrss = prdyVrss;
        this.prdyCrt = prdyCrt;
    }

    public static GetPieceMarketPriceResponseDto toDto(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            JsonNode output = root.path("output");

            Long stckPrpr = output.path("stck_prpr").asLong();
            Long stckOprc = output.path("stck_oprc").asLong();
            Long stckHgpr = output.path("stck_hgpr").asLong();
            Long stckLwpr = output.path("stck_lwpr").asLong();

            String prdyVrssSign = output.path("prdy_vrss_sign").asText();
            Long prdyVrss = output.path("prdy_vrss").asLong();
            Double prdyCrt = output.path("prdy_ctrt").asDouble();

            return GetPieceMarketPriceResponseDto.builder()
                    .stckPrpr(stckPrpr)
                    .stckOprc(stckOprc)
                    .stckHgpr(stckHgpr)
                    .stckLwpr(stckLwpr)
                    .prdyVrssSign(prdyVrssSign)
                    .prdyVrss(prdyVrss)
                    .prdyCrt(prdyCrt)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }

    public GetPieceMarketPriceResponseVo toVo() {
        return GetPieceMarketPriceResponseVo.builder()
                .stckPrpr(stckPrpr)
                .stckOprc(stckOprc)
                .stckHgpr(stckHgpr)
                .stckLwpr(stckLwpr)
                .prdyVrssSign(prdyVrssSign)
                .prdyVrss(prdyVrss)
                .prdyCrt(prdyCrt)
                .build();
    }
}
