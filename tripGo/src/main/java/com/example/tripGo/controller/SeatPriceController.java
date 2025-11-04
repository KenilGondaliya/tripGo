package com.example.tripGo.controller;

import com.example.tripGo.dto.BulkSeatPriceRequestDto;
import com.example.tripGo.dto.SeatPriceRequestDto;
import com.example.tripGo.dto.SeatPriceResponseDto;
import com.example.tripGo.dto.UpdateSeatPriceDto;
import com.example.tripGo.entity.SeatPrice;
import com.example.tripGo.service.SeatMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/seat-prices")
@RequiredArgsConstructor
public class SeatPriceController {

    private final SeatMapService priceService;

    @PostMapping
    public ResponseEntity<SeatPrice> create(@Valid @RequestBody SeatPriceRequestDto dto) {
        return ResponseEntity.status(201).body(priceService.create(dto));
    }

    @PostMapping("/bulk")
    public ResponseEntity<Void> bulkSetPrice(@Valid @RequestBody BulkSeatPriceRequestDto dto) {
        priceService.setBulkPrice(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{priceId}")
    public ResponseEntity<SeatPrice> update(
            @PathVariable Long priceId,
            @Valid @RequestBody UpdateSeatPriceDto dto) {
        return ResponseEntity.ok(priceService.update(priceId, dto.getPrice()));
    }

    @DeleteMapping("/{priceId}")
    public ResponseEntity<Void> delete(@PathVariable Long priceId) {
        priceService.delete(priceId);
        return ResponseEntity.noContent().build();
    }
}
