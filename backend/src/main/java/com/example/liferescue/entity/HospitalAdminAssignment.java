package com.example.liferescue.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "hospital_admin_assignments", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "hospital_id"})})
public class HospitalAdminAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public HospitalAdminAssignment() {}

    public HospitalAdminAssignment(Long id, User user, Hospital hospital, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.hospital = hospital;
        this.createdAt = createdAt;
    }

    public static HospitalAdminAssignmentBuilder builder() {
        return new HospitalAdminAssignmentBuilder();
    }

    public static class HospitalAdminAssignmentBuilder {
        private Long id;
        private User user;
        private Hospital hospital;
        private LocalDateTime createdAt;

        public HospitalAdminAssignmentBuilder id(Long id) { this.id = id; return this; }
        public HospitalAdminAssignmentBuilder user(User user) { this.user = user; return this; }
        public HospitalAdminAssignmentBuilder hospital(Hospital hospital) { this.hospital = hospital; return this; }
        public HospitalAdminAssignmentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public HospitalAdminAssignment build() {
            return new HospitalAdminAssignment(id, user, hospital, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
