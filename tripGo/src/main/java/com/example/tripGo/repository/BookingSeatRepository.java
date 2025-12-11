package com.example.tripGo.repository;

import com.example.tripGo.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    List<BookingSeat> findByBookingScheduleScheduleId(Long scheduleId);

    boolean existsBySeatSeatIdAndBookingScheduleScheduleId(Long seatId, Long scheduleId);

    @Query("SELECT bs FROM BookingSeat bs WHERE bs.booking.schedule.scheduleId = :scheduleId " +
            "AND bs.seatStatus = 'BOOKED'")
    List<BookingSeat> findBookedSeatsBySchedule(@Param("scheduleId") Long scheduleId);
}