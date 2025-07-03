package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.kisapi.dto.out.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiTokenService {

    @Value("${kis.real-app-key}")
    private String apiRealAppKey;
    @Value("${kis.real-app-secret-key}")
    private String apiRealAppSecretKey;

    private String authUrl = "https://openapi.koreainvestment.com:9443/oauth2/tokenP";

    private final RestTemplate restTemplate;

    private String currentToken;
    private Instant tokenExpiration;

    public synchronized String getValidToken() {
        if (currentToken == null || Instant.now().isAfter(tokenExpiration.minusSeconds(60))) {
            refreshToken();
        }
        return currentToken;
    }

    public void refreshToken() {
        // 요청 Body 생성
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("appkey", apiRealAppKey);
        requestBody.put("appsecret", apiRealAppSecretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // 또는 FORM 방식이면 MediaType.APPLICATION_FORM_URLENCODED

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                authUrl,
                HttpMethod.POST,
                entity,
                TokenResponse.class
        );

        TokenResponse tokenResponse = response.getBody();
        this.currentToken = tokenResponse.getAccessToken();

        // 만료 시간 계산 (expires_in은 초 단위)
        this.tokenExpiration = Instant.now().plusSeconds(tokenResponse.getExpiresIn());
    }
}

