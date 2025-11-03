package com.example.tripGo.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class SchedulePointResponseDto {
    private String locationName;
    private LocalTime arrival;
    private LocalTime departure;
    private boolean boarding;
    private boolean dropping;
}
