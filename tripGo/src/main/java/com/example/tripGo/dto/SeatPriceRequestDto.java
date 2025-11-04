package com.example.tripGo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatPriceRequestDto {
    @NotNull
    Long routeId;
    @NotNull Long seatId;
    @NotNull @Positive
    BigDecimal price;
}
