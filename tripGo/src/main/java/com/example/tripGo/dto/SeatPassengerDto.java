package com.example.tripGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatPassengerDto {
    private String seatNumber;
    private String passengerName;
    private Integer passengerAge;
    private String passengerGender;
    private BigDecimal price;
}
