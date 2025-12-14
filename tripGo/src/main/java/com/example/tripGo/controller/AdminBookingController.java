package com.example.tripGo.controller;

import com.example.tripGo.dto.BookingCancellationResponseDto;
import com.example.tripGo.dto.BusBookingResponseDto;
import com.example.tripGo.dto.BusBookingStatsDto;
import com.example.tripGo.dto.ChartDataDto;
import com.example.tripGo.entity.Bus;
import com.example.tripGo.repository.BookingRepository;
import com.example.tripGo.repository.BusRepository;
import com.example.tripGo.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final BookingService bookingService;
    private final BusRepository busRepository;
    private final BookingRepository bookingRepository;

    /**
     * Get all bookings for a specific bus
     */
    @GetMapping("/bus/{busId}")
    public ResponseEntity<Page<BusBookingResponseDto>> getBookingsByBus(
            @PathVariable Long busId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<BusBookingResponseDto> bookings = bookingService.getBookingsByBus(
                busId, startDate, endDate, status, page, size);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get booking statistics for a bus
     */
    @GetMapping("/bus/{busId}/stats")
    public ResponseEntity<BusBookingStatsDto> getBookingStatsByBus(
            @PathVariable Long busId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        BusBookingStatsDto stats = bookingService.getBookingStatsByBus(busId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get booking details (admin view)
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BusBookingResponseDto> getBookingDetails(
            @PathVariable Long bookingId) {

        BusBookingResponseDto booking = bookingService.getBookingDetailsForAdmin(bookingId);
        return ResponseEntity.ok(booking);
    }

    /**
     * Cancel booking by admin
     */
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingCancellationResponseDto> cancelBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody CancelBookingRequestDto request) {

        BookingCancellationResponseDto response = bookingService.cancelBookingByAdmin(
                bookingId, request.getReason());
        return ResponseEntity.ok(response);
    }

    /**
     * Search bookings across all buses
     */
    @GetMapping("/search")
    public ResponseEntity<Page<BusBookingResponseDto>> searchBookings(
            @RequestParam(required = false) String referenceNumber,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) String busNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<BusBookingResponseDto> bookings = bookingService.searchBookings(
                referenceNumber, contactEmail, contactPhone, busNumber, journeyDate, page, size);
        return ResponseEntity.ok(bookings);
    }

    // DTO for cancel request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelBookingRequestDto {
        @NotBlank(message = "Cancellation reason is required")
        private String reason;
    }

    /**
     * Get bookings by day for chart
     */
    @GetMapping("/charts/daily")
    public ResponseEntity<ChartDataDto> getDailyBookingsChart(
            @RequestParam(defaultValue = "7") int days) {

        ChartDataDto chartData = bookingService.getDailyBookingCounts(days);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Get booking status distribution
     */
    @GetMapping("/charts/status")
    public ResponseEntity<ChartDataDto> getStatusDistribution() {

        ChartDataDto chartData = bookingService.getStatusDistribution();
        return ResponseEntity.ok(chartData);
    }

    /**
     * Download booking receipt (PDF) - Admin version
     */
    @GetMapping("/{bookingId}/receipt")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long bookingId) throws Exception {

        // For admin, we don't need email verification
        // You can get admin email from security context if needed
        String adminEmail = getCurrentAdminEmail();

        byte[] receipt = bookingService.generateReceipt(bookingId, adminEmail);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"booking_" + bookingId + "_receipt.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(receipt);
    }

    private String getCurrentAdminEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


}
