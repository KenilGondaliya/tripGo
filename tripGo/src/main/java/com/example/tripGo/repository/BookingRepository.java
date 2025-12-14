package com.example.tripGo.repository;

import com.example.tripGo.entity.Booking;
import com.example.tripGo.entity.type.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

//    Optional<Booking> findByReferenceNumber(String referenceNumber);

    @Query("SELECT DISTINCT sb.seat.seatId FROM BookingSeat sb WHERE sb.booking.schedule.scheduleId = :scheduleId")
    List<Long> findBookedSeatIdsByScheduleId(@Param("scheduleId") Long scheduleId);

    List<Booking> findByContactEmailOrderByBookingDateDesc(String email);

    Page<Booking> findByContactEmailOrderByBookingDateDesc(String email, Pageable pageable);

    Page<Booking> findByContactEmailAndBookingStatusOrderByBookingDateDesc(
            String email, BookingStatus status, Pageable pageable);

    // Add this method
    List<Booking> findByContactEmail(String email);

    List<Booking> findByScheduleScheduleIdAndBookingStatusNot(Long scheduleId, BookingStatus status);

    // Find bookings by bus ID
    @Query("SELECT b FROM Booking b WHERE b.schedule.bus.busId = :busId")
    Page<Booking> findByBusId(@Param("busId") Long busId, Pageable pageable);

    // Find bookings by bus ID with optional date range
    @Query("SELECT b FROM Booking b WHERE b.schedule.bus.busId = :busId " +
            "AND (:startDate IS NULL OR b.bookingDate >= :startDate) " +
            "AND (:endDate IS NULL OR b.bookingDate <= :endDate)")
    Page<Booking> findByBusIdAndDateRange(
            @Param("busId") Long busId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Find bookings by bus ID and status
    @Query("SELECT b FROM Booking b WHERE b.schedule.bus.busId = :busId " +
            "AND b.bookingStatus = :status")
    Page<Booking> findByBusIdAndStatus(
            @Param("busId") Long busId,
            @Param("status") BookingStatus status,
            Pageable pageable);

    // Get booking statistics by bus
    @Query("SELECT COUNT(b), SUM(b.totalAmount) FROM Booking b " +
            "WHERE b.schedule.bus.busId = :busId " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate " +
            "AND b.bookingStatus = 'CONFIRMED'")
    Object[] getBookingStatsByBus(
            @Param("busId") Long busId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // For statistics
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.schedule.bus.busId = :busId " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate " +
            "AND b.bookingStatus = :status")
    Long countByBusIdAndStatusAndDateRange(
            @Param("busId") Long busId,
            @Param("status") BookingStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get daily statistics
    @Query("SELECT DATE(b.bookingDate) as bookingDate, " +
            "COUNT(b) as bookingsCount, " +
            "SUM(b.totalAmount) as dailyRevenue, " +
            "COUNT(bs) as passengersCount " +
            "FROM Booking b " +
            "LEFT JOIN b.bookingSeats bs " +
            "WHERE b.schedule.bus.busId = :busId " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate " +
            "AND b.bookingStatus = 'CONFIRMED' " +
            "GROUP BY DATE(b.bookingDate) " +
            "ORDER BY DATE(b.bookingDate) DESC")
    List<Object[]> getDailyStatsByBus(
            @Param("busId") Long busId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get cancelled revenue
    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b " +
            "WHERE b.schedule.bus.busId = :busId " +
            "AND b.bookingDate BETWEEN :startDate AND :endDate " +
            "AND b.bookingStatus = 'CANCELLED'")
    BigDecimal getCancelledRevenueByBus(
            @Param("busId") Long busId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Combined search with all filters
    @Query("SELECT b FROM Booking b WHERE b.schedule.bus.busId = :busId " +
            "AND (:status IS NULL OR b.bookingStatus = :status) " +
            "AND (:startDate IS NULL OR b.bookingDate >= :startDate) " +
            "AND (:endDate IS NULL OR b.bookingDate <= :endDate)")
    Page<Booking> findByBusIdAndStatusAndDateRange(
            @Param("busId") Long busId,
            @Param("status") BookingStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);


    @Query("SELECT DATE(b.bookingDate) as bookingDate, COUNT(b) as count " +
            "FROM Booking b " +
            "WHERE b.bookingDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(b.bookingDate) " +
            "ORDER BY DATE(b.bookingDate)")
    List<Object[]> getDailyBookingCounts(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b.bookingStatus as status, COUNT(b) as count " +
            "FROM Booking b " +
            "GROUP BY b.bookingStatus")
    List<Object[]> getBookingStatusCounts();



}