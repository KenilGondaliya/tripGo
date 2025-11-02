package com.example.tripGo.dto;

import com.example.tripGo.entity.type.AcType;
import com.example.tripGo.entity.type.BusSeatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BusRequestDto {
    @NotNull
    @Pattern(regexp = "^[A-Z]{2}-[0-9]{2}-[A-Z]{2}-[0-9]{4}$")
    String busNumber;
    String busType;
    @NotNull
    BusSeatType seatType;
    String operatorName;
    @NotNull
    AcType acType;
    @NotNull
    @Min(1) Integer totalSeats;
}
