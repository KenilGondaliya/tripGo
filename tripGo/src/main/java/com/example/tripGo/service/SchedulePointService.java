package com.example.tripGo.service;

import com.example.tripGo.dto.SchedulePointRequestDto;
import com.example.tripGo.dto.SchedulePointResponseDto;
import com.example.tripGo.entity.Schedule;
import com.example.tripGo.entity.SchedulePoint;
import com.example.tripGo.error.ResourceNotFoundException;
import com.example.tripGo.repository.SchedulePointRepository;
import com.example.tripGo.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SchedulePointService {

    private final SchedulePointRepository pointRepo;
    private final ScheduleRepository scheduleRepo;
    private final ModelMapper mapper;

    // CREATE MULTIPLE POINTS
    public List<SchedulePointResponseDto> addPoints(Long scheduleId, List<SchedulePointRequestDto> dtos) {
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));

        List<SchedulePoint> points = dtos.stream()
                .map(dto -> SchedulePoint.builder()
                        .schedule(schedule)
                        .locationName(dto.getLocationName())
                        .arrivalTime(dto.getArrivalTime())
                        .departureTime(dto.getDepartureTime())
                        .isBoardingPoint(dto.isBoardingPoint())
                        .isDroppingPoint(dto.isDroppingPoint())
                        .build())
                .toList();

        List<SchedulePoint> savedPoints = pointRepo.saveAll(points);

        return savedPoints.stream()
                .map(this::toResponseDto)
                .toList();
    }


    // UPDATE SINGLE POINT - CRITICAL FIX
    public SchedulePointResponseDto updatePoint(Long pointId, SchedulePointRequestDto dto) {
        SchedulePoint point = pointRepo.findById(pointId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule point not found with id: " + pointId));

        // Update all fields including boolean flags
        point.setLocationName(dto.getLocationName());
        point.setArrivalTime(dto.getArrivalTime());
        point.setDepartureTime(dto.getDepartureTime());
        point.setBoardingPoint(dto.isBoardingPoint());  // ← FIX: Use setter, not setIs*
        point.setDroppingPoint(dto.isDroppingPoint());  // ← FIX: Use setter, not setIs*

        SchedulePoint updated = pointRepo.save(point);
        return toResponseDto(updated);
    }

    // DELETE POINT - WITH PROPER VERIFICATION
    public void deletePoint(Long pointId) {
        SchedulePoint point = pointRepo.findById(pointId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule point not found with id: " + pointId));

        pointRepo.deleteById(pointId);
    }

    // DELETE ALL POINTS FOR A SCHEDULE
    @Transactional
    public void deleteAllPointsForSchedule(Long scheduleId) {
        pointRepo.deleteBySchedule_ScheduleId(scheduleId);
    }

    // GET BOARDING POINTS
    public List<SchedulePointResponseDto> getBoardingPoints(Long scheduleId) {
        // Verify schedule exists
        scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));

        return pointRepo.findBySchedule_ScheduleIdAndIsBoardingPointTrue(scheduleId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    // GET DROPPING POINTS
    public List<SchedulePointResponseDto> getDroppingPoints(Long scheduleId) {
        // Verify schedule exists
        scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));

        return pointRepo.findBySchedule_ScheduleIdAndIsDroppingPointTrue(scheduleId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    private SchedulePointResponseDto toResponseDto(SchedulePoint point) {
        SchedulePointResponseDto dto = new SchedulePointResponseDto();
        dto.setId(point.getSchedulePointId());  // ← This is correct
        dto.setLocationName(point.getLocationName());
        dto.setArrivalTime(point.getArrivalTime());
        dto.setDepartureTime(point.getDepartureTime());
        dto.setBoardingPoint(point.isBoardingPoint());
        dto.setDroppingPoint(point.isDroppingPoint());
        return dto;
    }

}
