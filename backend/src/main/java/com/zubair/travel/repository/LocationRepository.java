package com.zubair.travel.repository;

import com.zubair.travel.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByCountry(String country);
    List<Location> findByCity(String city);
    List<Location> findByNameContainingIgnoreCase(String name);
}
