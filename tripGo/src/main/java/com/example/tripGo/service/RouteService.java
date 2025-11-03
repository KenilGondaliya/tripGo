package com.example.tripGo.service;

import com.example.tripGo.dto.RoutePointRequestDto;
import com.example.tripGo.dto.RoutePointResponseDto;
import com.example.tripGo.dto.RouteRequestDto;
import com.example.tripGo.dto.RouteResponseDto;
import com.example.tripGo.entity.Bus;
import com.example.tripGo.entity.Route;
import com.example.tripGo.entity.RoutePoint;
import com.example.tripGo.error.ResourceNotFoundException;
import com.example.tripGo.repository.BusRepository;
import com.example.tripGo.repository.RoutePointRepository;
import com.example.tripGo.repository.RouteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteService {

        private final RouteRepository routeRepository;
        private final RoutePointRepository routePointRepository;
        private final BusRepository busRepository;
        private final ModelMapper mapper;

    public RouteResponseDto create(RouteRequestDto dto) {
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

        Route route = mapper.map(dto, Route.class);
        route.setBus(bus);

        if (dto.getTotalTravelTime() != null) {
            route.setTotalTravelTime(dto.getTotalTravelTime());
        }

        try {
            Route saved = routeRepository.save(route);
            return mapper.map(saved, RouteResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace(); // 👈 This will show the exact error in the console
            throw e;
        }
    }


    public Page<RouteResponseDto> listAllRoutes(String start, String end, Pageable p) {
        Page<Route> page = routeRepository.findByStartPointContainingIgnoreCaseAndEndPointContainingIgnoreCase(
                start == null ? "" : start,
                end == null ? "" : end, p);
        return page.map(r -> mapper.map(r, RouteResponseDto.class));
    }

    public Page<RouteResponseDto> listByBusNumber(String busNumber, String start, String end, Pageable p) {
        Page<Route> page;
        if (!busNumber.isBlank()) {
            page = routeRepository.findByBusBusNumberContainingIgnoreCaseAndStartPointContainingIgnoreCaseAndEndPointContainingIgnoreCase(
                    busNumber, start, end, p);
        } else {
            page = routeRepository.findByStartPointContainingIgnoreCaseAndEndPointContainingIgnoreCase(start, end, p);
        }
        return page.map(r -> mapper.map(r, RouteResponseDto.class));
    }

    public RouteResponseDto get(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        return mapper.map(route, RouteResponseDto.class);
    }

    public RouteResponseDto update(Long id, RouteRequestDto dto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        if (!route.getBus().getBusId().equals(dto.getBusId())) {
            Bus bus = busRepository.findById(dto.getBusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
            route.setBus(bus);
        }

        mapper.map(dto, route);
        if (dto.getTotalTravelTime() != null) {
            route.setTotalTravelTime(dto.getTotalTravelTime());
        }

        return mapper.map(routeRepository.save(route), RouteResponseDto.class);
    }

    public void delete(Long id) {
        routeRepository.deleteById(id);
    }

    // --- Route Points ---
    public List<RoutePointResponseDto> addPoints(Long routeId, List<RoutePointRequestDto> dtos) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        AtomicInteger maxSeq = new AtomicInteger(routePointRepository.findByRouteRouteIdOrderBySequenceNoAsc(routeId)
                .stream()
                .mapToInt(RoutePoint::getSequenceNo)
                .max()
                .orElse(0));

        List<RoutePoint> points = dtos.stream()
                .map(dto -> {
                    RoutePoint p = mapper.map(dto, RoutePoint.class);
                    p.setRoute(route);
                    p.setSequenceNo(dto.getSequenceNo() != null ? dto.getSequenceNo() : maxSeq.incrementAndGet());
                    return p;
                })
                .toList();

        return routePointRepository.saveAll(points).stream()
                .map(p -> mapper.map(p, RoutePointResponseDto.class))
                .toList();
    }

    public RoutePointResponseDto updatePoint(Long routeId, Long pointId, RoutePointRequestDto dto) {
        RoutePoint point = routePointRepository.findById(pointId)
                .orElseThrow(() -> new ResourceNotFoundException("Point not found"));

        if (!point.getRoute().getRouteId().equals(routeId)) {
            throw new ResourceNotFoundException("Point not in route");
        }

        mapper.map(dto, point);
        if (dto.getSequenceNo() != null) {
            point.setSequenceNo(dto.getSequenceNo());
        }

        return mapper.map(routePointRepository.save(point), RoutePointResponseDto.class);
    }

    public void deletePoint(Long routeId, Long pointId) {
        RoutePoint point = routePointRepository.findById(pointId)
                .orElseThrow(() -> new ResourceNotFoundException("Point not found"));

        if (!point.getRoute().getRouteId().equals(routeId)) {
            throw new ResourceNotFoundException("Point not in route");
        }

        routePointRepository.delete(point);
    }
}
