package com.alexaytov.ai_hub.ai.token;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientCredentialsTokenGenerator implements TokenGenerator {

    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;

    private String currentToken;
    private long tokenExpirationTime;

    public ClientCredentialsTokenGenerator(String clientId, String clientSecret, String tokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = tokenUrl;
    }

    @Override
    public String generateToken() {
        if (currentToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            return currentToken;
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> responseMap = mapper.readValue(response.getBody(), Map.class);
            currentToken = (String) responseMap.get("access_token");
            int expiresIn = (Integer) responseMap.get("expires_in");

            // Subtracting 60 seconds to account for any delay or clock skew
            tokenExpirationTime = System.currentTimeMillis() + (expiresIn - 60L) * 1000;
        } catch (IOException | ClassCastException e) {
            throw new TokenGenerationFailed("Failed to parse token response", e);
        }

        return currentToken;
    }
}