package com.pieceofcake.real_time_data.kisapi.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "kis_quotes_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KisQuotes {
    @Id
    private String id;

    private String stockCode;

    private List<Long> askp;       // 매도 호가 가격들
    private List<Long> bidp;       // 매수 호가 가격들
    private List<Integer> askpRsqn; // 매도 호가 잔량
    private List<Integer> bidRsqn;  // 매수 호가 잔량
}
