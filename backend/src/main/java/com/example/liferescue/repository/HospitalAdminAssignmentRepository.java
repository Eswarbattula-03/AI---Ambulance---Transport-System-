package com.example.liferescue.repository;

import com.example.liferescue.entity.HospitalAdminAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalAdminAssignmentRepository extends JpaRepository<HospitalAdminAssignment, Long> {
    List<HospitalAdminAssignment> findByUserId(Long userId);
    Optional<HospitalAdminAssignment> findByUserIdAndHospitalId(Long userId, Long hospitalId);
}
