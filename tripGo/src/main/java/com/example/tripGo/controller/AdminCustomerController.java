package com.example.tripGo.controller;

import com.example.tripGo.dto.*;
import com.example.tripGo.entity.Booking;
import com.example.tripGo.entity.Customer;
import com.example.tripGo.entity.User;
import com.example.tripGo.entity.type.RoleType;
import com.example.tripGo.error.ResourceNotFoundException;
import com.example.tripGo.repository.BookingRepository;
import com.example.tripGo.repository.CustomerRepository;
import com.example.tripGo.repository.UserRepository;
import com.example.tripGo.service.BookingService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    /**
     * Get all customers with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<Page<CustomerResponseDto>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "name_asc") String sort) {

        Pageable pageable = createPageable(page, size, sort);

        Page<Customer> customersPage;

        if (search != null && !search.isEmpty()) {
            // Search by name or email
            customersPage = customerRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, pageable);
        } else {
            customersPage = customerRepository.findAll(pageable);
        }

        // Convert to DTO with booking stats
        Page<CustomerResponseDto> responsePage = customersPage.map(this::convertToCustomerResponseDto);

        return ResponseEntity.ok(responsePage);
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDetailResponseDto> getCustomerById(@PathVariable Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        CustomerDetailResponseDto response = convertToCustomerDetailDto(customer);
        return ResponseEntity.ok(response);
    }

    /**
     * Update customer
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody UpdateCustomerRequest request) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Update customer fields
        if (request.getName() != null) {
            customer.setName(request.getName());
        }

        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
        }

        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }

        // Update user roles if provided and user exists
        if (customer.getUser() != null && request.getRoles() != null && !request.getRoles().isEmpty()) {
            customer.getUser().setRoles(request.getRoles());
        }

        Customer updatedCustomer = customerRepository.save(customer);
        return ResponseEntity.ok(convertToCustomerResponseDto(updatedCustomer));
    }

    /**
     * Delete customer
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Delete associated bookings first
        List<Booking> customerBookings = bookingRepository.findByContactEmail(customer.getEmail());
        bookingRepository.deleteAll(customerBookings);

        // Delete user account if exists
        if (customer.getUser() != null) {
            userRepository.delete(customer.getUser());
        }

        // Delete customer
        customerRepository.delete(customer);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get top customers by bookings
     */
    @GetMapping("/top")
    public ResponseEntity<List<CustomerResponseDto>> getTopCustomers(
            @RequestParam(defaultValue = "30") int period) {

        LocalDateTime startDate = LocalDateTime.now().minusDays(period);

        // Get all customers with their booking counts
        List<Customer> allCustomers = customerRepository.findAll();
        List<CustomerResponseDto> topCustomers = allCustomers.stream()
                .map(this::convertToCustomerResponseDto)
                .filter(c -> c.getTotalBookings() > 0)
                .sorted((a, b) -> b.getTotalBookings().compareTo(a.getTotalBookings()))
                .limit(10)
                .collect(Collectors.toList());

        return ResponseEntity.ok(topCustomers);
    }

    /**
     * Export customers to CSV
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCustomers() {
        List<Customer> customers = customerRepository.findAll();

        // Create CSV header
        StringBuilder csv = new StringBuilder();
        csv.append("Customer ID,Name,Email,Phone,Username,Status,Total Bookings,Total Spent,Last Booking\n");

        for (Customer customer : customers) {
            CustomerResponseDto dto = convertToCustomerResponseDto(customer);

            csv.append(customer.getCustomerId()).append(",")
                    .append(escapeCsv(customer.getName())).append(",")
                    .append(escapeCsv(customer.getEmail())).append(",")
                    .append(escapeCsv(customer.getPhone() != null ? customer.getPhone() : "")).append(",")
                    .append(escapeCsv(customer.getUser() != null ? customer.getUser().getUsername() : "")).append(",")
                    .append(customer.getUser() != null && customer.getUser().isEnabled() ? "Active" : "Inactive").append(",")
                    .append(dto.getTotalBookings()).append(",")
                    .append(dto.getTotalRevenue() != null ? dto.getTotalRevenue() : "0").append(",")
                    .append(dto.getLastBooking() != null ? dto.getLastBooking() : "Never")
                    .append("\n");
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=customers.csv")
                .header("Content-Type", "text/csv")
                .body(csv.toString().getBytes());
    }

    // Helper methods
    private Pageable createPageable(int page, int size, String sort) {
        Sort.Direction direction = Sort.Direction.ASC;
        String sortBy = "name";

        switch (sort) {
            case "name_desc":
                direction = Sort.Direction.DESC;
                sortBy = "name";
                break;
            case "recent":
                direction = Sort.Direction.DESC;
                sortBy = "customerId";
                break;
            case "oldest":
                direction = Sort.Direction.ASC;
                sortBy = "customerId";
                break;
            case "bookings_desc":
                // Note: This would require a custom query with booking stats
                // For now, sort by ID
                direction = Sort.Direction.DESC;
                sortBy = "customerId";
                break;
            case "bookings_asc":
                direction = Sort.Direction.ASC;
                sortBy = "customerId";
                break;
            default: // name_asc
                direction = Sort.Direction.ASC;
                sortBy = "name";
        }

        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    private CustomerResponseDto convertToCustomerResponseDto(Customer customer) {
        // Get booking statistics for this customer
        List<Booking> customerBookings = bookingRepository.findByContactEmail(customer.getEmail());

        Long totalBookings = (long) customerBookings.size();
        BigDecimal totalRevenue = customerBookings.stream()
                .map(Booking::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Optional<Booking> lastBooking = customerBookings.stream()
                .max(Comparator.comparing(Booking::getBookingDate));

        LocalDateTime lastBookingDate = lastBooking.map(Booking::getBookingDate).orElse(null);

        return CustomerResponseDto.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .user(customer.getUser() != null ?
                        UserDto.builder()
                                .username(customer.getUser().getUsername())
                                .active(customer.getUser().isEnabled()) // Use isEnabled() method
                                .roles((Set<RoleType>) customer.getUser().getRoles())
                                .build()
                        : null)
                .totalBookings(totalBookings)
                .totalRevenue(totalRevenue)
                .lastBooking(lastBookingDate)
                .build();
    }

    private CustomerDetailResponseDto convertToCustomerDetailDto(Customer customer) {
        CustomerResponseDto basicInfo = convertToCustomerResponseDto(customer);

        // Get recent bookings
        List<Booking> recentBookings = bookingRepository.findByContactEmailOrderByBookingDateDesc(
                customer.getEmail(), PageRequest.of(0, 10)).getContent();

        List<CustomerBookingDto> recentBookingDtos = recentBookings.stream()
                .map(this::convertToCustomerBookingDto)
                .collect(Collectors.toList());

        return CustomerDetailResponseDto.builder()
                .customerId(basicInfo.getCustomerId())
                .name(basicInfo.getName())
                .email(basicInfo.getEmail())
                .phone(basicInfo.getPhone())
                .user(basicInfo.getUser())
                .totalBookings(basicInfo.getTotalBookings())
                .totalRevenue(basicInfo.getTotalRevenue())
                .lastBooking(basicInfo.getLastBooking())
                .recentBookings(recentBookingDtos)
                .build();
    }

    private CustomerBookingDto convertToCustomerBookingDto(Booking booking) {
        return CustomerBookingDto.builder()
                .bookingId(booking.getBookingId())
                .referenceNumber(booking.getReferenceNumber())
                .bookingDate(booking.getBookingDate())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getBookingStatus().toString())
                .busNumber(booking.getSchedule() != null && booking.getSchedule().getBus() != null ?
                        booking.getSchedule().getBus().getBusNumber() : "N/A")
                .build();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }



    @PatchMapping("/{customerId}/roles")
    public ResponseEntity<CustomerResponseDto> updateCustomerRoles(
            @PathVariable Long customerId,
            @RequestBody Set<RoleType> roles) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getUser() == null) {
            throw new ResourceNotFoundException("Customer has no user account");
        }

        customer.getUser().setRoles(roles);
        customerRepository.save(customer);

        return ResponseEntity.ok(convertToCustomerResponseDto(customer));
    }


    // DTOs
    @Data
    public static class UpdateCustomerRequest {
        private String name;
        private String email;
        private String phone;

        @JsonCreator
        public UpdateCustomerRequest(@JsonProperty("name") String name,
                                     @JsonProperty("email") String email,
                                     @JsonProperty("phone") String phone,
                                     @JsonProperty("roles") Set<String> roles) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            if (roles != null) {
                this.roles = roles.stream()
                        .map(role -> RoleType.valueOf(role.toUpperCase()))
                        .collect(Collectors.toSet());
            }
        }

        private Set<RoleType> roles;

        // Or if you prefer to accept strings and convert manually:
        @JsonProperty("roles")
        public void setRolesFromStrings(Set<String> roleStrings) {
            if (roleStrings != null) {
                this.roles = roleStrings.stream()
                        .map(role -> RoleType.valueOf(role.toUpperCase()))
                        .collect(Collectors.toSet());
            }
        }
    }

}