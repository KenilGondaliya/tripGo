package com.example.tripGo.dto;

import com.example.tripGo.entity.type.DeckType;
import com.example.tripGo.entity.type.SeatType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatRequestDto {
    @NotNull
    String seatNumber;
    @NotNull SeatType seatType;
    DeckType deckType;
}
