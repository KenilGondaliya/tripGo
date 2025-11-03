package com.example.tripGo.repository;

import com.example.tripGo.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("""
        SELECT s FROM Schedule s
        WHERE (:date IS NULL OR s.journeyDate = :date)
        AND (:start IS NULL OR LOWER(s.route.startPoint) LIKE LOWER(CONCAT('%', :start, '%')))
        AND (:end IS NULL OR LOWER(s.route.endPoint) LIKE LOWER(CONCAT('%', :end, '%')))
        """)
    Page<Schedule> searchSchedules(
            @Param("date") LocalDate date,
            @Param("start") String start,
            @Param("end") String end,
            Pageable pageable);

}