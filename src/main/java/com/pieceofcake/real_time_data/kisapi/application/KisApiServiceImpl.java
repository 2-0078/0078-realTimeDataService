package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.kisapi.dto.out.GetPieceMarketPriceResponseDto;
import com.pieceofcake.real_time_data.kisapi.dto.out.GetPieceQuotesResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class KisApiServiceImpl implements KisApiService {
    private final String stockExecutionPriceURL = "https://openapivts.koreainvestment.com:29443/uapi/domestic-stock/v1/quotations/inquire-price";
    private final String stockQuotesURL = "https://openapivts.koreainvestment.com:29443/uapi/domestic-stock/v1/quotations/inquire-asking-price-exp-ccn";
    @Value("${kis.api.approval-key}")
    private String apiApprovalKey;
    @Value("${kis.app-key}")
    private String apiAppKey;
    @Value("${kis.app-secret-key}")
    private String apiAppSecretKey;

    private final RestTemplate restTemplate;

    @Override
    public GetPieceMarketPriceResponseDto getPieceMarketPrice(String pieceProductUuid) {
        return GetPieceMarketPriceResponseDto.toDto(getStockInfo("005930"));
    }

    @Override
    public GetPieceQuotesResponseDto getPieceQuotes(String pieceProductUuid) {
        return GetPieceQuotesResponseDto.toDto(getQuotesInfo("005930"));
    }

    // 시세 정보 조회
    public String getStockInfo(String inputCode) {
        // 1. 요청 URL 및 쿼리 파라미터 구성
        URI uri = UriComponentsBuilder
                .fromUriString(stockExecutionPriceURL)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_INPUT_ISCD", inputCode)
                .build()
                .encode()
                .toUri();

        // 2. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiApprovalKey); // Authorization: Bearer {token}
        headers.set("appkey", apiAppKey);
        headers.set("appsecret", apiAppSecretKey);
        headers.set("tr_id", "FHKST01010100");
        headers.set("custtype", "P");

        // 3. HTTP 엔티티 구성 (body 없이 header만)
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // 4. GET 요청 전송
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // 5. 결과 처리
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println(response.getBody());
            return response.getBody();
        } else {
            throw new RuntimeException("API 호출 실패: " + response.getStatusCode());
        }
    }

    // 호가 정보 조회
    public String getQuotesInfo(String inputCode) {
        // 1. 요청 URL 및 쿼리 파라미터 구성
        URI uri = UriComponentsBuilder
                .fromUriString(stockQuotesURL)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_INPUT_ISCD", inputCode)
                .build()
                .encode()
                .toUri();

        // 2. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiApprovalKey); // Authorization: Bearer {token}
        headers.set("appkey", apiAppKey);
        headers.set("appsecret", apiAppSecretKey);
        headers.set("tr_id", "FHKST01010200");
        headers.set("custtype", "P");

        // 3. HTTP 엔티티 구성 (body 없이 header만)
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // 4. GET 요청 전송
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // 5. 결과 처리
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println(response.getBody());
            return response.getBody();
        } else {
            throw new RuntimeException("API 호출 실패: " + response.getStatusCode());
        }
    }
}
