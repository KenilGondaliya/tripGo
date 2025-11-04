package com.example.tripGo.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class UpdateSeatPriceDto {
    @NonNull
    BigDecimal price;
}
