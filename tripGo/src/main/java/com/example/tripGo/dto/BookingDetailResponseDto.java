package com.example.tripGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetailResponseDto {
    private Long bookingId;
    private String referenceNumber;
    private LocalDateTime bookingDate;
    private String status;
    private BigDecimal totalAmount;

    private ScheduleInfoDto schedule;
    private PointInfoDto boardingPoint;
    private PointInfoDto droppingPoint;

    private String contactName;
    private String contactPhone;
    private String contactEmail;

    private List<SeatPassengerDto> seats;

    private String paymentStatus;
    private String paymentMode;
    private String transactionId;
}
