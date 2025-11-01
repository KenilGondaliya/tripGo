package com.example.tripGo.repository;

import com.example.tripGo.entity.SeatPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatPriceRepository extends JpaRepository<SeatPrice, Integer> {
}