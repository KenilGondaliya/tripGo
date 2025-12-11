package com.example.tripGo.controller;

import com.example.tripGo.dto.*;
import com.example.tripGo.entity.Customer;
import com.example.tripGo.error.ResourceNotFoundException;
import com.example.tripGo.service.BookingService;
import com.example.tripGo.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @Valid @RequestBody BookingRequestDto bookingRequest) {
        BookingResponseDto response = bookingService.createBooking(bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Add authorization check if needed
        BookingResponseDto response = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponseDto>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<BookingResponseDto> bookings = bookingService.getBookingsByCustomerEmail(userDetails.getUsername());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/schedule/{scheduleId}/booked-seats")
    public ResponseEntity<List<BookedSeatsDto>> getBookedSeats(
            @PathVariable Long scheduleId) {
        List<BookedSeatsDto> bookedSeats = bookingService.getBookedSeatsBySchedule(scheduleId);
        return ResponseEntity.ok(bookedSeats);
    }

    @GetMapping("/my-bookings/paginated")
    public ResponseEntity<Page<BookingResponseDto>> getMyBookingsPaginated(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Page<BookingResponseDto> bookings = bookingService.getBookingsByCustomerEmailPaginated(
                userDetails.getUsername(), page, size, status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{bookingId}/details")
    public ResponseEntity<BookingDetailResponseDto> getBookingDetails(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingDetailResponseDto details = bookingService.getBookingDetailsForCustomer(bookingId, userDetails.getUsername());
        return ResponseEntity.ok(details);
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingCancellationResponseDto> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingCancellationResponseDto response = bookingService.cancelBookingByCustomer(
                bookingId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}/cancellation-policy")
    public ResponseEntity<CancellationPolicyDto> getCancellationPolicy(
            @PathVariable Long bookingId) {
        CancellationPolicyDto policy = bookingService.getCancellationPolicy(bookingId);
        return ResponseEntity.ok(policy);
    }

    @GetMapping("/{bookingId}/receipt")
    public ResponseEntity<byte[]> downloadReceipt(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        byte[] receipt = bookingService.generateReceipt(bookingId, userDetails.getUsername());

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"booking_" + bookingId + "_receipt.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(receipt);
    }
}