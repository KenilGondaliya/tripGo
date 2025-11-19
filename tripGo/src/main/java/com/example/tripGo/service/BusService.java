package com.example.tripGo.service;

import com.example.tripGo.dto.BusRequestDto;
import com.example.tripGo.dto.BusResponseDto;
import com.example.tripGo.dto.SeatPriceResponseDto;
import com.example.tripGo.dto.SeatResponseDto;
import com.example.tripGo.entity.Bus;
import com.example.tripGo.entity.Route;
import com.example.tripGo.entity.SeatPrice;
import com.example.tripGo.entity.type.AcType;
import com.example.tripGo.entity.type.BusSeatType;
import com.example.tripGo.error.DuplicateResourceException;
import com.example.tripGo.error.ResourceNotFoundException;
import com.example.tripGo.repository.BusRepository;
import com.example.tripGo.repository.RouteRepository;
import com.example.tripGo.repository.SeatPriceRepository;
import com.example.tripGo.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BusService {

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final SeatPriceRepository seatPriceRepository;
    private final SeatRepository seatRepository;
    private final SeatService seatService;
    private final ModelMapper mapper;

    public BusResponseDto createBus(BusRequestDto dto) {
        if (busRepository.existsByBusNumber(dto.getBusNumber())) {
            throw new DuplicateResourceException("Bus number exists");
        }
        Bus bus = mapper.map(dto, Bus.class);
        return mapper.map(busRepository.save(bus), BusResponseDto.class);
    }

    public Page<BusResponseDto> getBuses(String busNumber, String operatorName,
                                     BusSeatType seatType, AcType acType, Pageable p) {
        Page<Bus> page = busRepository.findByBusNumberContainingIgnoreCaseAndOperatorNameContainingIgnoreCaseAndSeatTypeAndAcType(busNumber, operatorName, seatType, acType, p);
        return page.map(b -> mapper.map(b, BusResponseDto.class));
    }

    public BusResponseDto getBus(Long id) {
        return busRepository.findById(id)
                .map(b -> mapper.map(b, BusResponseDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
    }

    public List<BusResponseDto> getAllBuses() {
        return busRepository.findAll().stream()
                .map(bus -> mapper.map(bus, BusResponseDto.class))
                .toList();
    }


    public BusResponseDto updateBus(Long id, BusRequestDto dto) {
        Bus bus = busRepository.findById(id).orElseThrow();
        if (!bus.getBusNumber().equals(dto.getBusNumber()) && busRepository.existsByBusNumber(dto.getBusNumber())) {
            throw new DuplicateResourceException("Bus number taken");
        }
        mapper.map(dto, bus);
        return mapper.map(busRepository.save(bus), BusResponseDto.class);
    }

    public void deleteBus(Long id) {
        busRepository.findById(id).ifPresent(busRepository::delete);
    }

    public Page<BusResponseDto> searchBusesByRoute(String from, String to, Pageable p) {
        Page<Bus> buses = busRepository.findByRoutesStartPointContainingIgnoreCaseAndRoutesEndPointContainingIgnoreCase(
                from, to, p);
        return buses.map(b -> mapper.map(b, BusResponseDto.class));
    }

    public List<SeatResponseDto> getSeatsWithPrice(Long busId, Long routeId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        return seatRepository.findByBus(bus).stream()
                .map(seat -> {
                    BigDecimal price = seatPriceRepository
                            .findByRoute_RouteIdAndSeat_SeatId(routeId, seat.getSeatId())
                            .map(SeatPrice::getPrice)
                            .orElse(BigDecimal.ZERO);

                    SeatResponseDto dto = new SeatResponseDto();  // ← NOW CORRECT TYPE
                    dto.setSeatId(seat.getSeatId());
                    dto.setSeatNumber(seat.getSeatNumber());
                    dto.setSeatType(seat.getSeatType());
                    dto.setDeckType(seat.getDeckType());
                    dto.setAvailable(seat.isAvailable());
                    dto.setPrice(price);                        // ← SET PRICE
                    dto.setCreatedAt(seat.getCreatedAt());
                    dto.setUpdatedAt(seat.getUpdatedAt());
                    return dto;
                })
                .toList();
    }
}
