package com.example.tripGo.repository;

import com.example.tripGo.entity.Bus;
import com.example.tripGo.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByBus_BusId(Long busId);
    List<Seat> findByBus(Bus bus);
    List<Seat> findByBus_BusIdIn(List<Long> busIds);
}