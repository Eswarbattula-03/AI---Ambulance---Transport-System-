package com.example.liferescue.dto;

import java.math.BigDecimal;

public class TriagePredictionResponse {
    private String priority;
    private BigDecimal score;
    private String explanation;
    private Double confidence;
    private String modelVersion;

    public TriagePredictionResponse() {}

    public TriagePredictionResponse(String priority, BigDecimal score, String explanation, Double confidence, String modelVersion) {
        this.priority = priority;
        this.score = score;
        this.explanation = explanation;
        this.confidence = confidence;
        this.modelVersion = modelVersion;
    }

    public static TriagePredictionResponseBuilder builder() {
        return new TriagePredictionResponseBuilder();
    }

    public static class TriagePredictionResponseBuilder {
        private String priority;
        private BigDecimal score;
        private String explanation;
        private Double confidence;
        private String modelVersion;

        public TriagePredictionResponseBuilder priority(String priority) { this.priority = priority; return this; }
        public TriagePredictionResponseBuilder score(BigDecimal score) { this.score = score; return this; }
        public TriagePredictionResponseBuilder explanation(String explanation) { this.explanation = explanation; return this; }
        public TriagePredictionResponseBuilder confidence(Double confidence) { this.confidence = confidence; return this; }
        public TriagePredictionResponseBuilder modelVersion(String modelVersion) { this.modelVersion = modelVersion; return this; }

        public TriagePredictionResponse build() {
            return new TriagePredictionResponse(priority, score, explanation, confidence, modelVersion);
        }
    }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
}
