package com.example.tripGo.repository;

import com.example.tripGo.entity.Booking;
import com.example.tripGo.entity.type.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

//    Optional<Booking> findByReferenceNumber(String referenceNumber);

    @Query("SELECT DISTINCT sb.seat.seatId FROM BookingSeat sb WHERE sb.booking.schedule.scheduleId = :scheduleId")
    List<Long> findBookedSeatIdsByScheduleId(@Param("scheduleId") Long scheduleId);


    List<Booking> findByContactEmailOrderByBookingDateDesc(String email);

    Page<Booking> findByContactEmailOrderByBookingDateDesc(String email, Pageable pageable);

    Page<Booking> findByContactEmailAndBookingStatusOrderByBookingDateDesc(
            String email, BookingStatus status, Pageable pageable);

    List<Booking> findByScheduleScheduleIdAndBookingStatusNot(Long scheduleId, BookingStatus status);
}