package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Driver extends User {
    private String licensePlate;
    @Enumerated(EnumType.STRING)
    private DriverVehicleType driverVehicleType;
    private boolean isAvailable;
    private int totalDeliveries;
    @OneToOne
    private FoodOrder currentOrder;
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FoodOrder> myOrders;
    private Double rating;
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Chat> chats;


    public Driver(String login, String password, String name, String surname, String phone_number, String email, String licensePlate, DriverVehicleType driverVehicleType) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone_number = phone_number;
        this.email = email;
        this.dateCreated = LocalDateTime.now();
        this.dateModified = LocalDateTime.now();
        this.licensePlate = licensePlate;
        this.driverVehicleType = driverVehicleType;
        this.isAvailable = true;
        this.myOrders = new ArrayList<>();
        this.totalDeliveries = 0;
        this.rating = 0.0;
        this.isAdmin = false;
    }


    private void toggleAvailability() {
        this.isAvailable = !this.isAvailable;
    }

    public void assignOrder(FoodOrder order) {
        this.currentOrder = order;
        toggleAvailability();
    }

    public void completeDelivery() {
        if (currentOrder != null) {
            this.totalDeliveries++;
            this.myOrders.add(currentOrder);
            this.currentOrder = null;
            toggleAvailability();
        }
    }

    public void updateRating(double newRating) {
        double currentRating = this.rating == null ? 0.0 : this.rating;
        if (totalDeliveries == 0) {
            this.rating = newRating;
        } else {
            this.rating = (currentRating * totalDeliveries + newRating) / (totalDeliveries + 1);
        }
    }

}














