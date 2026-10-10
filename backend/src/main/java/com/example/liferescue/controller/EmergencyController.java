package com.example.liferescue.controller;

import com.example.liferescue.dto.ApiResponse;
import com.example.liferescue.dto.EmergencyCreateRequest;
import com.example.liferescue.dto.EmergencyResponse;
import com.example.liferescue.entity.EmergencyStatus;
import com.example.liferescue.security.UserPrincipal;
import com.example.liferescue.service.EmergencyRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/emergencies")
public class EmergencyController {

    private final EmergencyRequestService emergencyService;

    public EmergencyController(EmergencyRequestService emergencyService) {
        this.emergencyService = emergencyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<EmergencyResponse>> createEmergency(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody EmergencyCreateRequest request) {

        EmergencyResponse response = emergencyService.createEmergency(currentUser.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Emergency SOS request dispatched", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmergencyResponse>> getEmergencyById(@PathVariable Long id) {
        EmergencyResponse response = emergencyService.getEmergencyById(id);
        return ResponseEntity.ok(ApiResponse.success("Emergency details retrieved", response));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<EmergencyResponse>> getActiveEmergency(@AuthenticationPrincipal UserPrincipal currentUser) {
        Optional<EmergencyResponse> active = emergencyService.getActivePatientEmergency(currentUser.getId());
        return active.map(emergencyResponse -> ResponseEntity.ok(ApiResponse.success("Active emergency found", emergencyResponse)))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.success("No active emergency found", null)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<EmergencyResponse>>> getMyEmergencies(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<EmergencyResponse> list = emergencyService.getPatientEmergencies(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Emergency history retrieved", list));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<EmergencyResponse>> cancelEmergency(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        EmergencyResponse response = emergencyService.updateStatus(id, currentUser.getId(), EmergencyStatus.CANCELLED, "Cancelled by user");
        return ResponseEntity.ok(ApiResponse.success("Emergency cancelled successfully", response));
    }
}
