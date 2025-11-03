package com.example.tripGo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoutePointResponseDto {
    private Long pointId;
    private String locationName;
    private Integer sequenceNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
