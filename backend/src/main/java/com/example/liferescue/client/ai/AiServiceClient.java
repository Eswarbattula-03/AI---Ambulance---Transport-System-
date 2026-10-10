package com.example.liferescue.client.ai;

import com.example.liferescue.dto.TriagePredictionRequest;
import com.example.liferescue.dto.TriagePredictionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;

@Component
public class AiServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AiServiceClient.class);
    private final RestTemplate restTemplate;

    @Value("${app.ai-service.url:http://localhost:8000}")
    private String aiServiceUrl;

    public AiServiceClient(RestTemplateBuilder builder,
                           @Value("${app.ai-service.connect-timeout-ms:3000}") int connectTimeout,
                           @Value("${app.ai-service.read-timeout-ms:5000}") int readTimeout) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    public TriagePredictionResponse predictTriage(String symptoms, BigDecimal lat, BigDecimal lng) {
        try {
            String endpoint = aiServiceUrl + "/api/triage/predict";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            TriagePredictionRequest payload = TriagePredictionRequest.builder()
                    .symptoms(symptoms)
                    .pickupLat(lat)
                    .pickupLng(lng)
                    .build();

            HttpEntity<TriagePredictionRequest> entity = new HttpEntity<>(payload, headers);
            TriagePredictionResponse response = restTemplate.postForObject(endpoint, entity, TriagePredictionResponse.class);

            if (response != null) {
                log.info("AI Triage Prediction Success: Priority={}, Score={}", response.getPriority(), response.getScore());
                return response;
            }
        } catch (Exception ex) {
            log.warn("AI Microservice call failed: {}. Utilizing transparent rule-based fallback.", ex.getMessage());
        }

        // Fallback transparent rule-based triage logic if Python service is unavailable
        return fallbackTriage(symptoms);
    }

    private TriagePredictionResponse fallbackTriage(String symptoms) {
        String lower = symptoms == null ? "" : symptoms.toLowerCase();
        String priority = "MEDIUM";
        BigDecimal score = new BigDecimal("50.00");
        String explanation = "Evaluated via fallback rule engine (AI service offline).";

        if (lower.contains("chest pain") || lower.contains("cardiac") || lower.contains("unconscious") || lower.contains("heart attack") || lower.contains("stopped breathing")) {
            priority = "CRITICAL";
            score = new BigDecimal("95.00");
            explanation = "Critical condition identified via emergency symptom keyword matching.";
        } else if (lower.contains("breathing") || lower.contains("bleeding") || lower.contains("fracture") || lower.contains("severe")) {
            priority = "HIGH";
            score = new BigDecimal("75.00");
            explanation = "High priority symptoms identified requiring urgent transport.";
        } else if (lower.contains("fever") || lower.contains("cold") || lower.contains("mild")) {
            priority = "LOW";
            score = new BigDecimal("25.00");
            explanation = "Non-life-threatening symptoms classified as low priority.";
        }

        return TriagePredictionResponse.builder()
                .priority(priority)
                .score(score)
                .explanation(explanation)
                .confidence(0.85)
                .modelVersion("FALLBACK_RULE_1.0")
                .build();
    }
}
