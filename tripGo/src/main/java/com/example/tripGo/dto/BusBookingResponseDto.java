package com.example.tripGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusBookingResponseDto {
    private Long bookingId;
    private String referenceNumber;
    private LocalDateTime bookingDate;
    private String status;
    private BigDecimal totalAmount;

    // Customer info
    private String contactName;
    private String contactPhone;
    private String contactEmail;

    // Journey info
    private Long scheduleId;
    private LocalDate journeyDate;
    private LocalTime startTime;
    private LocalTime endTime;

    // Route info
    private String routeName;
    private String startPoint;
    private String endPoint;

    // Bus info
    private Long busId;
    private String busNumber;
    private String busType;

    // Seats info
    private List<SeatPassengerDto> seats;

    // Payment info
    private String paymentStatus;
    private String paymentMode;
    private String transactionId;
}