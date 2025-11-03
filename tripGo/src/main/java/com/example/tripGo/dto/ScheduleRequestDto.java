package com.example.tripGo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate journeyDate;
    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    @NotNull LocalTime endTime;
    @NotNull
    String totalTravelTime;
}
