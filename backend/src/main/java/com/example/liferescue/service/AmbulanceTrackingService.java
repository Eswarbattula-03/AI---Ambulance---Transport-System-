package com.example.liferescue.service;

import com.example.liferescue.dto.LocationUpdateRequest;
import com.example.liferescue.entity.Ambulance;
import com.example.liferescue.entity.AmbulanceLocationHistory;
import com.example.liferescue.exception.ResourceNotFoundException;
import com.example.liferescue.repository.AmbulanceLocationHistoryRepository;
import com.example.liferescue.repository.AmbulanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AmbulanceTrackingService {

    private static final Logger log = LoggerFactory.getLogger(AmbulanceTrackingService.class);

    private final AmbulanceRepository ambulanceRepository;
    private final AmbulanceLocationHistoryRepository locationHistoryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public AmbulanceTrackingService(AmbulanceRepository ambulanceRepository,
                                   AmbulanceLocationHistoryRepository locationHistoryRepository,
                                   SimpMessagingTemplate messagingTemplate) {
        this.ambulanceRepository = ambulanceRepository;
        this.locationHistoryRepository = locationHistoryRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void updateLocation(Long driverId, LocationUpdateRequest request) {
        Ambulance ambulance = ambulanceRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("No ambulance assigned to this driver"));

        ambulance.setCurrentLat(request.getLatitude());
        ambulance.setCurrentLng(request.getLongitude());
        ambulance.setLastUpdated(LocalDateTime.now());
        ambulanceRepository.save(ambulance);

        AmbulanceLocationHistory history = AmbulanceLocationHistory.builder()
                .ambulance(ambulance)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        locationHistoryRepository.save(history);

        Map<String, Object> payload = new HashMap<>();
        payload.put("ambulanceId", ambulance.getId());
        payload.put("vehicleNumber", ambulance.getVehicleNumber());
        payload.put("latitude", request.getLatitude());
        payload.put("longitude", request.getLongitude());
        payload.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/ambulances/" + ambulance.getId() + "/location", payload);
    }
}
