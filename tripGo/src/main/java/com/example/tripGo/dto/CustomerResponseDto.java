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
public class CustomerResponseDto {
    private Long customerId;
    private String name;
    private String email;
    private String phone;
    private UserDto user;
    private Long totalBookings;
    private BigDecimal totalRevenue;
    private LocalDateTime lastBooking;
}
