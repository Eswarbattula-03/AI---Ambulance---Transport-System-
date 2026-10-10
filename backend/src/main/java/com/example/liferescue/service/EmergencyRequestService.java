package com.example.liferescue.service;

import com.example.liferescue.client.ai.AiServiceClient;
import com.example.liferescue.dto.*;
import com.example.liferescue.entity.*;
import com.example.liferescue.exception.BadRequestException;
import com.example.liferescue.exception.ResourceNotFoundException;
import com.example.liferescue.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmergencyRequestService {

    private static final Logger log = LoggerFactory.getLogger(EmergencyRequestService.class);

    private final EmergencyRequestRepository emergencyRequestRepository;
    private final EmergencyStatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final HospitalRepository hospitalRepository;
    private final AiServiceClient aiServiceClient;
    private final SimpMessagingTemplate messagingTemplate;

    public EmergencyRequestService(EmergencyRequestRepository emergencyRequestRepository,
                                  EmergencyStatusHistoryRepository statusHistoryRepository,
                                  UserRepository userRepository,
                                  AmbulanceRepository ambulanceRepository,
                                  HospitalRepository hospitalRepository,
                                  AiServiceClient aiServiceClient,
                                  SimpMessagingTemplate messagingTemplate) {
        this.emergencyRequestRepository = emergencyRequestRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.userRepository = userRepository;
        this.ambulanceRepository = ambulanceRepository;
        this.hospitalRepository = hospitalRepository;
        this.aiServiceClient = aiServiceClient;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public EmergencyResponse createEmergency(Long patientId, EmergencyCreateRequest request) {
        Optional<EmergencyRequest> existing = emergencyRequestRepository.findActiveRequestByPatientId(patientId);
        if (existing.isPresent()) {
            throw new BadRequestException("You already have an active emergency request in progress.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // Build combined symptoms string with type and patient details if provided
        StringBuilder symptomsBuilder = new StringBuilder();
        if (request.getEmergencyType() != null && !request.getEmergencyType().isBlank()) {
            symptomsBuilder.append("[").append(request.getEmergencyType().toUpperCase()).append("] ");
        }
        symptomsBuilder.append(request.getSymptoms());
        if (request.getPatientName() != null && !request.getPatientName().isBlank()) {
            symptomsBuilder.append(" | Patient Name: ").append(request.getPatientName());
        }
        if (request.getPatientPhone() != null && !request.getPatientPhone().isBlank()) {
            symptomsBuilder.append(" | Contact: ").append(request.getPatientPhone());
        }
        if (request.getPatientNotes() != null && !request.getPatientNotes().isBlank()) {
            symptomsBuilder.append(" | Notes: ").append(request.getPatientNotes());
        }

        EmergencyRequest emergency = EmergencyRequest.builder()
                .patient(patient)
                .symptoms(symptomsBuilder.toString())
                .pickupLat(request.getPickupLat())
                .pickupLng(request.getPickupLng())
                .pickupAddress(request.getPickupAddress())
                .status(EmergencyStatus.REQUESTED)
                .build();

        // Assign selected destination hospital if provided
        if (request.getHospitalId() != null) {
            Hospital hosp = hospitalRepository.findById(request.getHospitalId()).orElse(null);
            if (hosp != null) {
                emergency.setHospital(hosp);
                emergency.setDestinationLat(hosp.getLatitude());
                emergency.setDestinationLng(hosp.getLongitude());
            }
        } else {
            assignNearestHospital(emergency);
        }

        EmergencyRequest saved = emergencyRequestRepository.save(emergency);
        recordStatusHistory(saved, EmergencyStatus.REQUESTED, patient, "Booking Request Initialized");

        try {
            TriagePredictionResponse triage = aiServiceClient.predictTriage(
                    request.getSymptoms(), request.getPickupLat(), request.getPickupLng());
            if (triage != null && triage.getPriority() != null) {
                saved.setTriagePriority(TriagePriority.valueOf(triage.getPriority()));
                saved.setTriageScore(triage.getScore());
                saved.setTriageExplanation(triage.getExplanation());
                saved = emergencyRequestRepository.save(saved);
            }
        } catch (Exception e) {
            log.warn("AI Triage Service offline or failed, using default triage rules: {}", e.getMessage());
        }

        EmergencyResponse response = mapToResponse(saved);
        broadcastEmergencyUpdate(response);

        return response;
    }

    private void assignNearestHospital(EmergencyRequest request) {
        List<Hospital> hospitals = hospitalRepository.findHospitalsWithAvailableBeds();
        Hospital selectedHospital = null;
        double minHospitalDistance = Double.MAX_VALUE;

        for (Hospital h : hospitals) {
            double dist = calculateDistance(
                    request.getPickupLat().doubleValue(), request.getPickupLng().doubleValue(),
                    h.getLatitude().doubleValue(), h.getLongitude().doubleValue());
            if (dist < minHospitalDistance) {
                minHospitalDistance = dist;
                selectedHospital = h;
            }
        }

        if (selectedHospital != null) {
            request.setHospital(selectedHospital);
            request.setDestinationLat(selectedHospital.getLatitude());
            request.setDestinationLng(selectedHospital.getLongitude());
        }
    }

    @Transactional(readOnly = true)
    public List<EmergencyResponse> getPendingRequests() {
        return emergencyRequestRepository.findPendingRequests()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public synchronized EmergencyResponse acceptEmergencyByDriver(Long requestId, Long driverUserId) {
        EmergencyRequest emergency = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency Request not found"));

        // PREVENT MULTIPLE DRIVERS FROM ACCEPTING THE SAME REQUEST
        if (emergency.getAmbulance() != null || 
            (emergency.getStatus() != EmergencyStatus.REQUESTED && 
             emergency.getStatus() != EmergencyStatus.TRIAGED && 
             emergency.getStatus() != EmergencyStatus.DISPATCHED)) {
            throw new BadRequestException("This emergency request has already been accepted by another driver.");
        }

        User driver = userRepository.findById(driverUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver user record not found"));

        Ambulance ambulance = ambulanceRepository.findByDriverId(driverUserId)
                .orElseGet(() -> {
                    // Auto-assign first available ambulance to driver if not linked yet
                    List<Ambulance> available = ambulanceRepository.findByStatus(AmbulanceStatus.AVAILABLE);
                    if (!available.isEmpty()) {
                        Ambulance amb = available.get(0);
                        amb.setDriver(driver);
                        return ambulanceRepository.save(amb);
                    }
                    throw new BadRequestException("No ambulance vehicle is linked to your driver account.");
                });

        emergency.setAmbulance(ambulance);
        emergency.setStatus(EmergencyStatus.ACCEPTED);
        emergency.setAcceptedAt(LocalDateTime.now());

        ambulance.setStatus(AmbulanceStatus.ASSIGNED);
        ambulanceRepository.save(ambulance);

        EmergencyRequest updated = emergencyRequestRepository.save(emergency);
        recordStatusHistory(updated, EmergencyStatus.ACCEPTED, driver, "Request accepted by driver: " + driver.getName());

        EmergencyResponse response = mapToResponse(updated);
        broadcastEmergencyUpdate(response);

        return response;
    }

    @Transactional
    public EmergencyResponse updateStatus(Long requestId, Long userId, EmergencyStatus newStatus, String notes) {
        EmergencyRequest emergency = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency Request not found"));

        User user = userRepository.findById(userId).orElse(null);

        emergency.setStatus(newStatus);
        LocalDateTime now = LocalDateTime.now();

        if (newStatus == EmergencyStatus.ACCEPTED) {
            emergency.setAcceptedAt(now);
        } else if (newStatus == EmergencyStatus.ARRIVED || newStatus == EmergencyStatus.PATIENT_ONBOARD || newStatus == EmergencyStatus.EN_ROUTE_TO_PICKUP) {
            if (emergency.getPickupAt() == null) {
                emergency.setPickupAt(now);
            }
        } else if (newStatus == EmergencyStatus.COMPLETED || newStatus == EmergencyStatus.DELIVERED) {
            emergency.setDeliveredAt(now);

            if (emergency.getAmbulance() != null) {
                Ambulance amb = emergency.getAmbulance();
                amb.setStatus(AmbulanceStatus.AVAILABLE);
                ambulanceRepository.save(amb);
            }

            if (emergency.getHospital() != null) {
                Hospital hosp = emergency.getHospital();
                if (hosp.getAvailableBeds() > 0) {
                    hosp.setAvailableBeds(hosp.getAvailableBeds() - 1);
                }
                if (emergency.getTriagePriority() == TriagePriority.CRITICAL && hosp.getAvailableIcuBeds() > 0) {
                    hosp.setAvailableIcuBeds(hosp.getAvailableIcuBeds() - 1);
                }
                hospitalRepository.save(hosp);
            }
        } else if (newStatus == EmergencyStatus.CANCELLED || newStatus == EmergencyStatus.FAILED) {
            if (emergency.getAmbulance() != null) {
                Ambulance amb = emergency.getAmbulance();
                amb.setStatus(AmbulanceStatus.AVAILABLE);
                ambulanceRepository.save(amb);
            }
        }

        EmergencyRequest updated = emergencyRequestRepository.save(emergency);
        recordStatusHistory(updated, newStatus, user, notes != null ? notes : "Status updated to " + newStatus);

        EmergencyResponse response = mapToResponse(updated);
        broadcastEmergencyUpdate(response);

        return response;
    }

    public EmergencyResponse getEmergencyById(Long id) {
        EmergencyRequest emergency = emergencyRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency Request not found"));
        return mapToResponse(emergency);
    }

    public List<EmergencyResponse> getPatientEmergencies(Long patientId) {
        return emergencyRequestRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<EmergencyResponse> getActivePatientEmergency(Long patientId) {
        return emergencyRequestRepository.findActiveRequestByPatientId(patientId)
                .map(this::mapToResponse);
    }

    public Optional<EmergencyResponse> getActiveDriverEmergency(Long driverId) {
        return emergencyRequestRepository.findActiveRequestByDriverId(driverId)
                .map(this::mapToResponse);
    }

    private void recordStatusHistory(EmergencyRequest req, EmergencyStatus status, User user, String notes) {
        EmergencyStatusHistory history = EmergencyStatusHistory.builder()
                .emergencyRequest(req)
                .status(status)
                .updatedBy(user)
                .notes(notes)
                .build();
        statusHistoryRepository.save(history);
    }

    private void broadcastEmergencyUpdate(EmergencyResponse response) {
        messagingTemplate.convertAndSend("/topic/emergencies/" + response.getId(), response);

        if (response.getHospitalId() != null) {
            messagingTemplate.convertAndSend("/topic/hospitals/" + response.getHospitalId() + "/incoming", response);
        }
    }

    public EmergencyResponse mapToResponse(EmergencyRequest req) {
        return EmergencyResponse.builder()
                .id(req.getId())
                .patientId(req.getPatient().getId())
                .patientName(req.getPatient().getName())
                .patientPhone(req.getPatient().getPhone())
                .ambulanceId(req.getAmbulance() != null ? req.getAmbulance().getId() : null)
                .vehicleNumber(req.getAmbulance() != null ? req.getAmbulance().getVehicleNumber() : null)
                .driverName(req.getAmbulance() != null && req.getAmbulance().getDriver() != null ? req.getAmbulance().getDriver().getName() : null)
                .driverPhone(req.getAmbulance() != null && req.getAmbulance().getDriver() != null ? req.getAmbulance().getDriver().getPhone() : null)
                .ambulanceLat(req.getAmbulance() != null ? req.getAmbulance().getCurrentLat() : null)
                .ambulanceLng(req.getAmbulance() != null ? req.getAmbulance().getCurrentLng() : null)
                .hospitalId(req.getHospital() != null ? req.getHospital().getId() : null)
                .hospitalName(req.getHospital() != null ? req.getHospital().getName() : null)
                .hospitalAddress(req.getHospital() != null ? req.getHospital().getAddress() : null)
                .hospitalContact(req.getHospital() != null ? req.getHospital().getContact() : null)
                .destinationLat(req.getDestinationLat())
                .destinationLng(req.getDestinationLng())
                .triagePriority(req.getTriagePriority())
                .triageScore(req.getTriageScore())
                .triageExplanation(req.getTriageExplanation())
                .status(req.getStatus())
                .symptoms(req.getSymptoms())
                .pickupLat(req.getPickupLat())
                .pickupLng(req.getPickupLng())
                .pickupAddress(req.getPickupAddress())
                .createdAt(req.getCreatedAt())
                .acceptedAt(req.getAcceptedAt())
                .pickupAt(req.getPickupAt())
                .deliveredAt(req.getDeliveredAt())
                .build();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        return dist;
    }
}
