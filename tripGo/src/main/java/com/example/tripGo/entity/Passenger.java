package com.example.tripGo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passengers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passengerId;

    private String name;
    private int age;
    private String gender;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;
}
