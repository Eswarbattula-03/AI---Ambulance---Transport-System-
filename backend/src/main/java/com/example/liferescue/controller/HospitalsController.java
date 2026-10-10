package com.example.liferescue.controller;

import com.example.liferescue.dto.ApiResponse;
import com.example.liferescue.entity.Hospital;
import com.example.liferescue.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalsController {

    private final HospitalService hospitalService;

    public HospitalsController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Hospital>>> getAllHospitals() {
        List<Hospital> list = hospitalService.getAllHospitals();
        return ResponseEntity.ok(ApiResponse.success("Hospitals retrieved successfully", list));
    }
}
