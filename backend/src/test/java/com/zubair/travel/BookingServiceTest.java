package com.zubair.travel;

import com.zubair.travel.entity.Booking;
import com.zubair.travel.entity.User;
import com.zubair.travel.repository.BookingRepository;
import com.zubair.travel.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setUser(testUser);
        testBooking.setDestination("Paris");
        testBooking.setStartDate(LocalDate.of(2025, 6, 1));
        testBooking.setEndDate(LocalDate.of(2025, 6, 10));
        testBooking.setTotalPrice(1500.00);
        testBooking.setStatus("CONFIRMED");
    }

    @Test
    public void testCreateBooking_Success() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking createdBooking = bookingService.createBooking(testBooking);

        assertNotNull(createdBooking);
        assertEquals("Paris", createdBooking.getDestination());
        assertEquals(1500.00, createdBooking.getTotalPrice());
        assertEquals("CONFIRMED", createdBooking.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testGetBookingById_Found() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));

        Optional<Booking> foundBooking = bookingService.getBookingById(1L);

        assertTrue(foundBooking.isPresent());
        assertEquals(1L, foundBooking.get().getId());
        assertEquals("Paris", foundBooking.get().getDestination());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetBookingById_NotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Booking> foundBooking = bookingService.getBookingById(99L);

        assertFalse(foundBooking.isPresent());
        verify(bookingRepository, times(1)).findById(99L);
    }

    @Test
    public void testGetAllBookings() {
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setDestination("London");
        booking2.setTotalPrice(1200.00);

        List<Booking> bookings = Arrays.asList(testBooking, booking2);
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> allBookings = bookingService.getAllBookings();

        assertEquals(2, allBookings.size());
        assertEquals("Paris", allBookings.get(0).getDestination());
        assertEquals("London", allBookings.get(1).getDestination());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    public void testGetBookingsByUserId() {
        List<Booking> userBookings = Arrays.asList(testBooking);
        when(bookingRepository.findByUserId(anyLong())).thenReturn(userBookings);

        List<Booking> bookings = bookingService.getBookingsByUserId(1L);

        assertEquals(1, bookings.size());
        assertEquals("Paris", bookings.get(0).getDestination());
        assertEquals(1L, bookings.get(0).getUser().getId());
        verify(bookingRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testUpdateBooking_Success() {
        Booking updatedBooking = new Booking();
        updatedBooking.setId(1L);
        updatedBooking.setDestination("Rome");
        updatedBooking.setTotalPrice(1800.00);
        updatedBooking.setStatus("PENDING");

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

        Booking result = bookingService.updateBooking(1L, updatedBooking);

        assertNotNull(result);
        assertEquals("Rome", result.getDestination());
        assertEquals(1800.00, result.getTotalPrice());
        assertEquals("PENDING", result.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testDeleteBooking_Success() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(bookingRepository).deleteById(anyLong());

        boolean result = bookingService.deleteBooking(1L);

        assertTrue(result);
        verify(bookingRepository, times(1)).existsById(1L);
        verify(bookingRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteBooking_NotFound() {
        when(bookingRepository.existsById(anyLong())).thenReturn(false);

        boolean result = bookingService.deleteBooking(99L);

        assertFalse(result);
        verify(bookingRepository, times(1)).existsById(99L);
        verify(bookingRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testCancelBooking_Success() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking cancelledBooking = bookingService.cancelBooking(1L);

        assertNotNull(cancelledBooking);
        assertEquals("CANCELLED", cancelledBooking.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testGetBookingsByStatus() {
        List<Booking> confirmedBookings = Arrays.asList(testBooking);
        when(bookingRepository.findByStatus(anyString())).thenReturn(confirmedBookings);

        List<Booking> bookings = bookingService.getBookingsByStatus("CONFIRMED");

        assertEquals(1, bookings.size());
        assertEquals("CONFIRMED", bookings.get(0).getStatus());
        verify(bookingRepository, times(1)).findByStatus("CONFIRMED");
    }

    @Test
    public void testCalculateTotalRevenue() {
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setTotalPrice(2000.00);
        booking2.setStatus("CONFIRMED");

        List<Booking> bookings = Arrays.asList(testBooking, booking2);
        when(bookingRepository.findAll()).thenReturn(bookings);

        double totalRevenue = bookingService.calculateTotalRevenue();

        assertEquals(3500.00, totalRevenue);
        verify(bookingRepository, times(1)).findAll();
    }
}
