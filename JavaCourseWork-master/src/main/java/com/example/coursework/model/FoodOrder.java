package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "food_order_dishes",
            joinColumns = @JoinColumn(name = "food_order_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id"))
    private List<Dishes> dishes = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id")
    private Chat chat;
    private boolean isDelivered;
    private double orderPrice;
    @ManyToOne
    private BasicUser customer;
    @ManyToOne
    private Restaurant restaurant;
    @ManyToOne
    private Driver driver;
    private LocalDateTime orderCreationDate;
    private LocalDateTime orderDeliveryDate;
    private String deliveryAddress;
    @Enumerated(EnumType.STRING)
    private FoodOrderStatus orderStatus;


    public FoodOrder() {
        this.dishes = new ArrayList<>();
        this.chat = new Chat();
        this.chat.setOrder(this);
        this.isDelivered = false;
        this.orderCreationDate = LocalDateTime.now();
    }

    public FoodOrder(Restaurant restaurant, BasicUser customer, Driver driver, String deliveryAddress, FoodOrderStatus orderStatus, Double orderPrice, List<Dishes> selectedDishes) {

        this();
        this.customer = customer;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.orderStatus = orderStatus;
        this.driver = driver;
        this.orderPrice = orderPrice == null ? 0.0 : orderPrice;

        if (selectedDishes != null) {
            for (Dishes dish : selectedDishes) {
                this.dishes.add(dish);
                if (dish.getOrders() == null) {
                    dish.setOrders(new ArrayList<>());
                }
                if (!dish.getOrders().contains(this)) {
                    dish.getOrders().add(this);
                }
            }
        }
        ensureChat();


    }

    public Chat ensureChat() {
        if (this.chat == null) {
            Chat newChat = new Chat();
            newChat.setOrder(this);
            newChat.setCustomer(customer);
            newChat.setDriver(driver);
            newChat.setRestaurant(restaurant);
            this.chat = newChat;
        } else {
            chat.setCustomer(customer);
            chat.setDriver(driver);
            chat.setRestaurant(restaurant);
            chat.setOrder(this);
        }
        return this.chat;
    }


}
