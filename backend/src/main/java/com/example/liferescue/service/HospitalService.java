package com.example.liferescue.service;

import com.example.liferescue.dto.CapacityUpdateRequest;
import com.example.liferescue.entity.Hospital;
import com.example.liferescue.entity.HospitalAdminAssignment;
import com.example.liferescue.exception.BadRequestException;
import com.example.liferescue.exception.ResourceNotFoundException;
import com.example.liferescue.repository.HospitalAdminAssignmentRepository;
import com.example.liferescue.repository.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalAdminAssignmentRepository assignmentRepository;

    public HospitalService(HospitalRepository hospitalRepository, HospitalAdminAssignmentRepository assignmentRepository) {
        this.hospitalRepository = hospitalRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public Hospital getHospitalById(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
    }

    public Hospital getHospitalForAdmin(Long userId) {
        List<HospitalAdminAssignment> assignments = assignmentRepository.findByUserId(userId);
        if (assignments.isEmpty()) {
            throw new ResourceNotFoundException("No hospital assigned to this hospital admin");
        }
        return assignments.get(0).getHospital();
    }

    @Transactional
    public Hospital updateCapacity(Long hospitalId, Long userId, CapacityUpdateRequest request) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        if (request.getAvailableBeds() > hospital.getTotalBeds()) {
            throw new BadRequestException("Available beds cannot exceed total bed capacity (" + hospital.getTotalBeds() + ")");
        }
        if (request.getAvailableIcuBeds() > hospital.getIcuBeds()) {
            throw new BadRequestException("Available ICU beds cannot exceed total ICU capacity (" + hospital.getIcuBeds() + ")");
        }

        hospital.setAvailableBeds(request.getAvailableBeds());
        hospital.setAvailableIcuBeds(request.getAvailableIcuBeds());

        return hospitalRepository.save(hospital);
    }
}
