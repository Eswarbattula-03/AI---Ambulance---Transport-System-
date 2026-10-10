package com.example.liferescue.repository;

import com.example.liferescue.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    @Query("SELECT h FROM Hospital h WHERE h.availableBeds > 0 OR h.availableIcuBeds > 0")
    List<Hospital> findHospitalsWithAvailableBeds();
}
