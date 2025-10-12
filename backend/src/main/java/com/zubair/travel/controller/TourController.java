package com.zubair.travel.controller;

import com.zubair.travel.entity.Tour;
import com.zubair.travel.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@CrossOrigin(origins = "*")
public class TourController {

    @Autowired
    private TourService tourService;

    // Create new tour (admin)
    @PostMapping
    public ResponseEntity<Tour> createTour(@RequestBody Tour tour) {
        Tour createdTour = tourService.createTour(tour);
        return new ResponseEntity<>(createdTour, HttpStatus.CREATED);
    }

    // Get all tours
    @GetMapping
    public ResponseEntity<List<Tour>> getAllTours() {
        List<Tour> tours = tourService.getAllTours();
        return new ResponseEntity<>(tours, HttpStatus.OK);
    }

    // Get tour by ID
    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable Long id) {
        Tour tour = tourService.getTourById(id);
        return new ResponseEntity<>(tour, HttpStatus.OK);
    }

    // Get tours by destination
    @GetMapping("/destination/{destination}")
    public ResponseEntity<List<Tour>> getToursByDestination(@PathVariable String destination) {
        List<Tour> tours = tourService.getToursByDestination(destination);
        return new ResponseEntity<>(tours, HttpStatus.OK);
    }

    // Get tours by price range
    @GetMapping("/price-range")
    public ResponseEntity<List<Tour>> getToursByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        List<Tour> tours = tourService.getToursByPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(tours, HttpStatus.OK);
    }

    // Get available tours
    @GetMapping("/available")
    public ResponseEntity<List<Tour>> getAvailableTours() {
        List<Tour> tours = tourService.getAvailableTours();
        return new ResponseEntity<>(tours, HttpStatus.OK);
    }

    // Update tour (admin)
    @PutMapping("/{id}")
    public ResponseEntity<Tour> updateTour(@PathVariable Long id, @RequestBody Tour tour) {
        Tour updatedTour = tourService.updateTour(id, tour);
        return new ResponseEntity<>(updatedTour, HttpStatus.OK);
    }

    // Delete tour (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Search tours by keyword
    @GetMapping("/search")
    public ResponseEntity<List<Tour>> searchTours(@RequestParam String keyword) {
        List<Tour> tours = tourService.searchTours(keyword);
        return new ResponseEntity<>(tours, HttpStatus.OK);
    }
}
