package com.example.liferescue.repository;

import com.example.liferescue.entity.EmergencyStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyStatusHistoryRepository extends JpaRepository<EmergencyStatusHistory, Long> {
    List<EmergencyStatusHistory> findByEmergencyRequestIdOrderByTimestampAsc(Long emergencyRequestId);
}
