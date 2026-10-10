package com.example.liferescue.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ambulance_location_history")
public class AmbulanceLocationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ambulance_id", nullable = false)
    private Ambulance ambulance;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 7)
    private BigDecimal longitude;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public AmbulanceLocationHistory() {}

    public AmbulanceLocationHistory(Long id, Ambulance ambulance, BigDecimal latitude, BigDecimal longitude, LocalDateTime timestamp) {
        this.id = id;
        this.ambulance = ambulance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public static AmbulanceLocationHistoryBuilder builder() {
        return new AmbulanceLocationHistoryBuilder();
    }

    public static class AmbulanceLocationHistoryBuilder {
        private Long id;
        private Ambulance ambulance;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private LocalDateTime timestamp;

        public AmbulanceLocationHistoryBuilder id(Long id) { this.id = id; return this; }
        public AmbulanceLocationHistoryBuilder ambulance(Ambulance ambulance) { this.ambulance = ambulance; return this; }
        public AmbulanceLocationHistoryBuilder latitude(BigDecimal latitude) { this.latitude = latitude; return this; }
        public AmbulanceLocationHistoryBuilder longitude(BigDecimal longitude) { this.longitude = longitude; return this; }
        public AmbulanceLocationHistoryBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public AmbulanceLocationHistory build() {
            return new AmbulanceLocationHistory(id, ambulance, latitude, longitude, timestamp);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Ambulance getAmbulance() { return ambulance; }
    public void setAmbulance(Ambulance ambulance) { this.ambulance = ambulance; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
