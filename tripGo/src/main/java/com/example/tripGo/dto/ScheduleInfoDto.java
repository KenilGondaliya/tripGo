package com.example.tripGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleInfoDto {
    private Long scheduleId;
    private String startPoint;
    private String endPoint;
    private LocalDate journeyDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String busNumber;
    private String operatorName;
}
