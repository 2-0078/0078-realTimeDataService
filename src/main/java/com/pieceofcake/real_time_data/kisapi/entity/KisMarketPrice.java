package com.pieceofcake.real_time_data.kisapi.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "kis_market_price") // MongoDB 컬렉션 이름
public class KisMarketPrice {

    @Id
    private String id;  // MongoDB는 보통 String 타입의 ObjectId 사용
    private String stockCode;   // 주식코드
    private Long startingPrice; // 시가
    private Long maximumPrice;  // 최고가
    private Long minimumPrice;  // 최저가
    private Long currentPrice;  // 현재가
    private Long tradeQuantity; // 체결 수량
    private LocalDateTime date; // 체결 시간
}
