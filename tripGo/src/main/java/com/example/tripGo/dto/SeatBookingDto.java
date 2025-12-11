package com.example.tripGo.dto;

import com.example.tripGo.entity.type.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//public class SeatBookingDto {
//    private Long seatId;
//    private String passengerName;
//    private Integer passengerAge;
//    private Gender passengerGender;
//}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatBookingDto {
    @NotNull(message = "Seat ID is required")
    private Long seatId;

    @NotEmpty(message = "Passenger name is required")
    private String passengerName;

    @NotNull(message = "Passenger age is required")
    private Integer passengerAge;

    @NotEmpty(message = "Passenger gender is required")
    private String passengerGender;
}
