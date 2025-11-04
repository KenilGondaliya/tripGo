package com.example.tripGo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    @Column(nullable = false, length = 200)
    private String locationName;

    private LocalTime arrivalTime;
    private LocalTime departureTime;

    @Column(nullable = false)
    private boolean isBoardingPoint = false;

    @Column(nullable = false)
    private boolean isDroppingPoint = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
