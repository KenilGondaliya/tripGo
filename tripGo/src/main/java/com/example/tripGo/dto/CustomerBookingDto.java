package com.example.tripGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBookingDto {
    private Long bookingId;
    private String referenceNumber;
    private LocalDateTime bookingDate;
    private BigDecimal totalAmount;
    private String status;
    private String busNumber;
}

