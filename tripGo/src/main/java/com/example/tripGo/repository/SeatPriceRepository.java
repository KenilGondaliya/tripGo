package com.example.tripGo.repository;

import com.example.tripGo.entity.Route;
import com.example.tripGo.entity.Seat;
import com.example.tripGo.entity.SeatPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatPriceRepository extends JpaRepository<SeatPrice, Long> {

    Optional<SeatPrice> findByRouteAndSeat(Route route, Seat seat);
}