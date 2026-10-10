package com.example.liferescue.controller;

import com.example.liferescue.dto.ApiResponse;
import com.example.liferescue.dto.EmergencyResponse;
import com.example.liferescue.entity.Ambulance;
import com.example.liferescue.entity.EmergencyStatus;
import com.example.liferescue.entity.Hospital;
import com.example.liferescue.entity.User;
import com.example.liferescue.repository.AmbulanceRepository;
import com.example.liferescue.repository.EmergencyRequestRepository;
import com.example.liferescue.repository.HospitalRepository;
import com.example.liferescue.repository.UserRepository;
import com.example.liferescue.service.EmergencyRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final HospitalRepository hospitalRepository;
    private final EmergencyRequestRepository emergencyRepository;
    private final EmergencyRequestService emergencyService;

    public AdminController(UserRepository userRepository,
                           AmbulanceRepository ambulanceRepository,
                           HospitalRepository hospitalRepository,
                           EmergencyRequestRepository emergencyRepository,
                           EmergencyRequestService emergencyService) {
        this.userRepository = userRepository;
        this.ambulanceRepository = ambulanceRepository;
        this.hospitalRepository = hospitalRepository;
        this.emergencyRepository = emergencyRepository;
        this.emergencyService = emergencyService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", userRepository.findAll()));
    }

    @GetMapping("/ambulances")
    public ResponseEntity<ApiResponse<List<Ambulance>>> getAllAmbulances() {
        return ResponseEntity.ok(ApiResponse.success("Ambulances retrieved", ambulanceRepository.findAll()));
    }

    @GetMapping("/hospitals")
    public ResponseEntity<ApiResponse<List<Hospital>>> getAllHospitals() {
        return ResponseEntity.ok(ApiResponse.success("Hospitals retrieved", hospitalRepository.findAll()));
    }

    @GetMapping("/emergencies")
    public ResponseEntity<ApiResponse<List<EmergencyResponse>>> getAllEmergencies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<EmergencyResponse> paged = emergencyRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(emergencyService::mapToResponse);

        return ResponseEntity.ok(ApiResponse.success("Emergencies retrieved", paged.getContent()));
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalAmbulances", ambulanceRepository.count());
        stats.put("totalHospitals", hospitalRepository.count());
        stats.put("totalEmergencies", emergencyRepository.count());
        stats.put("activeEmergencies", emergencyRepository.countByStatus(EmergencyStatus.DISPATCHED) + 
                                        emergencyRepository.countByStatus(EmergencyStatus.ACCEPTED) + 
                                        emergencyRepository.countByStatus(EmergencyStatus.EN_ROUTE_TO_PICKUP) +
                                        emergencyRepository.countByStatus(EmergencyStatus.PATIENT_ONBOARD));
        stats.put("deliveredEmergencies", emergencyRepository.countByStatus(EmergencyStatus.DELIVERED));

        return ResponseEntity.ok(ApiResponse.success("System analytics retrieved", stats));
    }
}
