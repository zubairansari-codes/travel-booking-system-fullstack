package com.zubair.travel.repository;

import com.zubair.travel.model.Lodge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LodgeRepository extends JpaRepository<Lodge, Long> {
    List<Lodge> findByLocationId(Long locationId);
    List<Lodge> findByType(String type);
    List<Lodge> findByAvailableTrue();
}
