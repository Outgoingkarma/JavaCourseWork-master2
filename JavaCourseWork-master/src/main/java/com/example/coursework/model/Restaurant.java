package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Restaurant extends User {
    private String openTime;
    private String closeTime;
    private int estimatedDeliveryTime;
    private boolean isActive = true;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    private int totalReviews = 0;
    private double averageRating = 0.0;
    private double deliveryFee;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Dishes> dishesMenu = new ArrayList<>();
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FoodOrder> orders = new ArrayList<>();
    private String address;
    @Transient
    private String imageURL;
    private String restaurantName;


    public Restaurant(String login, String password, String name, String surname, String restaurantName, String phone_number, String email, String address, String openTime, String closeTime) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.restaurantName = restaurantName;
        this.phone_number = phone_number;
        this.email = email;
        this.address = address;
        this.dateCreated = LocalDateTime.now();
        this.dateModified = LocalDateTime.now();
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isAdmin = false;
    }


    @Override
    public String toString() {
        String displayName = getRestaurantName();
        return displayName;
    }


}
