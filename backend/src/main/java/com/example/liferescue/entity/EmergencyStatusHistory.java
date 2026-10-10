package com.example.liferescue.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_status_history")
public class EmergencyStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_request_id", nullable = false)
    private EmergencyRequest emergencyRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmergencyStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    @Column(length = 255)
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public EmergencyStatusHistory() {}

    public EmergencyStatusHistory(Long id, EmergencyRequest emergencyRequest, EmergencyStatus status, User updatedBy, String notes, LocalDateTime timestamp) {
        this.id = id;
        this.emergencyRequest = emergencyRequest;
        this.status = status;
        this.updatedBy = updatedBy;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public static EmergencyStatusHistoryBuilder builder() {
        return new EmergencyStatusHistoryBuilder();
    }

    public static class EmergencyStatusHistoryBuilder {
        private Long id;
        private EmergencyRequest emergencyRequest;
        private EmergencyStatus status;
        private User updatedBy;
        private String notes;
        private LocalDateTime timestamp;

        public EmergencyStatusHistoryBuilder id(Long id) { this.id = id; return this; }
        public EmergencyStatusHistoryBuilder emergencyRequest(EmergencyRequest emergencyRequest) { this.emergencyRequest = emergencyRequest; return this; }
        public EmergencyStatusHistoryBuilder status(EmergencyStatus status) { this.status = status; return this; }
        public EmergencyStatusHistoryBuilder updatedBy(User updatedBy) { this.updatedBy = updatedBy; return this; }
        public EmergencyStatusHistoryBuilder notes(String notes) { this.notes = notes; return this; }
        public EmergencyStatusHistoryBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public EmergencyStatusHistory build() {
            return new EmergencyStatusHistory(id, emergencyRequest, status, updatedBy, notes, timestamp);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EmergencyRequest getEmergencyRequest() { return emergencyRequest; }
    public void setEmergencyRequest(EmergencyRequest emergencyRequest) { this.emergencyRequest = emergencyRequest; }
    public EmergencyStatus getStatus() { return status; }
    public void setStatus(EmergencyStatus status) { this.status = status; }
    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
