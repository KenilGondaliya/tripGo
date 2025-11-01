package com.example.tripGo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @Column(nullable = false, length = 100)
    private String startPoint;

    @Column(nullable = false, length = 100)
    private String endPoint;

    private Integer totalDistance;

    private LocalTime totalTravelTime;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutePoint> routePoints = new ArrayList<>();

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatPrice> seatPrices = new ArrayList<>();

//    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Schedule> schedules;

}
