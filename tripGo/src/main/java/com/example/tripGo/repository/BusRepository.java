package com.example.tripGo.repository;

import com.example.tripGo.entity.Bus;
import com.example.tripGo.entity.type.AcType;
import com.example.tripGo.entity.type.BusSeatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    boolean existsByBusNumber(String busNumber);

    Page<Bus> findByBusNumberContainingIgnoreCaseAndOperatorNameContainingIgnoreCaseAndSeatTypeAndAcType(
            String busNumber, String operatorName, BusSeatType seatType, AcType acType, Pageable pageable);
}