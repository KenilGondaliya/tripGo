package com.example.tripGo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "schedule_points")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SchedulePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schedulePointId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "point_id", nullable = false)
    private RoutePoint routePoint;

    private LocalTime arrivalTime;
    private LocalTime departureTime;

    @Column(nullable = false)
    private boolean isBoardingPoint = false;

    @Column(nullable = false)
    private boolean isDroppingPoint = false;

}
