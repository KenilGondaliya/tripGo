package com.example.tripGo.dto;

import com.example.tripGo.entity.type.Gender;
import lombok.Data;

@Data
public class SeatBookingDto {
    private Long seatId;
    private String passengerName;
    private Integer passengerAge;
    private Gender passengerGender;
}
