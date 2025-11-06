package com.example.tripGo.controller;

import com.example.tripGo.dto.BookingRequestDto;
import com.example.tripGo.dto.BookingResponseDto;
import com.example.tripGo.entity.Customer;
import com.example.tripGo.service.BookingService;
import com.example.tripGo.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAuthority('booking:write')")
    public ResponseEntity<BookingResponseDto> book(@Valid @RequestBody BookingRequestDto dto) {
        Customer customer = customerService.getCurrentCustomer();
        return ResponseEntity.status(201).body(bookingService.createBooking(dto));
    }
}
