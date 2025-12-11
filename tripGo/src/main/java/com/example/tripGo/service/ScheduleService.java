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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private final BookingRepository bookingRepository;
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

        // FIXED: Direct from SchedulePoint with proper ID
        List<SchedulePointResponseDto> points = schedulePointRepository
                .findBySchedule_ScheduleIdOrderByDepartureTimeAsc(s.getScheduleId())
                .stream()
                .map(p -> {
                    SchedulePointResponseDto pd = new SchedulePointResponseDto();
                    pd.setId(p.getSchedulePointId());  // ← THIS WAS MISSING!
                    pd.setLocationName(p.getLocationName());
                    pd.setArrivalTime(p.getArrivalTime());
                    pd.setDepartureTime(p.getDepartureTime());
                    pd.setBoardingPoint(p.isBoardingPoint());
                    pd.setDroppingPoint(p.isDroppingPoint());
                    return pd;
                })
                .toList();

        dto.setPoints(points);
        return dto;
    }

//    public List<SeatPriceResponseDto> getSeatMap(Long scheduleId) {
//        Schedule schedule = scheduleRepository.findById(scheduleId)
//                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
//
//        return seatRepository.findByBus(schedule.getBus()).stream()
//                .map(seat -> {
//                    BigDecimal price = seatPriceRepository.findByRouteAndSeat(schedule.getRoute(), seat)
//                            .map(SeatPrice::getPrice)
//                            .orElse(BigDecimal.ZERO);
//
//                    SeatPriceResponseDto dto = new SeatPriceResponseDto();
//                    dto.setSeatId(seat.getSeatId());
//                    dto.setSeatNumber(seat.getSeatNumber());
//                    dto.setSeatType(seat.getSeatType().name());
//                    dto.setDeckType(seat.getDeckType() != null ? seat.getDeckType().name() : null);
//                    dto.setAvailable(true);
//                    dto.setPrice(price);
//                    return dto;
//                })
//                .toList();
//    }

    public ScheduleResponseDto update(Long scheduleId, ScheduleRequestDto dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));

        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
        Route route = routeRepository.findById(dto.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        // Update fields
        schedule.setBus(bus);
        schedule.setRoute(route);
        schedule.setJourneyDate(dto.getJourneyDate());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setTotalTravelTime(dto.getTotalTravelTime());

        Schedule updated = scheduleRepository.save(schedule);
        return toSummary(updated);
    }

    @Transactional
    public void delete(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));

        // This will automatically delete all SchedulePoints due to cascade + orphanRemoval
        scheduleRepository.delete(schedule);
    }

    public ScheduleResponseDto findById(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));

        return toSummary(schedule);
    }


    public List<SeatPriceResponseDto> getSeatsWithPriceForSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        Bus bus = schedule.getBus();
        Route route = schedule.getRoute();

        // Get all booked seat IDs for this schedule
        List<Long> bookedSeatIds = bookingRepository.findBookedSeatIdsByScheduleId(scheduleId);

        return seatRepository.findByBus(bus).stream()
                .map(seat -> {
                    BigDecimal price = seatPriceRepository
                            .findByRouteAndSeat(route, seat)
                            .map(SeatPrice::getPrice)
                            .orElse(BigDecimal.valueOf(700));

                    SeatPriceResponseDto dto = new SeatPriceResponseDto();
                    dto.setSeatId(seat.getSeatId());
                    dto.setSeatNumber(seat.getSeatNumber());
                    dto.setSeatType(seat.getSeatType().toString());
                    dto.setDeckType(seat.getDeckType().toString());
                    dto.setPrice(price);

                    // Check if seat is already booked
                    boolean isBooked = bookedSeatIds.contains(seat.getSeatId());
                    dto.setAvailable(!isBooked);  // Available = not booked

                    return dto;
                })
                .sorted(Comparator.comparing(SeatPriceResponseDto::getSeatNumber))
                .collect(Collectors.toList());
    }
}
