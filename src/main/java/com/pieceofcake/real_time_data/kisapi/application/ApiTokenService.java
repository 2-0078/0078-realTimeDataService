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
        if (currentToken == null || tokenExpiration == null || Instant.now().isAfter(tokenExpiration.minusSeconds(60))) {
            System.out.println("🔄 토큰 갱신 시도");
            refreshToken();
        }
        return currentToken;
    }

    public synchronized void refreshToken() {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("grant_type", "client_credentials");
            requestBody.put("appkey", apiRealAppKey);
            requestBody.put("appsecret", apiRealAppSecretKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<TokenResponse> response = restTemplate.exchange(
                    authUrl,
                    HttpMethod.POST,
                    entity,
                    TokenResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                TokenResponse tokenResponse = response.getBody();
                this.currentToken = tokenResponse.getAccessToken();

                // ✅ 유효시간(초 단위)을 기준으로 만료 시점 계산
                long expiresIn = tokenResponse.getExpiresIn(); // 예: 7776000 (90일)
                this.tokenExpiration = Instant.now().plusSeconds(expiresIn);

                System.out.println("✅ 새 토큰 발급 완료");
                System.out.println("토큰 만료 예정 시각: " + this.tokenExpiration);
            } else {
                System.err.println("❌ 토큰 발급 실패: " + response.getStatusCode());
                this.currentToken = null;
                this.tokenExpiration = null;
            }
        } catch (Exception e) {
            System.err.println("❌ 토큰 발급 중 예외: " + e.getMessage());
            e.printStackTrace();
            this.currentToken = null;
            this.tokenExpiration = null;
        }
    }
}

