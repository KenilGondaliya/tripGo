package com.example.tripGo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RouteResponseDto {
    private Long routeId;
    private Long busId;
    private String busNumber;
    private String startPoint;
    private String endPoint;
    private Integer totalDistance;
    private String totalTravelTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RoutePointResponseDto> routePoints;
}
