package com.example.liferescue.repository;

import com.example.liferescue.entity.EmergencyRequest;
import com.example.liferescue.entity.EmergencyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {
    List<EmergencyRequest> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    
    @Query("SELECT e FROM EmergencyRequest e WHERE e.patient.id = :patientId AND e.status NOT IN ('DELIVERED', 'CANCELLED', 'FAILED', 'COMPLETED')")
    Optional<EmergencyRequest> findActiveRequestByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT e FROM EmergencyRequest e WHERE e.ambulance.driver.id = :driverId AND e.status NOT IN ('DELIVERED', 'CANCELLED', 'FAILED', 'COMPLETED')")
    Optional<EmergencyRequest> findActiveRequestByDriverId(@Param("driverId") Long driverId);

    @Query("SELECT e FROM EmergencyRequest e WHERE e.status IN ('REQUESTED', 'TRIAGED', 'DISPATCHED') AND e.ambulance IS NULL ORDER BY e.createdAt DESC")
    List<EmergencyRequest> findPendingRequests();

    List<EmergencyRequest> findByHospitalIdAndStatusNotIn(Long hospitalId, List<EmergencyStatus> statuses);

    Page<EmergencyRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Long countByStatus(EmergencyStatus status);
}
