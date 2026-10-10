package com.example.liferescue.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_requests")
public class EmergencyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ambulance_id")
    private Ambulance ambulance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(name = "triage_priority", nullable = false, length = 20)
    private TriagePriority triagePriority = TriagePriority.HIGH;

    @Column(name = "triage_score", precision = 5, scale = 2)
    private BigDecimal triageScore = BigDecimal.ZERO;

    @Column(name = "triage_explanation", columnDefinition = "TEXT")
    private String triageExplanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmergencyStatus status = EmergencyStatus.REQUESTED;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "pickup_lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal pickupLat;

    @Column(name = "pickup_lng", nullable = false, precision = 11, scale = 7)
    private BigDecimal pickupLng;

    @Column(name = "pickup_address", length = 255)
    private String pickupAddress;

    @Column(name = "destination_lat", precision = 10, scale = 7)
    private BigDecimal destinationLat;

    @Column(name = "destination_lng", precision = 11, scale = 7)
    private BigDecimal destinationLng;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "pickup_at")
    private LocalDateTime pickupAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    public EmergencyRequest() {}

    public EmergencyRequest(Long id, User patient, Ambulance ambulance, Hospital hospital, TriagePriority triagePriority, BigDecimal triageScore, String triageExplanation, EmergencyStatus status, String symptoms, BigDecimal pickupLat, BigDecimal pickupLng, String pickupAddress, BigDecimal destinationLat, BigDecimal destinationLng, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime acceptedAt, LocalDateTime pickupAt, LocalDateTime deliveredAt) {
        this.id = id;
        this.patient = patient;
        this.ambulance = ambulance;
        this.hospital = hospital;
        this.triagePriority = triagePriority;
        this.triageScore = triageScore;
        this.triageExplanation = triageExplanation;
        this.status = status;
        this.symptoms = symptoms;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.pickupAddress = pickupAddress;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.acceptedAt = acceptedAt;
        this.pickupAt = pickupAt;
        this.deliveredAt = deliveredAt;
    }

    public static EmergencyRequestBuilder builder() {
        return new EmergencyRequestBuilder();
    }

    public static class EmergencyRequestBuilder {
        private Long id;
        private User patient;
        private Ambulance ambulance;
        private Hospital hospital;
        private TriagePriority triagePriority = TriagePriority.HIGH;
        private BigDecimal triageScore = BigDecimal.ZERO;
        private String triageExplanation;
        private EmergencyStatus status = EmergencyStatus.REQUESTED;
        private String symptoms;
        private BigDecimal pickupLat;
        private BigDecimal pickupLng;
        private String pickupAddress;
        private BigDecimal destinationLat;
        private BigDecimal destinationLng;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime acceptedAt;
        private LocalDateTime pickupAt;
        private LocalDateTime deliveredAt;

        public EmergencyRequestBuilder id(Long id) { this.id = id; return this; }
        public EmergencyRequestBuilder patient(User patient) { this.patient = patient; return this; }
        public EmergencyRequestBuilder ambulance(Ambulance ambulance) { this.ambulance = ambulance; return this; }
        public EmergencyRequestBuilder hospital(Hospital hospital) { this.hospital = hospital; return this; }
        public EmergencyRequestBuilder triagePriority(TriagePriority priority) { this.triagePriority = priority; return this; }
        public EmergencyRequestBuilder triageScore(BigDecimal score) { this.triageScore = score; return this; }
        public EmergencyRequestBuilder triageExplanation(String explanation) { this.triageExplanation = explanation; return this; }
        public EmergencyRequestBuilder status(EmergencyStatus status) { this.status = status; return this; }
        public EmergencyRequestBuilder symptoms(String symptoms) { this.symptoms = symptoms; return this; }
        public EmergencyRequestBuilder pickupLat(BigDecimal pickupLat) { this.pickupLat = pickupLat; return this; }
        public EmergencyRequestBuilder pickupLng(BigDecimal pickupLng) { this.pickupLng = pickupLng; return this; }
        public EmergencyRequestBuilder pickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; return this; }
        public EmergencyRequestBuilder destinationLat(BigDecimal destinationLat) { this.destinationLat = destinationLat; return this; }
        public EmergencyRequestBuilder destinationLng(BigDecimal destinationLng) { this.destinationLng = destinationLng; return this; }
        public EmergencyRequestBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public EmergencyRequestBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public EmergencyRequestBuilder acceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; return this; }
        public EmergencyRequestBuilder pickupAt(LocalDateTime pickupAt) { this.pickupAt = pickupAt; return this; }
        public EmergencyRequestBuilder deliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; return this; }

        public EmergencyRequest build() {
            return new EmergencyRequest(id, patient, ambulance, hospital, triagePriority, triageScore, triageExplanation, status, symptoms, pickupLat, pickupLng, pickupAddress, destinationLat, destinationLng, createdAt, updatedAt, acceptedAt, pickupAt, deliveredAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }
    public Ambulance getAmbulance() { return ambulance; }
    public void setAmbulance(Ambulance ambulance) { this.ambulance = ambulance; }
    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }
    public TriagePriority getTriagePriority() { return triagePriority; }
    public void setTriagePriority(TriagePriority triagePriority) { this.triagePriority = triagePriority; }
    public BigDecimal getTriageScore() { return triageScore; }
    public void setTriageScore(BigDecimal triageScore) { this.triageScore = triageScore; }
    public String getTriageExplanation() { return triageExplanation; }
    public void setTriageExplanation(String triageExplanation) { this.triageExplanation = triageExplanation; }
    public EmergencyStatus getStatus() { return status; }
    public void setStatus(EmergencyStatus status) { this.status = status; }
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public BigDecimal getPickupLat() { return pickupLat; }
    public void setPickupLat(BigDecimal pickupLat) { this.pickupLat = pickupLat; }
    public BigDecimal getPickupLng() { return pickupLng; }
    public void setPickupLng(BigDecimal pickupLng) { this.pickupLng = pickupLng; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public BigDecimal getDestinationLat() { return destinationLat; }
    public void setDestinationLat(BigDecimal destinationLat) { this.destinationLat = destinationLat; }
    public BigDecimal getDestinationLng() { return destinationLng; }
    public void setDestinationLng(BigDecimal destinationLng) { this.destinationLng = destinationLng; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    public LocalDateTime getPickupAt() { return pickupAt; }
    public void setPickupAt(LocalDateTime pickupAt) { this.pickupAt = pickupAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
}
