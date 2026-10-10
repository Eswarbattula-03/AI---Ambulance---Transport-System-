package com.example.liferescue.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ambulances")
public class Ambulance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 50)
    private String vehicleNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", unique = true)
    private User driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AmbulanceStatus status = AmbulanceStatus.AVAILABLE;

    @Column(name = "current_lat", precision = 10, scale = 7)
    private BigDecimal currentLat;

    @Column(name = "current_lng", precision = 11, scale = 7)
    private BigDecimal currentLng;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Ambulance() {}

    public Ambulance(Long id, String vehicleNumber, User driver, AmbulanceStatus status, BigDecimal currentLat, BigDecimal currentLng, LocalDateTime lastUpdated, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.driver = driver;
        this.status = status;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        this.lastUpdated = lastUpdated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AmbulanceBuilder builder() {
        return new AmbulanceBuilder();
    }

    public static class AmbulanceBuilder {
        private Long id;
        private String vehicleNumber;
        private User driver;
        private AmbulanceStatus status = AmbulanceStatus.AVAILABLE;
        private BigDecimal currentLat;
        private BigDecimal currentLng;
        private LocalDateTime lastUpdated;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public AmbulanceBuilder id(Long id) { this.id = id; return this; }
        public AmbulanceBuilder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public AmbulanceBuilder driver(User driver) { this.driver = driver; return this; }
        public AmbulanceBuilder status(AmbulanceStatus status) { this.status = status; return this; }
        public AmbulanceBuilder currentLat(BigDecimal currentLat) { this.currentLat = currentLat; return this; }
        public AmbulanceBuilder currentLng(BigDecimal currentLng) { this.currentLng = currentLng; return this; }
        public AmbulanceBuilder lastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; return this; }
        public AmbulanceBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public AmbulanceBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Ambulance build() {
            return new Ambulance(id, vehicleNumber, driver, status, currentLat, currentLng, lastUpdated, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }
    public AmbulanceStatus getStatus() { return status; }
    public void setStatus(AmbulanceStatus status) { this.status = status; }
    public BigDecimal getCurrentLat() { return currentLat; }
    public void setCurrentLat(BigDecimal currentLat) { this.currentLat = currentLat; }
    public BigDecimal getCurrentLng() { return currentLng; }
    public void setCurrentLng(BigDecimal currentLng) { this.currentLng = currentLng; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
