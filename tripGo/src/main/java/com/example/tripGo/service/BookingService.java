package com.example.tripGo.service;

import com.example.tripGo.dto.BookingRequestDto;
import com.example.tripGo.dto.BookingResponseDto;
import com.example.tripGo.dto.SeatBookingDto;
import com.example.tripGo.dto.SeatInfoDto;
import com.example.tripGo.entity.*;
import com.example.tripGo.entity.type.BookingStatus;
import com.example.tripGo.entity.type.PaymentStatus;
import com.example.tripGo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepo;
    private final BookingSeatRepository bsRepo;
    private final PassengerRepository passengerRepo;
    private final PaymentRepository paymentRepo;
    private final ScheduleRepository scheduleRepo;
    private final SeatPriceRepository priceRepo;
    private final SeatRepository seatRepo;
    private final SchedulePointRepository pointRepo;

    public BookingResponseDto createBooking(BookingRequestDto dto) {
        Schedule schedule = scheduleRepo.findById(dto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        SchedulePoint boarding = pointRepo.findById(dto.getBoardingPointId())
                .orElseThrow(() -> new RuntimeException("Boarding point not found"));
        SchedulePoint dropping = pointRepo.findById(dto.getDroppingPointId())
                .orElseThrow(() -> new RuntimeException("Dropping point not found"));

        Booking booking = Booking.builder()
                .schedule(schedule)
                .boardingPoint(boarding)
                .droppingPoint(dropping)
                .contactName(dto.getContactName())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getContactEmail())
                .bookingStatus(BookingStatus.PENDING)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        List<SeatInfoDto> seatInfos = new ArrayList<>();

        for (SeatBookingDto s : dto.getSeats()) {
            Seat seat = seatRepo.findById(s.getSeatId())
                    .orElseThrow(() -> new RuntimeException("Seat not found"));

            BigDecimal price = priceRepo.findByRouteAndSeat(schedule.getRoute(), seat)
                    .map(SeatPrice::getPrice)
                    .orElseThrow(() -> new RuntimeException("Price not set"));

            BookingSeat bs = BookingSeat.builder()
                    .booking(booking)
                    .seat(seat)
                    .price(price)
                    .build();

            Passenger p = Passenger.builder()
                    .name(s.getPassengerName())
                    .age(s.getPassengerAge())
                    .gender(String.valueOf(s.getPassengerGender()))
                    .bookingSeat(bs)
                    .build();

            bs.setPassenger(p);
            booking.getBookingSeats().add(bs);
            total = total.add(price);

            seatInfos.add(SeatInfoDto.builder()
                    .seatNumber(seat.getSeatNumber())
                    .price(price)
                    .passengerName(s.getPassengerName())
                    .build());
        }

        booking.setTotalAmount(total);
        Booking saved = bookingRepo.save(booking);

        // Optional: Create Payment
        Payment payment = Payment.builder()
                .booking(saved)
                .amount(total)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        paymentRepo.save(payment);

        BookingResponseDto resp = new BookingResponseDto();
        resp.setBookingId(saved.getBookingId());
        resp.setContactName(saved.getContactName());
        resp.setContactPhone(saved.getContactPhone());
        resp.setTotalAmount(total);
        resp.setStatus(saved.getBookingStatus().name());
        resp.setSeats(seatInfos);
        return resp;
    }
}
