package com.example.liferescue.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_vitals")
public class PatientVital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_request_id", nullable = false)
    private EmergencyRequest emergencyRequest;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "bp_systolic")
    private Integer bpSystolic;

    @Column(name = "bp_diastolic")
    private Integer bpDiastolic;

    @Column(name = "spo2")
    private Integer spo2;

    @Column(precision = 4, scale = 1)
    private BigDecimal temperature;

    @CreationTimestamp
    @Column(name = "logged_at", nullable = false, updatable = false)
    private LocalDateTime loggedAt;

    public PatientVital() {}

    public PatientVital(Long id, EmergencyRequest emergencyRequest, Integer heartRate, Integer bpSystolic, Integer bpDiastolic, Integer spo2, BigDecimal temperature, LocalDateTime loggedAt) {
        this.id = id;
        this.emergencyRequest = emergencyRequest;
        this.heartRate = heartRate;
        this.bpSystolic = bpSystolic;
        this.bpDiastolic = bpDiastolic;
        this.spo2 = spo2;
        this.temperature = temperature;
        this.loggedAt = loggedAt;
    }

    public static PatientVitalBuilder builder() {
        return new PatientVitalBuilder();
    }

    public static class PatientVitalBuilder {
        private Long id;
        private EmergencyRequest emergencyRequest;
        private Integer heartRate;
        private Integer bpSystolic;
        private Integer bpDiastolic;
        private Integer spo2;
        private BigDecimal temperature;
        private LocalDateTime loggedAt;

        public PatientVitalBuilder id(Long id) { this.id = id; return this; }
        public PatientVitalBuilder emergencyRequest(EmergencyRequest req) { this.emergencyRequest = req; return this; }
        public PatientVitalBuilder heartRate(Integer hr) { this.heartRate = hr; return this; }
        public PatientVitalBuilder bpSystolic(Integer s) { this.bpSystolic = s; return this; }
        public PatientVitalBuilder bpDiastolic(Integer d) { this.bpDiastolic = d; return this; }
        public PatientVitalBuilder spo2(Integer s) { this.spo2 = s; return this; }
        public PatientVitalBuilder temperature(BigDecimal t) { this.temperature = t; return this; }
        public PatientVitalBuilder loggedAt(LocalDateTime l) { this.loggedAt = l; return this; }

        public PatientVital build() {
            return new PatientVital(id, emergencyRequest, heartRate, bpSystolic, bpDiastolic, spo2, temperature, loggedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EmergencyRequest getEmergencyRequest() { return emergencyRequest; }
    public void setEmergencyRequest(EmergencyRequest emergencyRequest) { this.emergencyRequest = emergencyRequest; }
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
