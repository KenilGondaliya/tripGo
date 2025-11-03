package com.example.tripGo.service;

import com.example.tripGo.dto.BusRequestDto;
import com.example.tripGo.dto.BusResponseDto;
import com.example.tripGo.entity.Bus;
import com.example.tripGo.entity.type.AcType;
import com.example.tripGo.entity.type.BusSeatType;
import com.example.tripGo.error.DuplicateResourceException;
import com.example.tripGo.error.ResourceNotFoundException;
import com.example.tripGo.repository.BusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BusService {

    private final BusRepository busRepository;
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
}
