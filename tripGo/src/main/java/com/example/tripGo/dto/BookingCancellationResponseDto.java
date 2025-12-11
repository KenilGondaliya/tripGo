package com.example.tripGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCancellationResponseDto {
    private Long bookingId;
    private String referenceNumber;
    private LocalDateTime cancellationDate;
    private String cancellationReason;
    private BigDecimal originalAmount;
    private BigDecimal refundAmount;
    private String refundStatus;
    private Integer estimatedRefundDays;
}

