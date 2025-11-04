package com.example.tripGo.repository;

import com.example.tripGo.entity.SchedulePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchedulePointRepository extends JpaRepository<SchedulePoint, Long> {
    List<SchedulePoint> findBySchedule_ScheduleIdOrderByDepartureTimeAsc(Long scheduleId);
    List<SchedulePoint> findBySchedule_ScheduleIdAndIsBoardingPointTrue(Long scheduleId);
    List<SchedulePoint> findBySchedule_ScheduleIdAndIsDroppingPointTrue(Long scheduleId);
}