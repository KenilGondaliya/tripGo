package com.example.tripGo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class BookingRequestDto {
//    private Long scheduleId;
//    private Long boardingPointId;
//    private Long droppingPointId;
//    private String contactName;
//    private String contactPhone;
//    private String contactEmail;
//    private List<SeatBookingDto> seats;
//}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    @NotNull(message = "Boarding point ID is required")
    private Long boardingPointId;

    @NotNull(message = "Dropping point ID is required")
    private Long droppingPointId;

    @NotEmpty(message = "Contact name is required")
    private String contactName;

    @NotEmpty(message = "Contact phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String contactPhone;

    @NotEmpty(message = "Contact email is required")
    @Email(message = "Email should be valid")
    private String contactEmail;

    @NotEmpty(message = "At least one seat must be selected")
    private List<SeatBookingDto> seats;
}


