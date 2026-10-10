package com.example.liferescue.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 7)
    private BigDecimal longitude;

    @Column(name = "total_beds", nullable = false)
    private Integer totalBeds = 0;

    @Column(name = "available_beds", nullable = false)
    private Integer availableBeds = 0;

    @Column(name = "icu_beds", nullable = false)
    private Integer icuBeds = 0;

    @Column(name = "available_icu_beds", nullable = false)
    private Integer availableIcuBeds = 0;

    @Column(nullable = false, length = 20)
    private String contact;

    @Column(length = 255)
    private String address;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Hospital() {}

    public Hospital(Long id, String name, BigDecimal latitude, BigDecimal longitude, Integer totalBeds, Integer availableBeds, Integer icuBeds, Integer availableIcuBeds, String contact, String address, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalBeds = totalBeds;
        this.availableBeds = availableBeds;
        this.icuBeds = icuBeds;
        this.availableIcuBeds = availableIcuBeds;
        this.contact = contact;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static HospitalBuilder builder() {
        return new HospitalBuilder();
    }

    public static class HospitalBuilder {
        private Long id;
        private String name;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private Integer totalBeds = 0;
        private Integer availableBeds = 0;
        private Integer icuBeds = 0;
        private Integer availableIcuBeds = 0;
        private String contact;
        private String address;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public HospitalBuilder id(Long id) { this.id = id; return this; }
        public HospitalBuilder name(String name) { this.name = name; return this; }
        public HospitalBuilder latitude(BigDecimal latitude) { this.latitude = latitude; return this; }
        public HospitalBuilder longitude(BigDecimal longitude) { this.longitude = longitude; return this; }
        public HospitalBuilder totalBeds(Integer totalBeds) { this.totalBeds = totalBeds; return this; }
        public HospitalBuilder availableBeds(Integer availableBeds) { this.availableBeds = availableBeds; return this; }
        public HospitalBuilder icuBeds(Integer icuBeds) { this.icuBeds = icuBeds; return this; }
        public HospitalBuilder availableIcuBeds(Integer availableIcuBeds) { this.availableIcuBeds = availableIcuBeds; return this; }
        public HospitalBuilder contact(String contact) { this.contact = contact; return this; }
        public HospitalBuilder address(String address) { this.address = address; return this; }
        public HospitalBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public HospitalBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Hospital build() {
            return new Hospital(id, name, latitude, longitude, totalBeds, availableBeds, icuBeds, availableIcuBeds, contact, address, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public Integer getTotalBeds() { return totalBeds; }
    public void setTotalBeds(Integer totalBeds) { this.totalBeds = totalBeds; }
    public Integer getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(Integer availableBeds) { this.availableBeds = availableBeds; }
    public Integer getIcuBeds() { return icuBeds; }
    public void setIcuBeds(Integer icuBeds) { this.icuBeds = icuBeds; }
    public Integer getAvailableIcuBeds() { return availableIcuBeds; }
    public void setAvailableIcuBeds(Integer availableIcuBeds) { this.availableIcuBeds = availableIcuBeds; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
