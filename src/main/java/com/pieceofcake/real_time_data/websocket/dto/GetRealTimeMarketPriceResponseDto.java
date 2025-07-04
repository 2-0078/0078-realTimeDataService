package com.pieceofcake.real_time_data.websocket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.pieceofcake.real_time_data.kisapi.entity.KisMarketPrice;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Long cntgVol;   // 체결 거래량
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date; // 체결 날짜&시간

    @Builder
    public GetRealTimeMarketPriceResponseDto(String stockCode, Long stckPrpr, Long stckOprc, Long stckHgpr,
                                             Long stckLwpr, Long cntgVol, LocalDateTime date) {
        this.stockCode = stockCode;
        this.stckPrpr = stckPrpr;
        this.stckOprc = stckOprc;
        this.stckHgpr = stckHgpr;
        this.stckLwpr = stckLwpr;
        this.cntgVol = cntgVol;
        this.date = date;
    }

    public static List<GetRealTimeMarketPriceResponseDto> redisToDto(JsonNode json) {
        List<GetRealTimeMarketPriceResponseDto> result = new ArrayList<>();

        long piecePrice = json.get("piecePrice").asLong();

        GetRealTimeMarketPriceResponseDto dto = GetRealTimeMarketPriceResponseDto.builder()
                .stckPrpr(piecePrice)
                .build();

        result.add(dto);
        return result;
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
                LocalDateTime stckCntgHour = parseTradeTime(fields[baseIdx + 1]);
                Long stckPrpr = parseLong(fields[baseIdx + 2]); // 현재가
                Long stckOprc = parseLong(fields[baseIdx + 7]); // 시가
                Long stckHgpr = parseLong(fields[baseIdx + 8]); // 고가
                Long stckLwpr = parseLong(fields[baseIdx + 9]); // 저가
                Long cntgVol = parseLong(fields[baseIdx + 12]);  // 체결 거래량

                GetRealTimeMarketPriceResponseDto dto = GetRealTimeMarketPriceResponseDto.builder()
                        .stockCode(stockCode)
                        .stckPrpr(stckPrpr)
                        .stckOprc(stckOprc)
                        .stckHgpr(stckHgpr)
                        .stckLwpr(stckLwpr)
                        .cntgVol(cntgVol)
                        .date(stckCntgHour)
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

    /**
     * 체결시간 문자열(STCK_CNTG_HOUR)을 현재 날짜와 합쳐 LocalDateTime으로 변환
     * 예) "103339" -> 오늘 날짜의 10:33:39
     */
    public static LocalDateTime parseTradeTime(String stckCntgHour) {
        if (stckCntgHour == null || stckCntgHour.length() != 6) {
            throw new IllegalArgumentException("체결 시간 문자열은 6자리여야 합니다. 예: '103339'");
        }

        int hour = Integer.parseInt(stckCntgHour.substring(0, 2));
        int minute = Integer.parseInt(stckCntgHour.substring(2, 4));
        int second = Integer.parseInt(stckCntgHour.substring(4, 6));

        LocalDate today = LocalDate.now();

        return today.atTime(hour, minute, second);
    }

    public KisMarketPrice toEntity() {
        return KisMarketPrice.builder()
                .stockCode(stockCode)
                .startingPrice(stckOprc)
                .maximumPrice(stckHgpr)
                .minimumPrice(stckLwpr)
                .currentPrice(stckPrpr)
                .tradeQuantity(cntgVol)
                .date(date)
                .build();
    }
}
