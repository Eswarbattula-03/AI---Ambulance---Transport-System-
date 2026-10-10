package com.example.liferescue.controller;

import com.example.liferescue.dto.ApiResponse;
import com.example.liferescue.dto.EmergencyResponse;
import com.example.liferescue.dto.LocationUpdateRequest;
import com.example.liferescue.entity.EmergencyStatus;
import com.example.liferescue.security.UserPrincipal;
import com.example.liferescue.service.AmbulanceTrackingService;
import com.example.liferescue.service.EmergencyRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/driver")
@PreAuthorize("hasRole('AMBULANCE_DRIVER') or hasRole('SYSTEM_ADMIN')")
public class DriverController {

    private final EmergencyRequestService emergencyService;
    private final AmbulanceTrackingService trackingService;

    public DriverController(EmergencyRequestService emergencyService, AmbulanceTrackingService trackingService) {
        this.emergencyService = emergencyService;
        this.trackingService = trackingService;
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<EmergencyResponse>>> getPendingRequests() {
        List<EmergencyResponse> pending = emergencyService.getPendingRequests();
        return ResponseEntity.ok(ApiResponse.success("Pending emergency requests retrieved", pending));
    }

    @GetMapping("/active-assignment")
    public ResponseEntity<ApiResponse<EmergencyResponse>> getActiveAssignment(@AuthenticationPrincipal UserPrincipal currentUser) {
        Optional<EmergencyResponse> active = emergencyService.getActiveDriverEmergency(currentUser.getId());
        return active.map(emergencyResponse -> ResponseEntity.ok(ApiResponse.success("Active assignment retrieved", emergencyResponse)))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.success("No active assignment", null)));
    }

    @PostMapping("/assignments/{id}/accept")
    public ResponseEntity<ApiResponse<EmergencyResponse>> acceptAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        EmergencyResponse response = emergencyService.acceptEmergencyByDriver(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Assignment accepted", response));
    }

    @PostMapping("/assignments/{id}/reject")
    public ResponseEntity<ApiResponse<EmergencyResponse>> rejectAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        EmergencyResponse response = emergencyService.updateStatus(id, currentUser.getId(), EmergencyStatus.FAILED, "Rejected by driver");
        return ResponseEntity.ok(ApiResponse.success("Assignment rejected", response));
    }

    @PatchMapping("/assignments/{id}/status")
    public ResponseEntity<ApiResponse<EmergencyResponse>> updateStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody Map<String, String> payload) {

        String statusStr = payload.get("status");
        String notes = payload.getOrDefault("notes", "Status updated by driver");
        EmergencyStatus newStatus = EmergencyStatus.valueOf(statusStr);

        EmergencyResponse response = emergencyService.updateStatus(id, currentUser.getId(), newStatus, notes);
        return ResponseEntity.ok(ApiResponse.success("Emergency status updated", response));
    }

    @PostMapping("/location")
    public ResponseEntity<ApiResponse<String>> updateLocation(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody LocationUpdateRequest request) {

        trackingService.updateLocation(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Location updated successfully"));
    }
}
