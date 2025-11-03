package com.example.tripGo.dto;


import com.example.tripGo.entity.type.DeckType;
import com.example.tripGo.entity.type.SeatType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeatResponseDto {
    private Long seatId;
    private String seatNumber;
    private SeatType seatType;
    private DeckType deckType;
    private boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
