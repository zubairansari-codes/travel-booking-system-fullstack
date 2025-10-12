package com.zubair.travel.repository;

import com.zubair.travel.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByTourId(Long tourId);
    List<Booking> findByStatus(String status);
}
