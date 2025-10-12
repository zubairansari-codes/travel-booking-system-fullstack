package com.zubair.travel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lodges")
public class Lodge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private Double pricePerNight;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Constructors
    public Lodge() {
    }
    
    public Lodge(String name, String address, Double pricePerNight, String description) {
        this.name = name;
        this.address = address;
        this.pricePerNight = pricePerNight;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Double getPricePerNight() {
        return pricePerNight;
    }
    
    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
