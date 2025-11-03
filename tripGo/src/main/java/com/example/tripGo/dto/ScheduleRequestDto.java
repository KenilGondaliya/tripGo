package com.example.tripGo.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestDto {
    @NotNull
    Long busId;
    @NotNull Long routeId;
    @NotNull @FutureOrPresent
    LocalDate journeyDate;
    @NotNull
    LocalTime startTime;
    @NotNull LocalTime endTime;
    @NotNull LocalTime totalTravelTime;
}
