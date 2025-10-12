package com.zubair.travel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;
    
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;
    
    @Column(name = "total_passengers", nullable = false)
    private Integer totalPassengers;
    
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "status", nullable = false)
    private String status;
}
