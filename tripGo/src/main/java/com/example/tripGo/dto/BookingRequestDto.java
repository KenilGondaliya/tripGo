package com.example.tripGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    private Long scheduleId;
    private Long boardingPointId;
    private Long droppingPointId;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private List<SeatBookingDto> seats;
}
