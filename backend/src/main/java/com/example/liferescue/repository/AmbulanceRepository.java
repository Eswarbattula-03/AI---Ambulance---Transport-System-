package com.example.liferescue.repository;

import com.example.liferescue.entity.Ambulance;
import com.example.liferescue.entity.AmbulanceStatus;
import com.example.liferescue.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {
    Optional<Ambulance> findByDriver(User driver);
    Optional<Ambulance> findByDriverId(Long driverId);
    Optional<Ambulance> findByVehicleNumber(String vehicleNumber);
    List<Ambulance> findByStatus(AmbulanceStatus status);
}
