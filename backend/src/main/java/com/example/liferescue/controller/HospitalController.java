package com.example.liferescue.controller;

import com.example.liferescue.dto.ApiResponse;
import com.example.liferescue.dto.CapacityUpdateRequest;
import com.example.liferescue.dto.EmergencyResponse;
import com.example.liferescue.entity.EmergencyStatus;
import com.example.liferescue.entity.Hospital;
import com.example.liferescue.repository.EmergencyRequestRepository;
import com.example.liferescue.security.UserPrincipal;
import com.example.liferescue.service.EmergencyRequestService;
import com.example.liferescue.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hospital")
@PreAuthorize("hasRole('HOSPITAL_ADMIN') or hasRole('SYSTEM_ADMIN')")
public class HospitalController {

    private final HospitalService hospitalService;
    private final EmergencyRequestRepository emergencyRepository;
    private final EmergencyRequestService emergencyService;

    public HospitalController(HospitalService hospitalService,
                              EmergencyRequestRepository emergencyRepository,
                              EmergencyRequestService emergencyService) {
        this.hospitalService = hospitalService;
        this.emergencyRepository = emergencyRepository;
        this.emergencyService = emergencyService;
    }

    @GetMapping("/my-hospital")
    public ResponseEntity<ApiResponse<Hospital>> getMyHospital(@AuthenticationPrincipal UserPrincipal currentUser) {
        Hospital hospital = hospitalService.getHospitalForAdmin(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Hospital profile retrieved", hospital));
    }

    @GetMapping("/incoming")
    public ResponseEntity<ApiResponse<List<EmergencyResponse>>> getIncomingAmbulances(@AuthenticationPrincipal UserPrincipal currentUser) {
        Hospital hospital = hospitalService.getHospitalForAdmin(currentUser.getId());
        List<EmergencyStatus> finished = Arrays.asList(EmergencyStatus.DELIVERED, EmergencyStatus.CANCELLED, EmergencyStatus.FAILED);

        List<EmergencyResponse> incoming = emergencyRepository.findByHospitalIdAndStatusNotIn(hospital.getId(), finished)
                .stream()
                .map(emergencyService::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Incoming emergencies retrieved", incoming));
    }

    @PatchMapping("/capacity")
    public ResponseEntity<ApiResponse<Hospital>> updateCapacity(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CapacityUpdateRequest request) {

        Hospital hospital = hospitalService.getHospitalForAdmin(currentUser.getId());
        Hospital updated = hospitalService.updateCapacity(hospital.getId(), currentUser.getId(), request);

        return ResponseEntity.ok(ApiResponse.success("Hospital capacity updated successfully", updated));
    }
}
