package com.example.liferescue.dto;

import java.math.BigDecimal;

public class TriagePredictionRequest {
    private String symptoms;
    private BigDecimal pickupLat;
    private BigDecimal pickupLng;

    public TriagePredictionRequest() {}

    public TriagePredictionRequest(String symptoms, BigDecimal pickupLat, BigDecimal pickupLng) {
        this.symptoms = symptoms;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
    }

    public static TriagePredictionRequestBuilder builder() {
        return new TriagePredictionRequestBuilder();
    }

    public static class TriagePredictionRequestBuilder {
        private String symptoms;
        private BigDecimal pickupLat;
        private BigDecimal pickupLng;

        public TriagePredictionRequestBuilder symptoms(String s) { this.symptoms = s; return this; }
        public TriagePredictionRequestBuilder pickupLat(BigDecimal lat) { this.pickupLat = lat; return this; }
        public TriagePredictionRequestBuilder pickupLng(BigDecimal lng) { this.pickupLng = lng; return this; }

        public TriagePredictionRequest build() {
            return new TriagePredictionRequest(symptoms, pickupLat, pickupLng);
        }
    }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public BigDecimal getPickupLat() { return pickupLat; }
    public void setPickupLat(BigDecimal pickupLat) { this.pickupLat = pickupLat; }
    public BigDecimal getPickupLng() { return pickupLng; }
    public void setPickupLng(BigDecimal pickupLng) { this.pickupLng = pickupLng; }
}
