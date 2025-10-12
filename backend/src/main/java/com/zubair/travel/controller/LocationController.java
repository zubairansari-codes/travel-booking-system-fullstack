package com.zubair.travel.controller;

import com.zubair.travel.entity.Location;
import com.zubair.travel.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // Create new location (admin)
    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        Location createdLocation = locationService.createLocation(location);
        return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
    }

    // Get all locations
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    // Get location by ID
    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        Location location = locationService.getLocationById(id);
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    // Get locations by city
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Location>> getLocationsByCity(@PathVariable String city) {
        List<Location> locations = locationService.getLocationsByCity(city);
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    // Get locations by country
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Location>> getLocationsByCountry(@PathVariable String country) {
        List<Location> locations = locationService.getLocationsByCountry(country);
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    // Get locations by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Location>> getLocationsByType(@PathVariable String type) {
        List<Location> locations = locationService.getLocationsByType(type);
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    // Search locations by keyword
    @GetMapping("/search")
    public ResponseEntity<List<Location>> searchLocations(@RequestParam String keyword) {
        List<Location> locations = locationService.searchLocations(keyword);
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    // Update location (admin)
    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody Location location) {
        Location updatedLocation = locationService.updateLocation(id, location);
        return new ResponseEntity<>(updatedLocation, HttpStatus.OK);
    }

    // Delete location (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
