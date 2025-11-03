package com.example.tripGo.repository;

import com.example.tripGo.entity.SchedulePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchedulePointRepository extends JpaRepository<SchedulePoint, Integer> {

    List<SchedulePoint> findBySchedule_ScheduleIdOrderByRoutePoint_SequenceNoAsc(Long scheduleId);
}