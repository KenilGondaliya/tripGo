package com.example.tripGo.repository;

import com.example.tripGo.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Page<Route> findByStartPointContainingIgnoreCaseAndEndPointContainingIgnoreCase(
            String start, String end, Pageable p);

    Page<Route> findByBusBusNumberContainingIgnoreCaseAndStartPointContainingIgnoreCaseAndEndPointContainingIgnoreCase(
            String busNumber, String start, String end, Pageable p);
}