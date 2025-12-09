package com.example.tripGo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePointResponseDto {
    private Long id;  // ← CHANGED from scheduleId
    private String locationName;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    @JsonProperty("isBoardingPoint")
    private boolean boardingPoint;
    @JsonProperty("isDroppingPoint")
    private boolean droppingPoint;
}