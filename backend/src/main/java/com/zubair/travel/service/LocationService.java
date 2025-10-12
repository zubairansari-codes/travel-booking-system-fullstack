package com.zubair.travel.service;

import com.zubair.travel.entity.Location;
import com.zubair.travel.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    // CRUD Operations

    public Location createLocation(Location location) {
        validateLocation(location);
        return locationRepository.save(location);
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + id));
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location updateLocation(Long id, Location locationDetails) {
        Location location = getLocationById(id);
        validateLocation(locationDetails);
        
        location.setName(locationDetails.getName());
        location.setCountry(locationDetails.getCountry());
        location.setState(locationDetails.getState());
        location.setCity(locationDetails.getCity());
        location.setDescription(locationDetails.getDescription());
        location.setClimate(locationDetails.getClimate());
        location.setBestTimeToVisit(locationDetails.getBestTimeToVisit());
        location.setPopularAttractions(locationDetails.getPopularAttractions());
        
        return locationRepository.save(location);
    }

    public void deleteLocation(Long id) {
        Location location = getLocationById(id);
        locationRepository.delete(location);
    }

    // Main Business Flows

    public List<Location> getLocationsByCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new InvalidInputException("Country cannot be empty");
        }
        return locationRepository.findByCountry(country);
    }

    public List<Location> getLocationsByState(String state) {
        if (state == null || state.trim().isEmpty()) {
            throw new InvalidInputException("State cannot be empty");
        }
        return locationRepository.findByState(state);
    }

    public List<Location> getLocationsByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new InvalidInputException("City cannot be empty");
        }
        return locationRepository.findByCity(city);
    }

    public List<Location> searchLocationsByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new InvalidInputException("Search keyword cannot be empty");
        }
        return locationRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Location> getLocationsByClimate(String climate) {
        if (climate == null || climate.trim().isEmpty()) {
            throw new InvalidInputException("Climate cannot be empty");
        }
        return locationRepository.findByClimate(climate);
    }

    public List<Location> getPopularLocations() {
        // Returns locations that have popular attractions listed
        return locationRepository.findAll().stream()
                .filter(location -> location.getPopularAttractions() != null && 
                                  !location.getPopularAttractions().trim().isEmpty())
                .toList();
    }

    public Location getLocationByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Location name cannot be empty");
        }
        return locationRepository.findByName(name)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with name: " + name));
    }

    public boolean locationExists(String name) {
        return locationRepository.findByName(name).isPresent();
    }

    public List<Location> getLocationsByBestTimeToVisit(String timeRange) {
        if (timeRange == null || timeRange.trim().isEmpty()) {
            throw new InvalidInputException("Time range cannot be empty");
        }
        return locationRepository.findByBestTimeToVisitContainingIgnoreCase(timeRange);
    }

    public Long getTotalLocationCount() {
        return locationRepository.count();
    }

    public Long getLocationCountByCountry(String country) {
        return (long) getLocationsByCountry(country).size();
    }

    // Validation

    private void validateLocation(Location location) {
        if (location == null) {
            throw new InvalidInputException("Location cannot be null");
        }
        if (location.getName() == null || location.getName().trim().isEmpty()) {
            throw new InvalidInputException("Location name is required");
        }
        if (location.getCountry() == null || location.getCountry().trim().isEmpty()) {
            throw new InvalidInputException("Country is required");
        }
        if (location.getCity() == null || location.getCity().trim().isEmpty()) {
            throw new InvalidInputException("City is required");
        }
    }

    // Business Exceptions

    public static class LocationNotFoundException extends RuntimeException {
        public LocationNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
