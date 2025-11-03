package com.example.tripGo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePointRequestDto {
    @NotNull
    Long routePointId;
    LocalTime arrivalTime;
    LocalTime departureTime;
    boolean isBoardingPoint;
    boolean isDroppingPoint;
}
