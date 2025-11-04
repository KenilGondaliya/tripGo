package com.example.tripGo.service;

import com.example.tripGo.dto.BusRequestDto;
import com.example.tripGo.dto.BusResponseDto;
import com.example.tripGo.dto.SeatRequestDto;
import com.example.tripGo.dto.SeatResponseDto;
import com.example.tripGo.entity.Bus;
import com.example.tripGo.entity.Seat;
import com.example.tripGo.error.DuplicateResourceException;
import com.example.tripGo.error.ResourceNotFoundException;
import com.example.tripGo.repository.BusRepository;
import com.example.tripGo.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService {

    private final SeatRepository seatRepository;
    private final BusRepository busRepository;
    private final ModelMapper modelMapper;

    public List<SeatResponseDto> bulkCreate(Long busId, List<SeatRequestDto> dtos) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

        List<Seat> seats = dtos.stream()
                .map(dto -> {
                    Seat s = modelMapper.map(dto, Seat.class);
                    s.setBus(bus);
                    return s;
                })
                .toList();

        try {
            return seatRepository.saveAll(seats)
                    .stream()
                    .map(seat -> modelMapper.map(seat, SeatResponseDto.class))
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public SeatResponseDto update(Long busId, Long seatId, SeatRequestDto dto) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        if (!seat.getBus().getBusId().equals(busId)) {
            throw new ResourceNotFoundException("Seat does not belong to this bus");
        }

        modelMapper.map(dto, seat);
        return modelMapper.map(seatRepository.save(seat), SeatResponseDto.class);
    }

    public void delete(Long busId, Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        if (!seat.getBus().getBusId().equals(busId)) {
            throw new ResourceNotFoundException("Seat does not belong to this bus");
        }
        seatRepository.delete(seat);
    }
}
