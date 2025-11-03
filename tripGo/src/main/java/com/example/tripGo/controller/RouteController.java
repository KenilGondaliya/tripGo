package com.example.tripGo.controller;

import com.example.tripGo.dto.*;
import com.example.tripGo.service.BusService;
import com.example.tripGo.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;
    private final BusService busService;

    @PostMapping
    public ResponseEntity<RouteResponseDto> create(@Valid @RequestBody RouteRequestDto dto) {
        return ResponseEntity.status(201).body(routeService.create(dto));
    }

    @GetMapping("/all-routes")
    public Page<RouteResponseDto> listAllRoutes(
            @RequestParam(defaultValue = "") String start,
            @RequestParam(defaultValue = "") String end,
            @PageableDefault(size = 20) Pageable p) {
        return routeService.listAllRoutes(start, end, p);
    }

    @GetMapping
    public Page<RouteResponseDto> list(
            @RequestParam(defaultValue = "") String busNumber,
            @RequestParam(defaultValue = "") String start,
            @RequestParam(defaultValue = "") String end,
            @PageableDefault(size = 20, sort = "routeId") Pageable p) {
        return routeService.listByBusNumber(busNumber, start, end, p);
    }

    @GetMapping("/search-by-route")
    public Page<BusResponseDto> searchBusesByRoute(
            @RequestParam String from,
            @RequestParam String to,
            @PageableDefault(size = 20) Pageable p) {
        return busService.searchBusesByRoute(from, to, p);
    }

    @GetMapping("/{id}")
    public RouteResponseDto get(@PathVariable Long id) {
        return routeService.get(id);
    }

    @PutMapping("/{id}")
    public RouteResponseDto update(@PathVariable Long id, @Valid @RequestBody RouteRequestDto dto) {
        return routeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/points")
    public List<RoutePointResponseDto> addPoints(
            @PathVariable Long id,
            @Valid @RequestBody List<RoutePointRequestDto> dtos) {
        return routeService.addPoints(id, dtos);
    }

    @PutMapping("/{routeId}/points/{pointId}")
    public RoutePointResponseDto updatePoint(
            @PathVariable Long routeId,
            @PathVariable Long pointId,
            @Valid @RequestBody RoutePointRequestDto dto) {
        return routeService.updatePoint(routeId, pointId, dto);
    }

    @DeleteMapping("/{routeId}/points/{pointId}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long routeId, @PathVariable Long pointId) {
        routeService.deletePoint(routeId, pointId);
        return ResponseEntity.noContent().build();
    }

}
