package com.example.tripGo.repository;

import com.example.tripGo.entity.RoutePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {
    List<RoutePoint> findByRouteRouteIdOrderBySequenceNoAsc(Long routeId);

    @Modifying
    @Query("UPDATE RoutePoint p SET p.sequenceNo = :seq WHERE p.pointId = :id")
    void updateSequence(Long id, Integer seq);
}