package com.example.tripGo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutePointRequestDto {
    @NotBlank
    @Size(max = 100) String locationName;
    Integer sequenceNo;
}
