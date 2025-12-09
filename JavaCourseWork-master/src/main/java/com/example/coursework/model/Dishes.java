package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Dishes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;

    @ElementCollection(targetClass = DishesIngredients.class)
    @CollectionTable(name = "dish_ingredient", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "ingredient")
    @Enumerated(EnumType.STRING)
    private List<DishesIngredients> dishesIngredients;
    @Enumerated(EnumType.STRING)
    private DishesPortionSize dishesPortionSize;
    @Enumerated(EnumType.STRING)
    private DishType dishType;
    private double price;
    private boolean isAvailable;
    @Transient
    private String imageURL;
    private int preparationTime; // in minutes
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Restaurant restaurant;
    @ManyToMany(mappedBy = "dishes")
    private List<FoodOrder> orders = new ArrayList<>();


    public Dishes(String name, String description, DishesPortionSize portionSize, DishType dishType, double price, Restaurant restaurant) {
        this.name = name;
        this.description = description;
        this.dishesPortionSize = portionSize;
        this.dishType = dishType;
        this.price = price;
        this.dishesIngredients = new ArrayList<>();
        this.isAvailable = true;
        this.restaurant = restaurant;
    }


    public void addIngredient(DishesIngredients ingredient) {
        if (!this.dishesIngredients.contains(ingredient)) {
            this.dishesIngredients.add(ingredient);
        }
    }

    public void removeIngredient(DishesIngredients ingredient) {
        this.dishesIngredients.remove(ingredient);
    }


    public void toggleAvailability() {
        this.isAvailable = !this.isAvailable;
    }

    @Override
    public String toString() {
        return name + " " + dishesPortionSize + " " + price;
    }
}
