package com.example.tripGo.controller;

import com.example.tripGo.dto.SchedulePointRequestDto;
import com.example.tripGo.dto.SchedulePointResponseDto;
import com.example.tripGo.service.SchedulePointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/schedules/{scheduleId}/points")
@RequiredArgsConstructor
public class SchedulePointController {

    private final SchedulePointService pointService;

    @PostMapping
    public List<SchedulePointResponseDto> addPoints(
            @PathVariable Long scheduleId,
            @Valid @RequestBody List<SchedulePointRequestDto> dtos) {
        return pointService.addPoints(scheduleId, dtos);
    }

    @PutMapping("/{pointId}")
    public SchedulePointResponseDto updatePoint(
            @PathVariable Long pointId,
            @Valid @RequestBody SchedulePointRequestDto dto) {
        return pointService.updatePoint(pointId, dto);
    }

    @DeleteMapping("/{pointId}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long pointId) {
        pointService.deletePoint(pointId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/boarding")
    public List<SchedulePointResponseDto> getBoardingPoints(@PathVariable Long scheduleId) {
        return pointService.getBoardingPoints(scheduleId);
    }

    @GetMapping("/dropping")
    public List<SchedulePointResponseDto> getDroppingPoints(@PathVariable Long scheduleId) {
        return pointService.getDroppingPoints(scheduleId);
    }
}
