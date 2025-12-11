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
public class CancellationPolicyDto {
    private Long bookingId;
    private Boolean canCancel;
    private Long hoursUntilJourney;
    private String cancellationMessage;
    private String refundPercentage;
    private BigDecimal refundAmount;
    private BigDecimal deductedAmount;
}
