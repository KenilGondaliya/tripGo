package com.example.tripGo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeatPriceResponseDto {
    private Long seatId;
    private String seatNumber;
    private String seatType;
    private String deckType;
    private BigDecimal price;
    private boolean available;
}
