package com.zubair.travel.repository;

import com.zubair.travel.model.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {
    List<Transport> findByType(String type);
    List<Transport> findByTourId(Long tourId);
    List<Transport> findByAvailableTrue();
}
