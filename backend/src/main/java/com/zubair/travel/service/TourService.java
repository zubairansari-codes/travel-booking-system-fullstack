package com.zubair.travel.service;

import com.zubair.travel.entity.Tour;
import com.zubair.travel.entity.Location;
import com.zubair.travel.repository.TourRepository;
import com.zubair.travel.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TourService {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private LocationRepository locationRepository;

    // CRUD Operations

    public Tour createTour(Tour tour) {
        validateTour(tour);
        if (tour.getLocation() != null && tour.getLocation().getId() != null) {
            Location location = locationRepository.findById(tour.getLocation().getId())
                    .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + tour.getLocation().getId()));
            tour.setLocation(location);
        }
        return tourRepository.save(tour);
    }

    public Tour getTourById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + id));
    }

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    public Tour updateTour(Long id, Tour tourDetails) {
        Tour tour = getTourById(id);
        validateTour(tourDetails);
        
        tour.setName(tourDetails.getName());
        tour.setDescription(tourDetails.getDescription());
        tour.setDuration(tourDetails.getDuration());
        tour.setPrice(tourDetails.getPrice());
        tour.setAvailableSeats(tourDetails.getAvailableSeats());
        tour.setStartDate(tourDetails.getStartDate());
        tour.setEndDate(tourDetails.getEndDate());
        
        if (tourDetails.getLocation() != null && tourDetails.getLocation().getId() != null) {
            Location location = locationRepository.findById(tourDetails.getLocation().getId())
                    .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + tourDetails.getLocation().getId()));
            tour.setLocation(location);
        }
        
        return tourRepository.save(tour);
    }

    public void deleteTour(Long id) {
        Tour tour = getTourById(id);
        tourRepository.delete(tour);
    }

    // Main Business Flows

    public List<Tour> getToursByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
        return tourRepository.findByLocation(location);
    }

    public List<Tour> getAvailableTours() {
        return tourRepository.findByAvailableSeatsGreaterThan(0);
    }

    public List<Tour> getToursByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            throw new InvalidInputException("Invalid price range");
        }
        return tourRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Tour> getToursByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidInputException("Start date must be before end date");
        }
        return tourRepository.findByStartDateBetween(startDate, endDate);
    }

    public List<Tour> searchToursByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new InvalidInputException("Search keyword cannot be empty");
        }
        return tourRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Tour bookSeat(Long tourId, int numberOfSeats) {
        Tour tour = getTourById(tourId);
        
        if (numberOfSeats <= 0) {
            throw new InvalidInputException("Number of seats must be positive");
        }
        
        if (tour.getAvailableSeats() < numberOfSeats) {
            throw new InsufficientSeatsException("Only " + tour.getAvailableSeats() + " seats available");
        }
        
        if (tour.getStartDate().isBefore(LocalDate.now())) {
            throw new TourExpiredException("Tour has already started");
        }
        
        tour.setAvailableSeats(tour.getAvailableSeats() - numberOfSeats);
        return tourRepository.save(tour);
    }

    public Tour releaseSeat(Long tourId, int numberOfSeats) {
        Tour tour = getTourById(tourId);
        
        if (numberOfSeats <= 0) {
            throw new InvalidInputException("Number of seats must be positive");
        }
        
        tour.setAvailableSeats(tour.getAvailableSeats() + numberOfSeats);
        return tourRepository.save(tour);
    }

    public boolean isTourAvailable(Long tourId, int numberOfSeats) {
        Tour tour = getTourById(tourId);
        return tour.getAvailableSeats() >= numberOfSeats && 
               !tour.getStartDate().isBefore(LocalDate.now());
    }

    // Validation

    private void validateTour(Tour tour) {
        if (tour == null) {
            throw new InvalidInputException("Tour cannot be null");
        }
        if (tour.getName() == null || tour.getName().trim().isEmpty()) {
            throw new InvalidInputException("Tour name is required");
        }
        if (tour.getPrice() == null || tour.getPrice() < 0) {
            throw new InvalidInputException("Tour price must be non-negative");
        }
        if (tour.getDuration() == null || tour.getDuration() <= 0) {
            throw new InvalidInputException("Tour duration must be positive");
        }
        if (tour.getAvailableSeats() == null || tour.getAvailableSeats() < 0) {
            throw new InvalidInputException("Available seats cannot be negative");
        }
        if (tour.getStartDate() == null || tour.getEndDate() == null) {
            throw new InvalidInputException("Tour dates are required");
        }
        if (tour.getStartDate().isAfter(tour.getEndDate())) {
            throw new InvalidInputException("Start date must be before end date");
        }
    }

    // Business Exceptions

    public static class TourNotFoundException extends RuntimeException {
        public TourNotFoundException(String message) {
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

    public static class TourExpiredException extends RuntimeException {
        public TourExpiredException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
