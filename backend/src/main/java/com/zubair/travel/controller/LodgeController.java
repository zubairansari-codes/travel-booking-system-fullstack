package com.zubair.travel.controller;

import com.zubair.travel.entity.Lodge;
import com.zubair.travel.service.LodgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lodges")
@CrossOrigin(origins = "*")
public class LodgeController {

    @Autowired
    private LodgeService lodgeService;

    // Create new lodge (admin)
    @PostMapping
    public ResponseEntity<Lodge> createLodge(@RequestBody Lodge lodge) {
        Lodge createdLodge = lodgeService.createLodge(lodge);
        return new ResponseEntity<>(createdLodge, HttpStatus.CREATED);
    }

    // Get all lodges
    @GetMapping
    public ResponseEntity<List<Lodge>> getAllLodges() {
        List<Lodge> lodges = lodgeService.getAllLodges();
        return new ResponseEntity<>(lodges, HttpStatus.OK);
    }

    // Get lodge by ID
    @GetMapping("/{id}")
    public ResponseEntity<Lodge> getLodgeById(@PathVariable Long id) {
        Lodge lodge = lodgeService.getLodgeById(id);
        return new ResponseEntity<>(lodge, HttpStatus.OK);
    }

    // Get lodges by location
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Lodge>> getLodgesByLocation(@PathVariable String location) {
        List<Lodge> lodges = lodgeService.getLodgesByLocation(location);
        return new ResponseEntity<>(lodges, HttpStatus.OK);
    }

    // Get lodges by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Lodge>> getLodgesByType(@PathVariable String type) {
        List<Lodge> lodges = lodgeService.getLodgesByType(type);
        return new ResponseEntity<>(lodges, HttpStatus.OK);
    }

    // Get lodges by tour ID
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<Lodge>> getLodgesByTourId(@PathVariable Long tourId) {
        List<Lodge> lodges = lodgeService.getLodgesByTourId(tourId);
        return new ResponseEntity<>(lodges, HttpStatus.OK);
    }

    // Get lodges by price range
    @GetMapping("/price-range")
    public ResponseEntity<List<Lodge>> getLodgesByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        List<Lodge> lodges = lodgeService.getLodgesByPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(lodges, HttpStatus.OK);
    }

    // Get available lodges
    @GetMapping("/available")
    public ResponseEntity<List<Lodge>> getAvailableLodges() {
        List<Lodge> lodges = lodgeService.getAvailableLodges();
        return new ResponseEntity<>(lodges, HttpStatus.OK);
    }

    // Search lodges by keyword
    @GetMapping("/search")
    public ResponseEntity<List<Lodge>> searchLodges(@RequestParam String keyword) {
        List<Lodge> lodges = lodgeService.searchLodges(keyword);
        return new ResponseEntity<>(lodges, HttpStatus.OK);
    }

    // Update lodge (admin)
    @PutMapping("/{id}")
    public ResponseEntity<Lodge> updateLodge(@PathVariable Long id, @RequestBody Lodge lodge) {
        Lodge updatedLodge = lodgeService.updateLodge(id, lodge);
        return new ResponseEntity<>(updatedLodge, HttpStatus.OK);
    }

    // Delete lodge (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLodge(@PathVariable Long id) {
        lodgeService.deleteLodge(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
