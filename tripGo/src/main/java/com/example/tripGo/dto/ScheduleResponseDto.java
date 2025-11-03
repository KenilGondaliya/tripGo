package com.example.tripGo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class ScheduleResponseDto {
    private Long scheduleId;
    private String busNumber;
    private String operatorName;
    private String startPoint;
    private String endPoint;
    private LocalDate journeyDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer availableSeats;
    private List<SchedulePointResponseDto> points;
}
