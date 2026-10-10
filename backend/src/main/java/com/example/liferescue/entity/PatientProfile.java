package com.example.liferescue.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_profiles")
public class PatientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PatientProfile() {}

    public PatientProfile(Long id, User user, String emergencyContactName, String emergencyContactPhone, String bloodGroup, String medicalHistory, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.bloodGroup = bloodGroup;
        this.medicalHistory = medicalHistory;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PatientProfileBuilder builder() {
        return new PatientProfileBuilder();
    }

    public static class PatientProfileBuilder {
        private Long id;
        private User user;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private String bloodGroup;
        private String medicalHistory;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public PatientProfileBuilder id(Long id) { this.id = id; return this; }
        public PatientProfileBuilder user(User user) { this.user = user; return this; }
        public PatientProfileBuilder emergencyContactName(String name) { this.emergencyContactName = name; return this; }
        public PatientProfileBuilder emergencyContactPhone(String phone) { this.emergencyContactPhone = phone; return this; }
        public PatientProfileBuilder bloodGroup(String bg) { this.bloodGroup = bg; return this; }
        public PatientProfileBuilder medicalHistory(String mh) { this.medicalHistory = mh; return this; }
        public PatientProfileBuilder createdAt(LocalDateTime cat) { this.createdAt = cat; return this; }
        public PatientProfileBuilder updatedAt(LocalDateTime uat) { this.updatedAt = uat; return this; }

        public PatientProfile build() {
            return new PatientProfile(id, user, emergencyContactName, emergencyContactPhone, bloodGroup, medicalHistory, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
