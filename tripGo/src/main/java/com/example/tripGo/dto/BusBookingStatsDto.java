package com.example.tripGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusBookingStatsDto {
    private Long busId;
    private String busNumber;
    private String operatorName;
    private Long totalBookings;
    private BigDecimal totalRevenue;
    private Long totalPassengers;
    private Long cancelledBookings;
    private BigDecimal cancelledRevenue;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        private LocalDate date;
        private Long bookingsCount;
        private BigDecimal dailyRevenue;
        private Long passengersCount;
    }

    private List<DailyStats> dailyStats;
}

