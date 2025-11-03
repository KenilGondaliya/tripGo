package com.example.tripGo.dto;

import com.example.tripGo.entity.type.AcType;
import com.example.tripGo.entity.type.BusSeatType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BusResponseDto {
    private Long busId;
    private String busNumber;
    private String busType;
    private BusSeatType seatType;
    private String operatorName;
    private AcType acType;
    private Integer totalSeats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SeatResponseDto> seats;
}
