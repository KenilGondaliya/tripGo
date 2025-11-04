package com.example.tripGo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BulkSeatPriceRequestDto {
    @NotNull
    private Long routeId;
    @NotNull private BigDecimal price;
}