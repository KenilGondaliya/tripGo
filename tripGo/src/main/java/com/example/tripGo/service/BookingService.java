package com.example.tripGo.service;

import com.example.tripGo.dto.*;
import com.example.tripGo.entity.*;
import com.example.tripGo.entity.type.BookingStatus;
import com.example.tripGo.entity.type.PaymentStatus;
import com.example.tripGo.entity.type.SeatStatus;
import com.example.tripGo.error.InvalidOperationException;
import com.example.tripGo.error.ResourceNotFoundException;
import com.example.tripGo.error.UnauthorizedException;
import com.example.tripGo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final PassengerRepository passengerRepository;
    private final PaymentRepository paymentRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatPriceRepository seatPriceRepository;
    private final SeatRepository seatRepository;
    private final SchedulePointRepository schedulePointRepository;

    private final PdfService pdfService;

    public BookingResponseDto createBooking(BookingRequestDto dto) {
        // Validate schedule
        Schedule schedule = scheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        // Validate boarding and dropping points
        SchedulePoint boarding = schedulePointRepository.findById(dto.getBoardingPointId())
                .orElseThrow(() -> new ResourceNotFoundException("Boarding point not found"));
        SchedulePoint dropping = schedulePointRepository.findById(dto.getDroppingPointId())
                .orElseThrow(() -> new ResourceNotFoundException("Dropping point not found"));

        // Generate unique reference number
        String referenceNumber = "TRIP" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Create booking
        Booking booking = Booking.builder()
                .referenceNumber(referenceNumber)
                .schedule(schedule)
                .boardingPoint(boarding)
                .droppingPoint(dropping)
                .contactName(dto.getContactName())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getContactEmail())
                .bookingStatus(BookingStatus.CONFIRMED)
                .bookingDate(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        List<BookingSeat> bookingSeats = new ArrayList<>();

        // Process each seat booking
        for (SeatBookingDto seatDto : dto.getSeats()) {
            // Validate seat
            Seat seat = seatRepository.findById(seatDto.getSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID: " + seatDto.getSeatId()));

            // Check if seat is already booked
            boolean isSeatBooked = bookingSeatRepository.existsBySeatSeatIdAndBookingScheduleScheduleId(
                    seatDto.getSeatId(), schedule.getScheduleId());
            if (isSeatBooked) {
                throw new InvalidOperationException("Seat " + seat.getSeatNumber() + " is already booked");
            }

            // Get price
            BigDecimal price = seatPriceRepository.findByRouteAndSeat(schedule.getRoute(), seat)
                    .map(SeatPrice::getPrice)
                    .orElseThrow(() -> new InvalidOperationException(
                            "Price not set for seat " + seat.getSeatNumber() + " on route"));

            // Create booking seat
            BookingSeat bs = BookingSeat.builder()
                    .booking(booking)
                    .seat(seat)
                    .price(price)
                    .seatStatus(SeatStatus.BOOKED)
                    .build();

            // Create passenger
            Passenger passenger = Passenger.builder()
                    .name(seatDto.getPassengerName())
                    .age(seatDto.getPassengerAge())
                    .gender(seatDto.getPassengerGender())
                    .bookingSeat(bs)
                    .build();

            bs.setPassenger(passenger);
            bookingSeats.add(bs);
            total = total.add(price);
        }

        booking.setTotalAmount(total);
        booking.setBookingSeats(bookingSeats);
        Booking savedBooking = bookingRepository.save(booking);

        // Create payment record
        Payment payment = Payment.builder()
                .booking(savedBooking)
                .amount(total)
                .paymentStatus(PaymentStatus.SUCCESS)
                .paymentMode("ONLINE")
                .transactionId("TXN" + UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                .paymentDate(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        // Update seat availability
        for (BookingSeat bs : bookingSeats) {
            Seat seat = bs.getSeat();
            seat.setAvailable(false);
            seatRepository.save(seat);
        }

        return convertToResponseDto(savedBooking);
    }

    public BookingResponseDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        return convertToResponseDto(booking);
    }

    /**
     * Get all bookings for a customer by email
     */
    public List<BookingResponseDto> getBookingsByCustomerEmail(String email) {
        List<Booking> bookings = bookingRepository.findByContactEmailOrderByBookingDateDesc(email);
        return bookings.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated bookings with optional status filter
     */
    public Page<BookingResponseDto> getBookingsByCustomerEmailPaginated(
            String email, int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDate").descending());

        Page<Booking> bookings;
        if (status != null && !status.isEmpty()) {
            try {
                BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
                bookings = bookingRepository.findByContactEmailAndBookingStatusOrderByBookingDateDesc(
                        email, bookingStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new InvalidOperationException("Invalid status value: " + status);
            }
        } else {
            bookings = bookingRepository.findByContactEmailOrderByBookingDateDesc(email, pageable);
        }

        return bookings.map(this::convertToResponseDto);
    }

    /**
     * Get booked seats for a schedule
     */
    public List<BookedSeatsDto> getBookedSeatsBySchedule(Long scheduleId) {
        // Validate schedule exists
        scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with ID: " + scheduleId));

        List<BookingSeat> bookedSeats = bookingSeatRepository.findBookedSeatsBySchedule(scheduleId);

        return bookedSeats.stream()
                .map(bs -> BookedSeatsDto.builder()
                        .seatId(bs.getSeat().getSeatId())
                        .seatNumber(bs.getSeat().getSeatNumber())
                        .bookingId(bs.getBooking().getBookingId())
                        .passengerName(bs.getPassenger().getName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get detailed booking information
     */
    public BookingDetailResponseDto getBookingDetailsForCustomer(Long bookingId, String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify customer owns this booking
        if (!booking.getContactEmail().equals(email)) {
            throw new UnauthorizedException("You don't have access to this booking");
        }

        return convertToBookingDetailDto(booking);
    }

    /**
     * Cancel booking by customer
     */
    public BookingCancellationResponseDto cancelBookingByCustomer(Long bookingId, String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify customer owns this booking
        if (!booking.getContactEmail().equals(email)) {
            throw new UnauthorizedException("You don't have access to this booking");
        }

        // Check if booking can be cancelled
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new InvalidOperationException("Booking is already cancelled");
        }

        LocalDateTime journeyDateTime = LocalDateTime.of(
                booking.getSchedule().getJourneyDate(),
                booking.getSchedule().getStartTime());

        if (LocalDateTime.now().isAfter(journeyDateTime)) {
            throw new InvalidOperationException("Cannot cancel booking after journey has started");
        }

        // Calculate refund
        CancellationPolicyDto policy = getCancellationPolicyInternal(booking);

        // Update booking status
        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // Free up seats
        booking.getBookingSeats().forEach(bs -> {
            Seat seat = bs.getSeat();
            seat.setAvailable(true);
            seatRepository.save(seat);
        });

        // Process refund if paid
        if (booking.getPayment() != null &&
                booking.getPayment().getPaymentStatus() == PaymentStatus.SUCCESS) {
            processRefund(booking, policy.getRefundAmount());
        }

        // Prepare response
        return BookingCancellationResponseDto.builder()
                .bookingId(bookingId)
                .referenceNumber(booking.getReferenceNumber())
                .cancellationDate(LocalDateTime.now())
                .cancellationReason("Cancelled by customer")
                .originalAmount(booking.getTotalAmount())
                .refundAmount(policy.getRefundAmount())
                .refundStatus("INITIATED")
                .estimatedRefundDays(5)
                .build();
    }

    /**
     * Get cancellation policy and refund details
     */
    public CancellationPolicyDto getCancellationPolicy(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return getCancellationPolicyInternal(booking);
    }

    /**
     * Generate booking receipt (PDF)
     */
    public byte[] generateReceipt(Long bookingId, String email) {
        log.info("Generating receipt for booking {} for user {}", bookingId, email);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getContactEmail().equals(email)) {
            throw new UnauthorizedException("You don't have access to this booking");
        }

        try {
            // Use PDF service
            return pdfService.generateBookingReceiptPdf(booking);
        } catch (Exception e) {
            log.error("Failed to generate PDF receipt for booking {}, falling back to text", bookingId, e);

            // Fallback to text receipt
            try {
                String receiptContent = generateReceiptContent(booking);
                return receiptContent.getBytes(StandardCharsets.UTF_8);
            } catch (Exception ex) {
                log.error("Failed to generate text receipt for booking {}", bookingId, ex);
                throw new RuntimeException("Failed to generate receipt", ex);
            }
        }
    }

    // ========== PRIVATE HELPER METHODS ==========

    private BookingResponseDto convertToResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .bookingId(booking.getBookingId())
                .referenceNumber(booking.getReferenceNumber())
                .bookingDate(booking.getBookingDate())
                .status(booking.getBookingStatus().toString())
                .totalAmount(booking.getTotalAmount())
                .schedule(mapScheduleToDto(booking.getSchedule()))
                .boardingPoint(mapPointToDto(booking.getBoardingPoint()))
                .droppingPoint(mapPointToDto(booking.getDroppingPoint()))
                .contactName(booking.getContactName())
                .contactPhone(booking.getContactPhone())
                .contactEmail(booking.getContactEmail())
                .seats(booking.getBookingSeats().stream()
                        .map(this::convertToSeatPassengerDto)
                        .collect(Collectors.toList()))
                .paymentStatus(booking.getPayment() != null ?
                        booking.getPayment().getPaymentStatus().toString() : "PENDING")
                .paymentMode(booking.getPayment() != null ?
                        booking.getPayment().getPaymentMode() : null)
                .build();
    }

    private BookingDetailResponseDto convertToBookingDetailDto(Booking booking) {
        return BookingDetailResponseDto.builder()
                .bookingId(booking.getBookingId())
                .referenceNumber(booking.getReferenceNumber())
                .bookingDate(booking.getBookingDate())
                .status(booking.getBookingStatus().toString())
                .totalAmount(booking.getTotalAmount())
                .schedule(mapScheduleToDto(booking.getSchedule()))
                .boardingPoint(mapPointToDto(booking.getBoardingPoint()))
                .droppingPoint(mapPointToDto(booking.getDroppingPoint()))
                .contactName(booking.getContactName())
                .contactPhone(booking.getContactPhone())
                .contactEmail(booking.getContactEmail())
                .seats(booking.getBookingSeats().stream()
                        .map(this::convertToSeatPassengerDto)
                        .collect(Collectors.toList()))
                .paymentStatus(booking.getPayment() != null ?
                        booking.getPayment().getPaymentStatus().toString() : "PENDING")
                .paymentMode(booking.getPayment() != null ?
                        booking.getPayment().getPaymentMode() : null)
                .transactionId(booking.getPayment() != null ?
                        booking.getPayment().getTransactionId() : null)
                .build();
    }

    private ScheduleInfoDto mapScheduleToDto(Schedule schedule) {
        if (schedule == null) {
            return ScheduleInfoDto.builder()
                    .scheduleId(null)
                    .startPoint("N/A")
                    .endPoint("N/A")
                    .journeyDate(null)
                    .startTime(null)
                    .endTime(null)
                    .busNumber("N/A")
                    .operatorName("N/A")
                    .build();
        }

        Route route = schedule.getRoute();
        Bus bus = schedule.getBus();

        return ScheduleInfoDto.builder()
                .scheduleId(schedule.getScheduleId())
                .startPoint(route != null ? route.getStartPoint() : "N/A")
                .endPoint(route != null ? route.getEndPoint() : "N/A")
                .journeyDate(schedule.getJourneyDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .busNumber(bus != null ? bus.getBusNumber() : "N/A")
                .operatorName(bus != null && bus.getOperatorName() != null ?
                        bus.getOperatorName() : "N/A")
                .build();
    }
    private PointInfoDto mapPointToDto(SchedulePoint point) {
        return PointInfoDto.builder()
                .locationName(point.getLocationName())
                .arrivalTime(point.getArrivalTime())
                .departureTime(point.getDepartureTime())
                .build();
    }

    private SeatPassengerDto convertToSeatPassengerDto(BookingSeat bs) {
        return SeatPassengerDto.builder()
                .seatNumber(bs.getSeat().getSeatNumber())
                .passengerName(bs.getPassenger().getName())
                .passengerAge(bs.getPassenger().getAge())
                .passengerGender(bs.getPassenger().getGender())
                .price(bs.getPrice())
                .build();
    }

    private CancellationPolicyDto getCancellationPolicyInternal(Booking booking) {
        Schedule schedule = booking.getSchedule();
        if (schedule == null) {
            throw new InvalidOperationException("Schedule information not found for this booking");
        }

        LocalDateTime journeyDateTime = LocalDateTime.of(
                schedule.getJourneyDate(),
                schedule.getStartTime());

        LocalDateTime now = LocalDateTime.now();
        long hoursUntilJourney = ChronoUnit.HOURS.between(now, journeyDateTime);

        boolean canCancel = hoursUntilJourney > 0;
        String cancellationMessage;
        BigDecimal refundAmount;
        String refundPercentage;

        if (hoursUntilJourney <= 0) {
            cancellationMessage = "Journey has already started. Cancellation not allowed.";
            refundAmount = BigDecimal.ZERO;
            refundPercentage = "0%";
        } else if (hoursUntilJourney <= 2) {
            cancellationMessage = "Less than 2 hours to journey. 50% refund applicable.";
            refundAmount = booking.getTotalAmount().multiply(new BigDecimal("0.5"));
            refundPercentage = "50%";
        } else if (hoursUntilJourney <= 6) {
            cancellationMessage = "6 hours or less to journey. 75% refund applicable.";
            refundAmount = booking.getTotalAmount().multiply(new BigDecimal("0.75"));
            refundPercentage = "75%";
        } else {
            cancellationMessage = "More than 6 hours to journey. 100% refund applicable.";
            refundAmount = booking.getTotalAmount();
            refundPercentage = "100%";
        }

        return CancellationPolicyDto.builder()
                .bookingId(booking.getBookingId())
                .canCancel(canCancel)
                .hoursUntilJourney(hoursUntilJourney)
                .cancellationMessage(cancellationMessage)
                .refundPercentage(refundPercentage)
                .refundAmount(refundAmount)
                .deductedAmount(booking.getTotalAmount().subtract(refundAmount))
                .build();
    }

    private void processRefund(Booking booking, BigDecimal refundAmount) {
        Payment payment = booking.getPayment();

        if (payment != null) {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            payment.setRemarks("Refund initiated for booking cancellation: " + refundAmount);
            paymentRepository.save(payment);
        }
    }


    private String generateReceiptContent(Booking booking) {
        Schedule schedule = booking.getSchedule();
        Route route = schedule != null ? schedule.getRoute() : null;
        Bus bus = schedule != null ? schedule.getBus() : null;
        SchedulePoint boarding = booking.getBoardingPoint();
        SchedulePoint dropping = booking.getDroppingPoint();

        StringBuilder receipt = new StringBuilder();
        receipt.append("=======================================\n");
        receipt.append("           BOOKING RECEIPT            \n");
        receipt.append("=======================================\n");
        receipt.append("Reference No: ").append(booking.getReferenceNumber()).append("\n");
        receipt.append("Booking Date: ").append(booking.getBookingDate()).append("\n");
        receipt.append("Status: ").append(booking.getBookingStatus()).append("\n");
        receipt.append("Total Amount: ").append(booking.getTotalAmount()).append("\n");
        receipt.append("---------------------------------------\n");

        // Route information from associated Route entity
        if (route != null) {
            receipt.append("Route: ").append(route.getStartPoint())
                    .append(" to ").append(route.getEndPoint()).append("\n");
        } else {
            receipt.append("Route: Information not available\n");
        }

        // Schedule information
        if (schedule != null) {
            receipt.append("Journey Date: ").append(schedule.getJourneyDate()).append("\n");
            receipt.append("Departure Time: ").append(schedule.getStartTime()).append("\n");
            receipt.append("Arrival Time: ").append(schedule.getEndTime()).append("\n");

            if (bus != null) {
                receipt.append("Bus Number: ").append(bus.getBusNumber()).append("\n");
            }
        }

        // Boarding and dropping points
        receipt.append("Boarding Point: ").append(boarding != null ? boarding.getLocationName() : "N/A").append("\n");
        receipt.append("Dropping Point: ").append(dropping != null ? dropping.getLocationName() : "N/A").append("\n");

        // Contact information
        receipt.append("---------------------------------------\n");
        receipt.append("Contact Name: ").append(booking.getContactName()).append("\n");
        receipt.append("Contact Phone: ").append(booking.getContactPhone()).append("\n");
        receipt.append("Contact Email: ").append(booking.getContactEmail()).append("\n");

        // Seats information
        receipt.append("---------------------------------------\n");
        receipt.append("Seats Booked:\n");
        booking.getBookingSeats().forEach(bs -> {
            receipt.append("- Seat ").append(bs.getSeat().getSeatNumber())
                    .append(": ").append(bs.getPassenger().getName())
                    .append(" (₹").append(bs.getPrice()).append(")\n");
        });

        receipt.append("=======================================\n");
        return receipt.toString();
    }
}
