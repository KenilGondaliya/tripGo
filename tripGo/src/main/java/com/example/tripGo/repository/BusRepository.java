package com.example.tripGo.repository;

import com.example.tripGo.entity.Bus;
import com.example.tripGo.entity.type.AcType;
import com.example.tripGo.entity.type.BusSeatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    boolean existsByBusNumber(String busNumber);
    List<Bus> findAll();

    Page<Bus> findByBusNumberContainingIgnoreCaseAndOperatorNameContainingIgnoreCaseAndSeatTypeAndAcType(
            String busNumber, String operatorName, BusSeatType seatType, AcType acType, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Bus b JOIN b.schedules s JOIN s.route r " +
            "WHERE LOWER(r.startPoint) LIKE LOWER(CONCAT('%', :from, '%')) " +
            "AND LOWER(r.endPoint) LIKE LOWER(CONCAT('%', :to, '%'))")
    Page<Bus> findByRoutesStartPointContainingIgnoreCaseAndRoutesEndPointContainingIgnoreCase(
            String from, String to, Pageable p);
}