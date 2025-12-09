package com.example.tripGo.controller;

import com.example.tripGo.dto.*;
import com.example.tripGo.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleResponseDto> create(@Valid @RequestBody ScheduleRequestDto dto) {
        System.out.println("Received Schedule DTO: " + dto);
        return ResponseEntity.status(201).body(scheduleService.create(dto));
    }

    @GetMapping("/search")
    public Page<ScheduleResponseDto> search(
            @RequestParam String startPoint,
            @RequestParam String endPoint,
            @RequestParam LocalDate date,
            @PageableDefault Pageable p) {
        return scheduleService.search(date, startPoint, endPoint, p);
    }

    @GetMapping
    public Page<ScheduleResponseDto> list(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) Long busId,
            @RequestParam(required = false) Long routeId,
            @PageableDefault Pageable p) {
        return scheduleService.search(date, null, null, p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequestDto dto) {
        ScheduleResponseDto updated = scheduleService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> getById(@PathVariable Long id) {
        ScheduleResponseDto schedule = scheduleService.findById(id);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/{scheduleId}/seats")
    public ResponseEntity<List<SeatPriceResponseDto>> getSeatsWithPriceForSchedule(
            @PathVariable Long scheduleId) {

        return ResponseEntity.ok(scheduleService.getSeatsWithPriceForSchedule(scheduleId));
    }

//    @GetMapping("/{id}/seats")
//    public List<SeatPriceResponseDto> getSeatMap(@PathVariable Long id) {
//        return scheduleService.getSeatMap(id);
//    }

}
