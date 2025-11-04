package com.example.tripGo.controller;

import com.example.tripGo.dto.BusRequestDto;
import com.example.tripGo.dto.BusResponseDto;
import com.example.tripGo.dto.SeatRequestDto;
import com.example.tripGo.dto.SeatResponseDto;
import com.example.tripGo.entity.type.AcType;
import com.example.tripGo.entity.type.BusSeatType;
import com.example.tripGo.service.BusService;
import com.example.tripGo.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;
    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<BusResponseDto> create(@Valid @RequestBody BusRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(busService.createBus(dto));
    }

    @GetMapping
    public ResponseEntity<Page<BusResponseDto>> list(
            @RequestParam(defaultValue = "") String busNumber,
            @RequestParam(defaultValue = "") String operatorName,
            @RequestParam(required = false) BusSeatType seatType,
            @RequestParam(required = false) AcType acType,
            @PageableDefault(size = 20, sort = "busId") Pageable pageable) {
        return ResponseEntity.ok(busService.getBuses(busNumber, operatorName, seatType, acType, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(busService.getBus(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusResponseDto> update(@PathVariable Long id, @Valid @RequestBody BusRequestDto dto) {
        return ResponseEntity.ok(busService.updateBus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }

    // Seat APIs
    @PostMapping("/{id}/seats")
    public List<SeatResponseDto> bulkCreateSeats(
            @PathVariable Long id,
            @Valid @RequestBody List<SeatRequestDto> dtos) {
        return seatService.bulkCreate(id, dtos);  // Direct call
    }

    @PutMapping("/{busId}/seats/{seatId}")
    public SeatResponseDto updateSeat(
            @PathVariable Long busId,
            @PathVariable Long seatId,
            @Valid @RequestBody SeatRequestDto dto) {
        return seatService.update(busId, seatId, dto);
    }

    @DeleteMapping("/{busId}/seats/{seatId}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long busId, @PathVariable Long seatId) {
        seatService.delete(busId, seatId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{busId}/seats")
    public ResponseEntity<List<SeatResponseDto>> getSeatsWithPrice(
            @PathVariable Long busId,
            @RequestParam Long routeId) {
        return ResponseEntity.ok(busService.getSeatsWithPrice(busId, routeId));
    }

}
