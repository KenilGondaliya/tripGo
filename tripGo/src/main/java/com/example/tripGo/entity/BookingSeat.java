package com.example.tripGo.entity;

import com.example.tripGo.entity.type.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_seats")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingSeatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @OneToOne(mappedBy = "bookingSeat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Passenger passenger;
}

