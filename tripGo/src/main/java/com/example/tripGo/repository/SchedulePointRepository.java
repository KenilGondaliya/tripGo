package com.example.tripGo.repository;

import com.example.tripGo.entity.SchedulePoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchedulePointRepository extends JpaRepository<SchedulePoint, Integer> {
}