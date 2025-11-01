package com.example.tripGo.entity;

import com.example.tripGo.entity.type.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;  // Which schedule this booking is for

    @ManyToOne
    @JoinColumn(name = "boarding_point_id")
    private RoutePoint boardingPoint;

    @ManyToOne
    @JoinColumn(name = "dropping_point_id")
    private RoutePoint droppingPoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus = BookingStatus.BOOKED;

    @Column(nullable = false)
    private LocalDateTime bookingDate = LocalDateTime.now();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passenger> passengers;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;
}
