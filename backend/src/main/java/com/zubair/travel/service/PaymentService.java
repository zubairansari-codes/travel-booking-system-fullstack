package com.zubair.travel.service;

import com.zubair.travel.entity.Payment;
import com.zubair.travel.entity.Booking;
import com.zubair.travel.repository.PaymentRepository;
import com.zubair.travel.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    // CRUD Operations

    public Payment createPayment(Payment payment) {
        validatePayment(payment);
        
        Booking booking = bookingRepository.findById(payment.getBooking().getId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + payment.getBooking().getId()));
        
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new PaymentNotAllowedException("Cannot process payment for a cancelled booking");
        }
        
        // Check if payment already exists for this booking
        Optional<Payment> existingPayment = paymentRepository.findByBooking(booking);
        if (existingPayment.isPresent() && "COMPLETED".equals(existingPayment.get().getPaymentStatus())) {
            throw new PaymentAlreadyExistsException("Payment already completed for this booking");
        }
        
        payment.setBooking(booking);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("PENDING");
        
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment updatePayment(Long id, Payment paymentDetails) {
        Payment payment = getPaymentById(id);
        
        if ("COMPLETED".equals(payment.getPaymentStatus())) {
            throw new PaymentUpdateNotAllowedException("Cannot update a completed payment");
        }
        
        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        payment.setAmount(paymentDetails.getAmount());
        
        return paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);
        
        if ("COMPLETED".equals(payment.getPaymentStatus())) {
            throw new PaymentDeletionNotAllowedException("Cannot delete a completed payment");
        }
        
        paymentRepository.delete(payment);
    }

    // Main Business Flows

    public Payment getPaymentByBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
        return paymentRepository.findByBooking(booking)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for booking id: " + bookingId));
    }

    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    public List<Payment> getPaymentsByMethod(String method) {
        return paymentRepository.findByPaymentMethod(method);
    }

    public Payment processPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        
        if ("COMPLETED".equals(payment.getPaymentStatus())) {
            throw new PaymentAlreadyProcessedException("Payment has already been processed");
        }
        
        if ("FAILED".equals(payment.getPaymentStatus())) {
            throw new PaymentProcessingException("Cannot process a failed payment. Create a new payment.");
        }
        
        // Simulate payment processing
        try {
            // In real application, integrate with payment gateway here
            payment.setPaymentStatus("COMPLETED");
            payment.setPaymentDate(LocalDateTime.now());
            
            // Confirm the booking after successful payment
            bookingService.confirmBooking(payment.getBooking().getId());
            
            return paymentRepository.save(payment);
        } catch (Exception e) {
            payment.setPaymentStatus("FAILED");
            paymentRepository.save(payment);
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage());
        }
    }

    public Payment refundPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        
        if (!"COMPLETED".equals(payment.getPaymentStatus())) {
            throw new RefundNotAllowedException("Only completed payments can be refunded");
        }
        
        if ("REFUNDED".equals(payment.getPaymentStatus())) {
            throw new PaymentAlreadyRefundedException("Payment has already been refunded");
        }
        
        // Process refund
        payment.setPaymentStatus("REFUNDED");
        
        // Cancel the associated booking
        bookingService.cancelBooking(payment.getBooking().getId());
        
        return paymentRepository.save(payment);
    }

    public Payment markPaymentAsFailed(Long paymentId, String reason) {
        Payment payment = getPaymentById(paymentId);
        
        if ("COMPLETED".equals(payment.getPaymentStatus())) {
            throw new PaymentUpdateNotAllowedException("Cannot mark a completed payment as failed");
        }
        
        payment.setPaymentStatus("FAILED");
        return paymentRepository.save(payment);
    }

    public List<Payment> getPendingPayments() {
        return paymentRepository.findByPaymentStatus("PENDING");
    }

    public List<Payment> getCompletedPayments() {
        return paymentRepository.findByPaymentStatus("COMPLETED");
    }

    public Double calculateTotalRevenue() {
        return getCompletedPayments().stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public Double calculatePendingAmount() {
        return getPendingPayments().stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    // Validation

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new InvalidInputException("Payment cannot be null");
        }
        if (payment.getBooking() == null || payment.getBooking().getId() == null) {
            throw new InvalidInputException("Booking is required for payment");
        }
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new InvalidInputException("Payment amount must be positive");
        }
        if (payment.getPaymentMethod() == null || payment.getPaymentMethod().trim().isEmpty()) {
            throw new InvalidInputException("Payment method is required");
        }
    }

    // Business Exceptions

    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(String message) {
            super(message);
        }
    }

    public static class BookingNotFoundException extends RuntimeException {
        public BookingNotFoundException(String message) {
            super(message);
        }
    }

    public static class PaymentNotAllowedException extends RuntimeException {
        public PaymentNotAllowedException(String message) {
            super(message);
        }
    }

    public static class PaymentAlreadyExistsException extends RuntimeException {
        public PaymentAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class PaymentUpdateNotAllowedException extends RuntimeException {
        public PaymentUpdateNotAllowedException(String message) {
            super(message);
        }
    }

    public static class PaymentDeletionNotAllowedException extends RuntimeException {
        public PaymentDeletionNotAllowedException(String message) {
            super(message);
        }
    }

    public static class PaymentAlreadyProcessedException extends RuntimeException {
        public PaymentAlreadyProcessedException(String message) {
            super(message);
        }
    }

    public static class PaymentProcessingException extends RuntimeException {
        public PaymentProcessingException(String message) {
            super(message);
        }
    }

    public static class RefundNotAllowedException extends RuntimeException {
        public RefundNotAllowedException(String message) {
            super(message);
        }
    }

    public static class PaymentAlreadyRefundedException extends RuntimeException {
        public PaymentAlreadyRefundedException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
