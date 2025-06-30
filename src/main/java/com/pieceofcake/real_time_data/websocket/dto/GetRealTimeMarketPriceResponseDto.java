package com.pieceofcake.real_time_data.websocket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Getter
public class GetRealTimeMarketPriceResponseDto {
    private String stockCode;
    private Long stckPrpr;  // 현재가
    private Long stckOprc;  // 시가
    private Long stckHgpr;  // 최고가
    private Long stckLwpr;  // 최저가

    @Builder
    public GetRealTimeMarketPriceResponseDto(String stockCode, Long stckPrpr, Long stckOprc, Long stckHgpr, Long stckLwpr) {
        this.stockCode = stockCode;
        this.stckPrpr = stckPrpr;
        this.stckOprc = stckOprc;
        this.stckHgpr = stckHgpr;
        this.stckLwpr = stckLwpr;
    }

    public static List<GetRealTimeMarketPriceResponseDto> toDto(String stockCode, String rawJson) {
        List<GetRealTimeMarketPriceResponseDto> result = new ArrayList<>();
        String[] parts = rawJson.split("\\|", 4);
        if (parts.length < 4) {
            log.warn("❗잘못된 메시지 구조: {}", rawJson);
            return result;
        }

        String dataCountStr = parts[2];
        int dataCount = Integer.parseInt(dataCountStr);
        String dataPayload = parts[3];

        String[] fields = dataPayload.split("\\^");

        // 예제용: 한 건에 필드가 50개라고 가정 (실제 필드 수로 변경!)
        int fieldsPerData = 46; // 체결 한 건이 가지는 필드 수 (정확한 스펙에 맞게 수정)

        if (fields.length < dataCount * fieldsPerData) {
            log.warn("❗데이터 수와 필드 수 불일치: 기대 {}건, 기대 {}필드, 실제 {}필드", dataCount, dataCount * fieldsPerData, fields.length);
            return result;
        }

        for (int i = 0; i < dataCount; i++) {
            int baseIdx = i * fieldsPerData;

            try {// 종목코드
                Long stckPrpr = parseLong(fields[baseIdx + 2]); // 현재가
                Long stckOprc = parseLong(fields[baseIdx + 7]); // 시가
                Long stckHgpr = parseLong(fields[baseIdx + 8]); // 고가
                Long stckLwpr = parseLong(fields[baseIdx + 9]); // 저가

                GetRealTimeMarketPriceResponseDto dto = GetRealTimeMarketPriceResponseDto.builder()
                        .stockCode(stockCode)
                        .stckPrpr(stckPrpr)
                        .stckOprc(stckOprc)
                        .stckHgpr(stckHgpr)
                        .stckLwpr(stckLwpr)
                        .build();

                result.add(dto);
            } catch (Exception e) {
                log.error("❌ 데이터 파싱 실패 - 건 {}: {}", i, e.getMessage());
            }
        }

        return result;
    }

    private static Long parseLong(String s) {
        try {
            return (s != null && !s.isBlank()) ? Long.parseLong(s) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
