package com.example.tripGo.dto;


import com.example.tripGo.entity.SeatPrice;
import com.example.tripGo.entity.type.DeckType;
import com.example.tripGo.entity.type.SeatType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SeatResponseDto {
    private Long seatId;
    private String seatNumber;
    private SeatType seatType;
    private DeckType deckType;
    private boolean isAvailable;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
