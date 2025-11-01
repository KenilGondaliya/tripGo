package com.example.tripGo.entity;

import com.example.tripGo.entity.type.AcType;
import com.example.tripGo.entity.type.BusSeatType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long busId;

    @Column(nullable = false, unique = true, length = 20)
    private String busNumber;

    private String busType; // e.g., Volvo, Express

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusSeatType seatType; // SEATER, SLEEPER, SEMI_SLEEPER, MIXED

    private String operatorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcType acType; // AC / NON_AC

    @Column(nullable = false)
    private Integer totalSeats;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Seat> seats = new ArrayList<>();

}