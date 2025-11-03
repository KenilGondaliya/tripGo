package com.example.tripGo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeatMapDto {
    private Long seatId;
    private String seatNumber;
    private String seatType;
    private String deckType;
    private boolean available;
    private BigDecimal price;
}
