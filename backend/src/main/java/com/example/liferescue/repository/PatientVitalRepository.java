package com.example.liferescue.repository;

import com.example.liferescue.entity.PatientVital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientVitalRepository extends JpaRepository<PatientVital, Long> {
    List<PatientVital> findByEmergencyRequestIdOrderByLoggedAtAsc(Long emergencyRequestId);
}
