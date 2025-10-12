package com.zubair.travel.service;

import com.zubair.travel.entity.Transport;
import com.zubair.travel.entity.Location;
import com.zubair.travel.repository.TransportRepository;
import com.zubair.travel.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransportService {

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private LocationRepository locationRepository;

    // CRUD Operations

    public Transport createTransport(Transport transport) {
        validateTransport(transport);
        
        if (transport.getFromLocation() != null && transport.getFromLocation().getId() != null) {
            Location fromLocation = locationRepository.findById(transport.getFromLocation().getId())
                    .orElseThrow(() -> new LocationNotFoundException("From location not found with id: " + transport.getFromLocation().getId()));
            transport.setFromLocation(fromLocation);
        }
        
        if (transport.getToLocation() != null && transport.getToLocation().getId() != null) {
            Location toLocation = locationRepository.findById(transport.getToLocation().getId())
                    .orElseThrow(() -> new LocationNotFoundException("To location not found with id: " + transport.getToLocation().getId()));
            transport.setToLocation(toLocation);
        }
        
        return transportRepository.save(transport);
    }

    public Transport getTransportById(Long id) {
        return transportRepository.findById(id)
                .orElseThrow(() -> new TransportNotFoundException("Transport not found with id: " + id));
    }

    public List<Transport> getAllTransports() {
        return transportRepository.findAll();
    }

    public Transport updateTransport(Long id, Transport transportDetails) {
        Transport transport = getTransportById(id);
        validateTransport(transportDetails);
        
        transport.setType(transportDetails.getType());
        transport.setProvider(transportDetails.getProvider());
        transport.setVehicleNumber(transportDetails.getVehicleNumber());
        transport.setCost(transportDetails.getCost());
        transport.setCapacity(transportDetails.getCapacity());
        transport.setAvailableSeats(transportDetails.getAvailableSeats());
        
        if (transportDetails.getFromLocation() != null && transportDetails.getFromLocation().getId() != null) {
            Location fromLocation = locationRepository.findById(transportDetails.getFromLocation().getId())
                    .orElseThrow(() -> new LocationNotFoundException("From location not found"));
            transport.setFromLocation(fromLocation);
        }
        
        if (transportDetails.getToLocation() != null && transportDetails.getToLocation().getId() != null) {
            Location toLocation = locationRepository.findById(transportDetails.getToLocation().getId())
                    .orElseThrow(() -> new LocationNotFoundException("To location not found"));
            transport.setToLocation(toLocation);
        }
        
        return transportRepository.save(transport);
    }

    public void deleteTransport(Long id) {
        Transport transport = getTransportById(id);
        transportRepository.delete(transport);
    }

    // Main Business Flows

    public List<Transport> getTransportsByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new InvalidInputException("Transport type cannot be empty");
        }
        return transportRepository.findByType(type);
    }

    public List<Transport> getTransportsByProvider(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            throw new InvalidInputException("Provider name cannot be empty");
        }
        return transportRepository.findByProvider(provider);
    }

    public List<Transport> getTransportsByRoute(Long fromLocationId, Long toLocationId) {
        Location fromLocation = locationRepository.findById(fromLocationId)
                .orElseThrow(() -> new LocationNotFoundException("From location not found with id: " + fromLocationId));
        Location toLocation = locationRepository.findById(toLocationId)
                .orElseThrow(() -> new LocationNotFoundException("To location not found with id: " + toLocationId));
        return transportRepository.findByFromLocationAndToLocation(fromLocation, toLocation);
    }

    public List<Transport> getAvailableTransports() {
        return transportRepository.findByAvailableSeatsGreaterThan(0);
    }

    public List<Transport> getTransportsByCostRange(Double minCost, Double maxCost) {
        if (minCost < 0 || maxCost < 0 || minCost > maxCost) {
            throw new InvalidInputException("Invalid cost range");
        }
        return transportRepository.findByCostBetween(minCost, maxCost);
    }

    public Transport bookTransportSeat(Long transportId, int numberOfSeats) {
        Transport transport = getTransportById(transportId);
        
        if (numberOfSeats <= 0) {
            throw new InvalidInputException("Number of seats must be positive");
        }
        
        if (transport.getAvailableSeats() < numberOfSeats) {
            throw new InsufficientSeatsException("Only " + transport.getAvailableSeats() + " seats available");
        }
        
        transport.setAvailableSeats(transport.getAvailableSeats() - numberOfSeats);
        return transportRepository.save(transport);
    }

    public Transport releaseTransportSeat(Long transportId, int numberOfSeats) {
        Transport transport = getTransportById(transportId);
        
        if (numberOfSeats <= 0) {
            throw new InvalidInputException("Number of seats must be positive");
        }
        
        int newAvailableSeats = transport.getAvailableSeats() + numberOfSeats;
        if (newAvailableSeats > transport.getCapacity()) {
            throw new InvalidInputException("Cannot exceed capacity of " + transport.getCapacity());
        }
        
        transport.setAvailableSeats(newAvailableSeats);
        return transportRepository.save(transport);
    }

    public boolean isTransportAvailable(Long transportId, int numberOfSeats) {
        Transport transport = getTransportById(transportId);
        return transport.getAvailableSeats() >= numberOfSeats;
    }

    public List<Transport> getTransportsFromLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
        return transportRepository.findByFromLocation(location);
    }

    public List<Transport> getTransportsToLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
        return transportRepository.findByToLocation(location);
    }

    // Validation

    private void validateTransport(Transport transport) {
        if (transport == null) {
            throw new InvalidInputException("Transport cannot be null");
        }
        if (transport.getType() == null || transport.getType().trim().isEmpty()) {
            throw new InvalidInputException("Transport type is required");
        }
        if (transport.getProvider() == null || transport.getProvider().trim().isEmpty()) {
            throw new InvalidInputException("Provider is required");
        }
        if (transport.getCost() == null || transport.getCost() < 0) {
            throw new InvalidInputException("Cost must be non-negative");
        }
        if (transport.getCapacity() == null || transport.getCapacity() <= 0) {
            throw new InvalidInputException("Capacity must be positive");
        }
        if (transport.getAvailableSeats() == null || transport.getAvailableSeats() < 0) {
            throw new InvalidInputException("Available seats cannot be negative");
        }
        if (transport.getAvailableSeats() > transport.getCapacity()) {
            throw new InvalidInputException("Available seats cannot exceed capacity");
        }
    }

    // Business Exceptions

    public static class TransportNotFoundException extends RuntimeException {
        public TransportNotFoundException(String message) {
            super(message);
        }
    }

    public static class LocationNotFoundException extends RuntimeException {
        public LocationNotFoundException(String message) {
            super(message);
        }
    }

    public static class InsufficientSeatsException extends RuntimeException {
        public InsufficientSeatsException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
