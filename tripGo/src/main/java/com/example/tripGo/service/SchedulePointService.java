package com.example.tripGo.service;

import com.example.tripGo.dto.SchedulePointRequestDto;
import com.example.tripGo.dto.SchedulePointResponseDto;
import com.example.tripGo.entity.Schedule;
import com.example.tripGo.entity.SchedulePoint;
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

    public List<SchedulePointResponseDto> addPoints(Long scheduleId, List<SchedulePointRequestDto> dtos) {
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

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

        return pointRepo.saveAll(points).stream()
                .map(p -> mapper.map(p, SchedulePointResponseDto.class))
                .toList();
    }

    public SchedulePointResponseDto updatePoint(Long pointId, SchedulePointRequestDto dto) {
        SchedulePoint point = pointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Point not found"));

        point.setLocationName(dto.getLocationName());
        point.setArrivalTime(dto.getArrivalTime());
        point.setDepartureTime(dto.getDepartureTime());
        return mapper.map(pointRepo.save(point), SchedulePointResponseDto.class);
    }

    public void deletePoint(Long pointId) {
        pointRepo.deleteById(pointId);
    }

    public List<SchedulePointResponseDto> getBoardingPoints(Long scheduleId) {
        return pointRepo.findBySchedule_ScheduleIdAndIsBoardingPointTrue(scheduleId)
                .stream()
                .map(p -> mapper.map(p, SchedulePointResponseDto.class))
                .toList();
    }

    public List<SchedulePointResponseDto> getDroppingPoints(Long scheduleId) {
        return pointRepo.findBySchedule_ScheduleIdAndIsDroppingPointTrue(scheduleId)
                .stream()
                .map(p -> mapper.map(p, SchedulePointResponseDto.class))
                .toList();
    }

}
