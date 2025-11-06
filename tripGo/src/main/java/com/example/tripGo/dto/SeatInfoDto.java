package com.example.tripGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder   // ← ADD THIS
@NoArgsConstructor
@AllArgsConstructor
public class SeatInfoDto {
    private String seatNumber;
    private BigDecimal price;
    private String passengerName;
}
