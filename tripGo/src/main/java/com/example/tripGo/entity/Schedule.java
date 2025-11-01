package com.example.tripGo.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private LocalDate journeyDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private LocalTime totalTravelTime;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SchedulePoint> schedulePoints;

//    // 🔁 One schedule can have many seat prices (for that day)
//    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<SeatPrice> seatPrices = new ArrayList<>();
}
