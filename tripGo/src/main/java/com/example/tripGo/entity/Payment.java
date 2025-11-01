package com.example.tripGo.entity;

import com.example.tripGo.entity.type.PaymentStatus;
import com.example.tripGo.entity.type.SeatType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long     paymentId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private LocalDateTime paymentTime;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
