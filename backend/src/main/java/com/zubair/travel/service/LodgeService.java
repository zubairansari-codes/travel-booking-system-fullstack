package com.zubair.travel.service;

import com.zubair.travel.entity.Lodge;
import com.zubair.travel.entity.Location;
import com.zubair.travel.repository.LodgeRepository;
import com.zubair.travel.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LodgeService {

    @Autowired
    private LodgeRepository lodgeRepository;

    @Autowired
    private LocationRepository locationRepository;

    // CRUD Operations

    public Lodge createLodge(Lodge lodge) {
        validateLodge(lodge);
        
        if (lodge.getLocation() != null && lodge.getLocation().getId() != null) {
            Location location = locationRepository.findById(lodge.getLocation().getId())
                    .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + lodge.getLocation().getId()));
            lodge.setLocation(location);
        }
        
        return lodgeRepository.save(lodge);
    }

    public Lodge getLodgeById(Long id) {
        return lodgeRepository.findById(id)
                .orElseThrow(() -> new LodgeNotFoundException("Lodge not found with id: " + id));
    }

    public List<Lodge> getAllLodges() {
        return lodgeRepository.findAll();
    }

    public Lodge updateLodge(Long id, Lodge lodgeDetails) {
        Lodge lodge = getLodgeById(id);
        validateLodge(lodgeDetails);
        
        lodge.setName(lodgeDetails.getName());
        lodge.setType(lodgeDetails.getType());
        lodge.setAddress(lodgeDetails.getAddress());
        lodge.setContactNumber(lodgeDetails.getContactNumber());
        lodge.setPricePerNight(lodgeDetails.getPricePerNight());
        lodge.setTotalRooms(lodgeDetails.getTotalRooms());
        lodge.setAvailableRooms(lodgeDetails.getAvailableRooms());
        lodge.setAmenities(lodgeDetails.getAmenities());
        lodge.setRating(lodgeDetails.getRating());
        
        if (lodgeDetails.getLocation() != null && lodgeDetails.getLocation().getId() != null) {
            Location location = locationRepository.findById(lodgeDetails.getLocation().getId())
                    .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + lodgeDetails.getLocation().getId()));
            lodge.setLocation(location);
        }
        
        return lodgeRepository.save(lodge);
    }

    public void deleteLodge(Long id) {
        Lodge lodge = getLodgeById(id);
        lodgeRepository.delete(lodge);
    }

    // Main Business Flows

    public List<Lodge> getLodgesByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
        return lodgeRepository.findByLocation(location);
    }

    public List<Lodge> getLodgesByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new InvalidInputException("Lodge type cannot be empty");
        }
        return lodgeRepository.findByType(type);
    }

    public List<Lodge> getAvailableLodges() {
        return lodgeRepository.findByAvailableRoomsGreaterThan(0);
    }

    public List<Lodge> getLodgesByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            throw new InvalidInputException("Invalid price range");
        }
        return lodgeRepository.findByPricePerNightBetween(minPrice, maxPrice);
    }

    public List<Lodge> searchLodgesByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new InvalidInputException("Search keyword cannot be empty");
        }
        return lodgeRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Lodge> getLodgesByRating(Double minRating) {
        if (minRating < 0 || minRating > 5) {
            throw new InvalidInputException("Rating must be between 0 and 5");
        }
        return lodgeRepository.findByRatingGreaterThanEqual(minRating);
    }

    public Lodge bookRoom(Long lodgeId, int numberOfRooms) {
        Lodge lodge = getLodgeById(lodgeId);
        
        if (numberOfRooms <= 0) {
            throw new InvalidInputException("Number of rooms must be positive");
        }
        
        if (lodge.getAvailableRooms() < numberOfRooms) {
            throw new InsufficientRoomsException("Only " + lodge.getAvailableRooms() + " rooms available");
        }
        
        lodge.setAvailableRooms(lodge.getAvailableRooms() - numberOfRooms);
        return lodgeRepository.save(lodge);
    }

    public Lodge releaseRoom(Long lodgeId, int numberOfRooms) {
        Lodge lodge = getLodgeById(lodgeId);
        
        if (numberOfRooms <= 0) {
            throw new InvalidInputException("Number of rooms must be positive");
        }
        
        int newAvailableRooms = lodge.getAvailableRooms() + numberOfRooms;
        if (newAvailableRooms > lodge.getTotalRooms()) {
            throw new InvalidInputException("Cannot exceed total rooms of " + lodge.getTotalRooms());
        }
        
        lodge.setAvailableRooms(newAvailableRooms);
        return lodgeRepository.save(lodge);
    }

    public boolean isLodgeAvailable(Long lodgeId, int numberOfRooms) {
        Lodge lodge = getLodgeById(lodgeId);
        return lodge.getAvailableRooms() >= numberOfRooms;
    }

    public List<Lodge> getTopRatedLodges() {
        return lodgeRepository.findByRatingGreaterThanEqual(4.0);
    }

    public Double calculateAveragePriceByLocation(Long locationId) {
        List<Lodge> lodges = getLodgesByLocation(locationId);
        if (lodges.isEmpty()) {
            return 0.0;
        }
        return lodges.stream()
                .mapToDouble(Lodge::getPricePerNight)
                .average()
                .orElse(0.0);
    }

    // Validation

    private void validateLodge(Lodge lodge) {
        if (lodge == null) {
            throw new InvalidInputException("Lodge cannot be null");
        }
        if (lodge.getName() == null || lodge.getName().trim().isEmpty()) {
            throw new InvalidInputException("Lodge name is required");
        }
        if (lodge.getType() == null || lodge.getType().trim().isEmpty()) {
            throw new InvalidInputException("Lodge type is required");
        }
        if (lodge.getAddress() == null || lodge.getAddress().trim().isEmpty()) {
            throw new InvalidInputException("Lodge address is required");
        }
        if (lodge.getPricePerNight() == null || lodge.getPricePerNight() < 0) {
            throw new InvalidInputException("Price per night must be non-negative");
        }
        if (lodge.getTotalRooms() == null || lodge.getTotalRooms() <= 0) {
            throw new InvalidInputException("Total rooms must be positive");
        }
        if (lodge.getAvailableRooms() == null || lodge.getAvailableRooms() < 0) {
            throw new InvalidInputException("Available rooms cannot be negative");
        }
        if (lodge.getAvailableRooms() > lodge.getTotalRooms()) {
            throw new InvalidInputException("Available rooms cannot exceed total rooms");
        }
        if (lodge.getRating() != null && (lodge.getRating() < 0 || lodge.getRating() > 5)) {
            throw new InvalidInputException("Rating must be between 0 and 5");
        }
    }

    // Business Exceptions

    public static class LodgeNotFoundException extends RuntimeException {
        public LodgeNotFoundException(String message) {
            super(message);
        }
    }

    public static class LocationNotFoundException extends RuntimeException {
        public LocationNotFoundException(String message) {
            super(message);
        }
    }

    public static class InsufficientRoomsException extends RuntimeException {
        public InsufficientRoomsException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
