package com.pieceofcake.real_time_data.kisapi.external;

import com.pieceofcake.real_time_data.kisapi.application.ApiTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class KisApiClient {
    private final RestTemplate restTemplate;
    private final ApiTokenService apiTokenService;

    private final String stockMarketPriceURL = "https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-price";
    private final String stockQuotesURL = "https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-asking-price-exp-ccn";
    private final String todayIntradayUrl = "https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-time-itemchartprice";
    private final String dailyIntradayUrl = "https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-time-dailychartprice";
    private final String periodMarketPriceUrl = "https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice";

    @Value("${kis.real-app-key}")
    private String apiRealAppKey;
    @Value("${kis.real-app-secret-key}")
    private String apiRealAppSecretKey;

    // 시세 정보 조회
    public String getKisStockMarketPriceInfo(String inputCode) {
        // 1. 요청 URL 및 쿼리 파라미터 구성
        URI uri = UriComponentsBuilder
                .fromUriString(stockMarketPriceURL)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_INPUT_ISCD", inputCode)
                .build()
                .encode()
                .toUri();

        // 2. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiTokenService.getValidToken()); // Authorization: Bearer {token}
        headers.set("appkey", apiRealAppKey);
        headers.set("appsecret", apiRealAppSecretKey);
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
    public String getKisQuotesInfo(String inputCode) {
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
        headers.setBearerAuth(apiTokenService.getValidToken()); // Authorization: Bearer {token}
        headers.set("appkey", apiRealAppKey);
        headers.set("appsecret", apiRealAppSecretKey);
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

    // 주식 당일 분봉 데이터 조회
    public String getKisTodayIntradayChartInfo(String inputCode, String inputHour) {
        // 1. 요청 URL 및 쿼리 파라미터 구성
        URI uri = UriComponentsBuilder
                .fromUriString(todayIntradayUrl)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")      // KRX: KOSPI
                .queryParam("FID_INPUT_ISCD", inputCode)         // 종목코드 (예: 005930)
                .queryParam("FID_INPUT_HOUR_1", inputHour)       // 조회 시작 시간 (예: 090000)
                .queryParam("FID_PW_DATA_INCU_YN", "Y")          // 과거 데이터 포함 여부
                .queryParam("FID_ETC_CLS_CODE", "")            // 기타 구분 코드
                .build()
                .encode()
                .toUri();

        // 2. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiTokenService.getValidToken()); // Authorization: Bearer {token}
        headers.set("appkey", apiRealAppKey);
        headers.set("appsecret", apiRealAppSecretKey);
        headers.set("tr_id", "FHKST03010200"); // 분봉 조회용 tr_id
        headers.set("custtype", "P");          // 고객 타입 (P: 개인, B: 법인)

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
            throw new RuntimeException("분봉 데이터 API 호출 실패: " + response.getStatusCode() +
                    ", body: " + response.getBody());
        }
    }

    // 주식 일별 분봉 데이터 조회
    public String getKisDailyIntradayChartInfo(String inputCode, String inputHour, String inputDate) {
        // 1. 요청 URL 및 쿼리 파라미터 구성
        URI uri = UriComponentsBuilder
                .fromUriString(dailyIntradayUrl)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")      // KRX: KOSPI
                .queryParam("FID_INPUT_ISCD", inputCode)         // 종목코드 (예: 005930)
                .queryParam("FID_INPUT_HOUR_1", inputHour)       // 조회 시작 시간 (예: 090000)
                .queryParam("FID_INPUT_DATE_1", inputDate)       // 조회 시작 날짜 (예: 20250630)
                .queryParam("FID_PW_DATA_INCU_YN", "Y")          // 과거 데이터 포함 여부
                .queryParam("FID_FAKE_TICK_INCU_YN", "N")            // 기타 구분 코드
                .build()
                .encode()
                .toUri();

        // 2. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiTokenService.getValidToken()); // Authorization: Bearer {token}
        headers.set("appkey", apiRealAppKey);
        headers.set("appsecret", apiRealAppSecretKey);
        headers.set("tr_id", "FHKST03010230"); // 분봉 조회용 tr_id
        headers.set("custtype", "P");          // 고객 타입 (P: 개인, B: 법인)

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
            System.out.println("[성공]: " + response.getBody());
            return response.getBody();
        } else {
            throw new RuntimeException("분봉 데이터 API 호출 실패: " + response.getStatusCode() +
                    ", body: " + response.getBody());
        }
    }

    // 기간별 시세 데이터 조회
    public String getKisPeriodMarketPriceInfo(String inputCode, String startDate, String endDate, String divCode) {
        // 1. 요청 URL 및 쿼리 파라미터 구성
        URI uri = UriComponentsBuilder
                .fromUriString(periodMarketPriceUrl)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_INPUT_ISCD", inputCode)         // 종목코드 (예: 005930)
                .queryParam("FID_INPUT_DATE_1", startDate)       // 조회 시작 시작일자 (예: 20250630)
                .queryParam("FID_INPUT_DATE_2", endDate)       // 조회 시작 종료일자 (예: 20250630)
                .queryParam("FID_PERIOD_DIV_CODE", divCode)          // 기간분류코드(D:일봉 W:주봉, M:월봉, Y:년봉)
                .queryParam("FID_ORG_ADJ_PRC", "1")            // 수정주가(0)/원주가(1) 조회 여부
                .build()
                .encode()
                .toUri();

        // 2. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiTokenService.getValidToken()); // Authorization: Bearer {token}
        headers.set("appkey", apiRealAppKey);
        headers.set("appsecret", apiRealAppSecretKey);
        headers.set("tr_id", "FHKST03010100"); // 분봉 조회용 tr_id
        headers.set("custtype", "P");          // 고객 타입 (P: 개인, B: 법인)

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
            System.out.println("[성공]: " + response.getBody());
            return response.getBody();
        } else {
            throw new RuntimeException("분봉 데이터 API 호출 실패: " + response.getStatusCode() +
                    ", body: " + response.getBody());
        }
    }
}
