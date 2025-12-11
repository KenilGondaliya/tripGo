package com.example.tripGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedSeatsDto {
    private Long seatId;
    private String seatNumber;
    private Long bookingId;
    private String passengerName;
}
