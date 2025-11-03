package com.example.tripGo.service;


import com.example.tripGo.dto.*;
import com.example.tripGo.entity.*;
import com.example.tripGo.error.ResourceNotFoundException;
import com.example.tripGo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final SchedulePointRepository schedulePointRepository;
    private final SeatPriceRepository seatPriceRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final SeatRepository seatRepository;
    private final RoutePointRepository routePointRepository;
    private final ModelMapper mapper;

    public ScheduleResponseDto create(ScheduleRequestDto dto) {
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
        Route route = routeRepository.findById(dto.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        Schedule schedule = Schedule.builder()
                .bus(bus)
                .route(route)
                .journeyDate(dto.getJourneyDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .totalTravelTime(dto.getTotalTravelTime())
                .build();

        Schedule saved = scheduleRepository.save(schedule);
        return toSummary(saved);
    }

    public Page<ScheduleResponseDto> search(LocalDate date, String start, String end, Pageable p) {
        Page<Schedule> page = scheduleRepository.searchSchedules(date, start, end, p);
        return page.map(this::toSummary);
    }

    public List<SchedulePointResponseDto> addPoints(Long scheduleId, List<SchedulePointRequestDto> dtos) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        List<SchedulePoint> points = dtos.stream()
                .map(dto -> {
                    RoutePoint rp = routePointRepository.findById(dto.getRoutePointId())
                            .orElseThrow(() -> new ResourceNotFoundException("RoutePoint not found"));
                    return SchedulePoint.builder()
                            .schedule(schedule)
                            .routePoint(rp)
                            .arrivalTime(dto.getArrivalTime())
                            .departureTime(dto.getDepartureTime())
                            .isBoardingPoint(dto.isBoardingPoint())
                            .isDroppingPoint(dto.isDroppingPoint())
                            .build();
                })
                .toList();

        return schedulePointRepository
                .saveAll(points).stream()
                .map(p -> {
                    SchedulePointResponseDto dto = new SchedulePointResponseDto();
                    dto.setLocationName(p.getRoutePoint().getLocationName());
                    dto.setArrival(p.getArrivalTime());
                    dto.setDeparture(p.getDepartureTime());
                    dto.setBoarding(p.isBoardingPoint());
                    dto.setDropping(p.isDroppingPoint());
                    return dto;
                })
                .toList();
    }

    private ScheduleResponseDto toSummary(Schedule s) {
        ScheduleResponseDto dto = new ScheduleResponseDto();
        dto.setScheduleId(s.getScheduleId());
        dto.setBusNumber(s.getBus().getBusNumber());
        dto.setOperatorName(s.getBus().getOperatorName());
        dto.setStartPoint(s.getRoute().getStartPoint());
        dto.setEndPoint(s.getRoute().getEndPoint());
        dto.setJourneyDate(s.getJourneyDate());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setAvailableSeats(s.getBus().getTotalSeats());

        List<SchedulePointResponseDto> points = schedulePointRepository
                .findBySchedule_ScheduleIdOrderByRoutePoint_SequenceNoAsc(s.getScheduleId())
                .stream()
                .map(p -> {
                    SchedulePointResponseDto pd = new SchedulePointResponseDto();
                    pd.setLocationName(p.getRoutePoint().getLocationName());
                    pd.setArrival(p.getArrivalTime());
                    pd.setDeparture(p.getDepartureTime());
                    pd.setBoarding(p.isBoardingPoint());
                    pd.setDropping(p.isDroppingPoint());
                    return pd;
                })
                .toList();

        dto.setPoints(points);
        return dto;
    }

    public List<SeatMapDto> getSeatMap(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        return seatRepository.findByBus(schedule.getBus()).stream()
                .map(seat -> {
                    BigDecimal price = seatPriceRepository.findByRouteAndSeat(schedule.getRoute(), seat)
                            .map(SeatPrice::getPrice)
                            .orElse(BigDecimal.ZERO);

                    SeatMapDto dto = new SeatMapDto();
                    dto.setSeatId(seat.getSeatId());
                    dto.setSeatNumber(seat.getSeatNumber());
                    dto.setSeatType(seat.getSeatType().name());
                    dto.setDeckType(seat.getDeckType() != null ? seat.getDeckType().name() : null);
                    dto.setAvailable(true);
                    dto.setPrice(price);
                    return dto;
                })
                .toList();
    }
}
