package com.example.tripGo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "route_points")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RoutePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false, length = 100)
    private String locationName;

//    @Column(nullable = false)
//    private Integer sequenceNo;
}
