package com.example.tripGo.repository;

import com.example.tripGo.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Integer> {
}