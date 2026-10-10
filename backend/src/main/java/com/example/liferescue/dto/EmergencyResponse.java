package com.example.liferescue.dto;

import com.example.liferescue.entity.EmergencyStatus;
import com.example.liferescue.entity.TriagePriority;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EmergencyResponse {

    private Long id;
    private Long patientId;
    private String patientName;
    private String patientPhone;
    
    private Long ambulanceId;
    private String vehicleNumber;
    private String driverName;
    private String driverPhone;
    private BigDecimal ambulanceLat;
    private BigDecimal ambulanceLng;

    private Long hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalContact;
    private BigDecimal destinationLat;
    private BigDecimal destinationLng;

    private TriagePriority triagePriority;
    private BigDecimal triageScore;
    private String triageExplanation;

    private EmergencyStatus status;
    private String symptoms;
    private BigDecimal pickupLat;
    private BigDecimal pickupLng;
    private String pickupAddress;

    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime pickupAt;
    private LocalDateTime deliveredAt;

    public EmergencyResponse() {}

    public EmergencyResponse(Long id, Long patientId, String patientName, String patientPhone, Long ambulanceId, String vehicleNumber, String driverName, String driverPhone, BigDecimal ambulanceLat, BigDecimal ambulanceLng, Long hospitalId, String hospitalName, String hospitalAddress, String hospitalContact, BigDecimal destinationLat, BigDecimal destinationLng, TriagePriority triagePriority, BigDecimal triageScore, String triageExplanation, EmergencyStatus status, String symptoms, BigDecimal pickupLat, BigDecimal pickupLng, String pickupAddress, LocalDateTime createdAt, LocalDateTime acceptedAt, LocalDateTime pickupAt, LocalDateTime deliveredAt) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.ambulanceId = ambulanceId;
        this.vehicleNumber = vehicleNumber;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.ambulanceLat = ambulanceLat;
        this.ambulanceLng = ambulanceLng;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
        this.hospitalContact = hospitalContact;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.triagePriority = triagePriority;
        this.triageScore = triageScore;
        this.triageExplanation = triageExplanation;
        this.status = status;
        this.symptoms = symptoms;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.pickupAddress = pickupAddress;
        this.createdAt = createdAt;
        this.acceptedAt = acceptedAt;
        this.pickupAt = pickupAt;
        this.deliveredAt = deliveredAt;
    }

    public static EmergencyResponseBuilder builder() {
        return new EmergencyResponseBuilder();
    }

    public static class EmergencyResponseBuilder {
        private Long id;
        private Long patientId;
        private String patientName;
        private String patientPhone;
        private Long ambulanceId;
        private String vehicleNumber;
        private String driverName;
        private String driverPhone;
        private BigDecimal ambulanceLat;
        private BigDecimal ambulanceLng;
        private Long hospitalId;
        private String hospitalName;
        private String hospitalAddress;
        private String hospitalContact;
        private BigDecimal destinationLat;
        private BigDecimal destinationLng;
        private TriagePriority triagePriority;
        private BigDecimal triageScore;
        private String triageExplanation;
        private EmergencyStatus status;
        private String symptoms;
        private BigDecimal pickupLat;
        private BigDecimal pickupLng;
        private String pickupAddress;
        private LocalDateTime createdAt;
        private LocalDateTime acceptedAt;
        private LocalDateTime pickupAt;
        private LocalDateTime deliveredAt;

        public EmergencyResponseBuilder id(Long id) { this.id = id; return this; }
        public EmergencyResponseBuilder patientId(Long patientId) { this.patientId = patientId; return this; }
        public EmergencyResponseBuilder patientName(String patientName) { this.patientName = patientName; return this; }
        public EmergencyResponseBuilder patientPhone(String patientPhone) { this.patientPhone = patientPhone; return this; }
        public EmergencyResponseBuilder ambulanceId(Long ambulanceId) { this.ambulanceId = ambulanceId; return this; }
        public EmergencyResponseBuilder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public EmergencyResponseBuilder driverName(String driverName) { this.driverName = driverName; return this; }
        public EmergencyResponseBuilder driverPhone(String driverPhone) { this.driverPhone = driverPhone; return this; }
        public EmergencyResponseBuilder ambulanceLat(BigDecimal ambulanceLat) { this.ambulanceLat = ambulanceLat; return this; }
        public EmergencyResponseBuilder ambulanceLng(BigDecimal ambulanceLng) { this.ambulanceLng = ambulanceLng; return this; }
        public EmergencyResponseBuilder hospitalId(Long hospitalId) { this.hospitalId = hospitalId; return this; }
        public EmergencyResponseBuilder hospitalName(String hospitalName) { this.hospitalName = hospitalName; return this; }
        public EmergencyResponseBuilder hospitalAddress(String hospitalAddress) { this.hospitalAddress = hospitalAddress; return this; }
        public EmergencyResponseBuilder hospitalContact(String hospitalContact) { this.hospitalContact = hospitalContact; return this; }
        public EmergencyResponseBuilder destinationLat(BigDecimal destinationLat) { this.destinationLat = destinationLat; return this; }
        public EmergencyResponseBuilder destinationLng(BigDecimal destinationLng) { this.destinationLng = destinationLng; return this; }
        public EmergencyResponseBuilder triagePriority(TriagePriority priority) { this.triagePriority = priority; return this; }
        public EmergencyResponseBuilder triageScore(BigDecimal score) { this.triageScore = score; return this; }
        public EmergencyResponseBuilder triageExplanation(String exp) { this.triageExplanation = exp; return this; }
        public EmergencyResponseBuilder status(EmergencyStatus status) { this.status = status; return this; }
        public EmergencyResponseBuilder symptoms(String symptoms) { this.symptoms = symptoms; return this; }
        public EmergencyResponseBuilder pickupLat(BigDecimal pickupLat) { this.pickupLat = pickupLat; return this; }
        public EmergencyResponseBuilder pickupLng(BigDecimal pickupLng) { this.pickupLng = pickupLng; return this; }
        public EmergencyResponseBuilder pickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; return this; }
        public EmergencyResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public EmergencyResponseBuilder acceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; return this; }
        public EmergencyResponseBuilder pickupAt(LocalDateTime pickupAt) { this.pickupAt = pickupAt; return this; }
        public EmergencyResponseBuilder deliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; return this; }

        public EmergencyResponse build() {
            return new EmergencyResponse(id, patientId, patientName, patientPhone, ambulanceId, vehicleNumber, driverName, driverPhone, ambulanceLat, ambulanceLng, hospitalId, hospitalName, hospitalAddress, hospitalContact, destinationLat, destinationLng, triagePriority, triageScore, triageExplanation, status, symptoms, pickupLat, pickupLng, pickupAddress, createdAt, acceptedAt, pickupAt, deliveredAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }
    public Long getAmbulanceId() { return ambulanceId; }
    public void setAmbulanceId(Long ambulanceId) { this.ambulanceId = ambulanceId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }
    public BigDecimal getAmbulanceLat() { return ambulanceLat; }
    public void setAmbulanceLat(BigDecimal ambulanceLat) { this.ambulanceLat = ambulanceLat; }
    public BigDecimal getAmbulanceLng() { return ambulanceLng; }
    public void setAmbulanceLng(BigDecimal ambulanceLng) { this.ambulanceLng = ambulanceLng; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public String getHospitalAddress() { return hospitalAddress; }
    public void setHospitalAddress(String hospitalAddress) { this.hospitalAddress = hospitalAddress; }
    public String getHospitalContact() { return hospitalContact; }
    public void setHospitalContact(String hospitalContact) { this.hospitalContact = hospitalContact; }
    public BigDecimal getDestinationLat() { return destinationLat; }
    public void setDestinationLat(BigDecimal destinationLat) { this.destinationLat = destinationLat; }
    public BigDecimal getDestinationLng() { return destinationLng; }
    public void setDestinationLng(BigDecimal destinationLng) { this.destinationLng = destinationLng; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    public LocalDateTime getPickupAt() { return pickupAt; }
    public void setPickupAt(LocalDateTime pickupAt) { this.pickupAt = pickupAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
}
