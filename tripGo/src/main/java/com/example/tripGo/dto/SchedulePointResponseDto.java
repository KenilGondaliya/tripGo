package com.example.tripGo.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class SchedulePointResponseDto {
    private Long id;
    private String locationName;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private boolean isBoardingPoint;
    private boolean isDroppingPoint;
}
