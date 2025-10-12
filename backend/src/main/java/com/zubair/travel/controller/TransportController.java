package com.zubair.travel.controller;

import com.zubair.travel.entity.Transport;
import com.zubair.travel.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transports")
@CrossOrigin(origins = "*")
public class TransportController {

    @Autowired
    private TransportService transportService;

    // Create new transport (admin)
    @PostMapping
    public ResponseEntity<Transport> createTransport(@RequestBody Transport transport) {
        Transport createdTransport = transportService.createTransport(transport);
        return new ResponseEntity<>(createdTransport, HttpStatus.CREATED);
    }

    // Get all transports
    @GetMapping
    public ResponseEntity<List<Transport>> getAllTransports() {
        List<Transport> transports = transportService.getAllTransports();
        return new ResponseEntity<>(transports, HttpStatus.OK);
    }

    // Get transport by ID
    @GetMapping("/{id}")
    public ResponseEntity<Transport> getTransportById(@PathVariable Long id) {
        Transport transport = transportService.getTransportById(id);
        return new ResponseEntity<>(transport, HttpStatus.OK);
    }

    // Get transports by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Transport>> getTransportsByType(@PathVariable String type) {
        List<Transport> transports = transportService.getTransportsByType(type);
        return new ResponseEntity<>(transports, HttpStatus.OK);
    }

    // Get transports by tour ID
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<Transport>> getTransportsByTourId(@PathVariable Long tourId) {
        List<Transport> transports = transportService.getTransportsByTourId(tourId);
        return new ResponseEntity<>(transports, HttpStatus.OK);
    }

    // Get available transports
    @GetMapping("/available")
    public ResponseEntity<List<Transport>> getAvailableTransports() {
        List<Transport> transports = transportService.getAvailableTransports();
        return new ResponseEntity<>(transports, HttpStatus.OK);
    }

    // Search transports by keyword
    @GetMapping("/search")
    public ResponseEntity<List<Transport>> searchTransports(@RequestParam String keyword) {
        List<Transport> transports = transportService.searchTransports(keyword);
        return new ResponseEntity<>(transports, HttpStatus.OK);
    }

    // Update transport (admin)
    @PutMapping("/{id}")
    public ResponseEntity<Transport> updateTransport(@PathVariable Long id, @RequestBody Transport transport) {
        Transport updatedTransport = transportService.updateTransport(id, transport);
        return new ResponseEntity<>(updatedTransport, HttpStatus.OK);
    }

    // Delete transport (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransport(@PathVariable Long id) {
        transportService.deleteTransport(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
