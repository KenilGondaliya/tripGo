package com.example.tripGo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RouteRequestDto {
    @NotNull
    Long busId;
    @NotNull @Size(max = 100) String startPoint;
    @NotNull
    @Size(max = 100) String endPoint;
    Integer totalDistance;
    String totalTravelTime;
}
