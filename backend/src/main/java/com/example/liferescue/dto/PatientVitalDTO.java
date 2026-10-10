package com.example.liferescue.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PatientVitalDTO {

    private Long id;
    private Long emergencyRequestId;
    private Integer heartRate;
    private Integer bpSystolic;
    private Integer bpDiastolic;
    private Integer spo2;
    private BigDecimal temperature;
    private LocalDateTime loggedAt;

    public PatientVitalDTO() {}

    public PatientVitalDTO(Long id, Long emergencyRequestId, Integer heartRate, Integer bpSystolic, Integer bpDiastolic, Integer spo2, BigDecimal temperature, LocalDateTime loggedAt) {
        this.id = id;
        this.emergencyRequestId = emergencyRequestId;
        this.heartRate = heartRate;
        this.bpSystolic = bpSystolic;
        this.bpDiastolic = bpDiastolic;
        this.spo2 = spo2;
        this.temperature = temperature;
        this.loggedAt = loggedAt;
    }

    public static PatientVitalDTOBuilder builder() {
        return new PatientVitalDTOBuilder();
    }

    public static class PatientVitalDTOBuilder {
        private Long id;
        private Long emergencyRequestId;
        private Integer heartRate;
        private Integer bpSystolic;
        private Integer bpDiastolic;
        private Integer spo2;
        private BigDecimal temperature;
        private LocalDateTime loggedAt;

        public PatientVitalDTOBuilder id(Long id) { this.id = id; return this; }
        public PatientVitalDTOBuilder emergencyRequestId(Long reqId) { this.emergencyRequestId = reqId; return this; }
        public PatientVitalDTOBuilder heartRate(Integer hr) { this.heartRate = hr; return this; }
        public PatientVitalDTOBuilder bpSystolic(Integer sys) { this.bpSystolic = sys; return this; }
        public PatientVitalDTOBuilder bpDiastolic(Integer dia) { this.bpDiastolic = dia; return this; }
        public PatientVitalDTOBuilder spo2(Integer spo2) { this.spo2 = spo2; return this; }
        public PatientVitalDTOBuilder temperature(BigDecimal temp) { this.temperature = temp; return this; }
        public PatientVitalDTOBuilder loggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; return this; }

        public PatientVitalDTO build() {
            return new PatientVitalDTO(id, emergencyRequestId, heartRate, bpSystolic, bpDiastolic, spo2, temperature, loggedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmergencyRequestId() { return emergencyRequestId; }
    public void setEmergencyRequestId(Long emergencyRequestId) { this.emergencyRequestId = emergencyRequestId; }
    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }
    public Integer getBpSystolic() { return bpSystolic; }
    public void setBpSystolic(Integer bpSystolic) { this.bpSystolic = bpSystolic; }
    public Integer getBpDiastolic() { return bpDiastolic; }
    public void setBpDiastolic(Integer bpDiastolic) { this.bpDiastolic = bpDiastolic; }
    public Integer getSpo2() { return spo2; }
    public void setSpo2(Integer spo2) { this.spo2 = spo2; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}
