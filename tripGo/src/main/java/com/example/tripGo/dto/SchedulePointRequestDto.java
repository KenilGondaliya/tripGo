package com.example.tripGo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePointRequestDto {
    @NotBlank(message = "Location name is required")
    private String locationName;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private boolean isBoardingPoint;
    private boolean isDroppingPoint;
}
