package com.zubair.travel.repository;

import com.zubair.travel.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByLocationId(Long locationId);
    List<Tour> findByNameContainingIgnoreCase(String name);
    List<Tour> findByAvailableTrue();
}
