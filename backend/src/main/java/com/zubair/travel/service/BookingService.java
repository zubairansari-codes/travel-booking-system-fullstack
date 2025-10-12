package com.zubair.travel.service;

import com.zubair.travel.entity.Booking;
import com.zubair.travel.entity.User;
import com.zubair.travel.entity.Tour;
import com.zubair.travel.repository.BookingRepository;
import com.zubair.travel.repository.UserRepository;
import com.zubair.travel.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private TourService tourService;

    // CRUD Operations

    public Booking createBooking(Booking booking) {
        validateBooking(booking);
        
        User user = userRepository.findById(booking.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + booking.getUser().getId()));
        
        Tour tour = tourRepository.findById(booking.getTour().getId())
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + booking.getTour().getId()));
        
        if (!tourService.isTourAvailable(tour.getId(), booking.getNumberOfPeople())) {
            throw new BookingNotAvailableException("Tour is not available for " + booking.getNumberOfPeople() + " people");
        }
        
        booking.setUser(user);
        booking.setTour(tour);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("PENDING");
        
        // Reserve seats
        tourService.bookSeat(tour.getId(), booking.getNumberOfPeople());
        
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking updateBooking(Long id, Booking bookingDetails) {
        Booking booking = getBookingById(id);
        
        if ("CONFIRMED".equals(booking.getStatus()) || "CANCELLED".equals(booking.getStatus())) {
            throw new BookingUpdateNotAllowedException("Cannot update a " + booking.getStatus() + " booking");
        }
        
        // If number of people changed, adjust seats
        if (!booking.getNumberOfPeople().equals(bookingDetails.getNumberOfPeople())) {
            int difference = bookingDetails.getNumberOfPeople() - booking.getNumberOfPeople();
            if (difference > 0) {
                tourService.bookSeat(booking.getTour().getId(), difference);
            } else {
                tourService.releaseSeat(booking.getTour().getId(), Math.abs(difference));
            }
            booking.setNumberOfPeople(bookingDetails.getNumberOfPeople());
        }
        
        booking.setSpecialRequests(bookingDetails.getSpecialRequests());
        
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);
        if ("CONFIRMED".equals(booking.getStatus())) {
            throw new BookingDeletionNotAllowedException("Cannot delete a confirmed booking. Please cancel it first.");
        }
        
        // Release seats if booking was pending
        if ("PENDING".equals(booking.getStatus())) {
            tourService.releaseSeat(booking.getTour().getId(), booking.getNumberOfPeople());
        }
        
        bookingRepository.delete(booking);
    }

    // Main Business Flows

    public List<Booking> getBookingsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return bookingRepository.findByUser(user);
    }

    public List<Booking> getBookingsByTour(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + tourId));
        return bookingRepository.findByTour(tour);
    }

    public List<Booking> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    public Booking confirmBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        
        if (!"PENDING".equals(booking.getStatus())) {
            throw new InvalidBookingStatusException("Only PENDING bookings can be confirmed");
        }
        
        booking.setStatus("CONFIRMED");
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new InvalidBookingStatusException("Booking is already cancelled");
        }
        
        // Release seats
        tourService.releaseSeat(booking.getTour().getId(), booking.getNumberOfPeople());
        
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    public List<Booking> getPendingBookings() {
        return bookingRepository.findByStatus("PENDING");
    }

    public List<Booking> getConfirmedBookings() {
        return bookingRepository.findByStatus("CONFIRMED");
    }

    public Double calculateTotalRevenue() {
        List<Booking> confirmedBookings = getConfirmedBookings();
        return confirmedBookings.stream()
                .mapToDouble(booking -> booking.getTour().getPrice() * booking.getNumberOfPeople())
                .sum();
    }

    public Integer getTotalBookingCount() {
        return bookingRepository.findAll().size();
    }

    public Integer getBookingCountByUser(Long userId) {
        return getBookingsByUser(userId).size();
    }

    // Validation

    private void validateBooking(Booking booking) {
        if (booking == null) {
            throw new InvalidInputException("Booking cannot be null");
        }
        if (booking.getUser() == null || booking.getUser().getId() == null) {
            throw new InvalidInputException("User is required for booking");
        }
        if (booking.getTour() == null || booking.getTour().getId() == null) {
            throw new InvalidInputException("Tour is required for booking");
        }
        if (booking.getNumberOfPeople() == null || booking.getNumberOfPeople() <= 0) {
            throw new InvalidInputException("Number of people must be positive");
        }
    }

    // Business Exceptions

    public static class BookingNotFoundException extends RuntimeException {
        public BookingNotFoundException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class TourNotFoundException extends RuntimeException {
        public TourNotFoundException(String message) {
            super(message);
        }
    }

    public static class BookingNotAvailableException extends RuntimeException {
        public BookingNotAvailableException(String message) {
            super(message);
        }
    }

    public static class BookingUpdateNotAllowedException extends RuntimeException {
        public BookingUpdateNotAllowedException(String message) {
            super(message);
        }
    }

    public static class BookingDeletionNotAllowedException extends RuntimeException {
        public BookingDeletionNotAllowedException(String message) {
            super(message);
        }
    }

    public static class InvalidBookingStatusException extends RuntimeException {
        public InvalidBookingStatusException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
