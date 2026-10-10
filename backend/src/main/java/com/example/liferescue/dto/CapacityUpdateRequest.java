package com.example.liferescue.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CapacityUpdateRequest {

    @NotNull(message = "Available beds is required")
    @Min(value = 0, message = "Available beds cannot be negative")
    private Integer availableBeds;

    @NotNull(message = "Available ICU beds is required")
    @Min(value = 0, message = "Available ICU beds cannot be negative")
    private Integer availableIcuBeds;

    public CapacityUpdateRequest() {}

    public CapacityUpdateRequest(Integer availableBeds, Integer availableIcuBeds) {
        this.availableBeds = availableBeds;
        this.availableIcuBeds = availableIcuBeds;
    }

    public Integer getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(Integer availableBeds) { this.availableBeds = availableBeds; }
    public Integer getAvailableIcuBeds() { return availableIcuBeds; }
    public void setAvailableIcuBeds(Integer availableIcuBeds) { this.availableIcuBeds = availableIcuBeds; }
}
