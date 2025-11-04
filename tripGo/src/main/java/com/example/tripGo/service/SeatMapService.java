package com.example.tripGo.service;

import com.example.tripGo.dto.BulkSeatPriceRequestDto;
import com.example.tripGo.dto.SeatPriceRequestDto;
import com.example.tripGo.dto.SeatPriceResponseDto;
import com.example.tripGo.entity.*;
import com.example.tripGo.error.ResourceNotFoundException;
import com.example.tripGo.repository.RouteRepository;
import com.example.tripGo.repository.ScheduleRepository;
import com.example.tripGo.repository.SeatPriceRepository;
import com.example.tripGo.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatMapService {

    private final SeatPriceRepository priceRepo;
    private final RouteRepository routeRepo;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepo;
    private final ModelMapper mapper;

    public SeatPrice create(SeatPriceRequestDto dto) {
        Route route = routeRepo.findById(dto.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));
        Seat seat = seatRepo.findById(dto.getSeatId())
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        SeatPrice price = priceRepo.findByRoute_RouteIdAndSeat_SeatId(dto.getRouteId(), dto.getSeatId())
                .orElse(new SeatPrice());

        price.setRoute(route);
        price.setSeat(seat);
        price.setPrice(dto.getPrice());

        return priceRepo.save(price);
    }

    public SeatPrice update(Long priceId, BigDecimal newPrice) {
        SeatPrice price = priceRepo.findById(priceId)
                .orElseThrow(() -> new RuntimeException("Price not found"));
        price.setPrice(newPrice);
        return priceRepo.save(price);
    }

    public void delete(Long priceId) {
        priceRepo.deleteById(priceId);
    }

    public void setBulkPrice(BulkSeatPriceRequestDto dto) {
        Route route = routeRepo.findById(dto.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        // Get all schedules for this route
        List<Schedule> schedules = scheduleRepository.findByRoute_RouteId(dto.getRouteId());
        if (schedules.isEmpty()) {
            throw new ResourceNotFoundException("No schedules found for route: " + dto.getRouteId());
        }

        // Extract bus IDs
        List<Long> busIds = schedules.stream()
                .map(s -> s.getBus().getBusId())
                .distinct()
                .toList();

        // Get all seats from these buses
        List<Seat> seats = seatRepo.findByBus_BusIdIn(busIds);

        List<SeatPrice> prices = seats.stream()
                .map(seat -> {
                    SeatPrice sp = priceRepo
                            .findByRoute_RouteIdAndSeat_SeatId(dto.getRouteId(), seat.getSeatId())
                            .orElse(SeatPrice.builder()
                                    .route(route)
                                    .seat(seat)
                                    .build());
                    sp.setPrice(dto.getPrice());
                    return sp;
                })
                .toList();

        priceRepo.saveAll(prices);
    }
}
