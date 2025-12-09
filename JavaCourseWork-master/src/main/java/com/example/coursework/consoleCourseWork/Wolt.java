package com.example.coursework.consoleCourseWork;

import com.example.coursework.model.FoodOrder;
import com.example.coursework.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter


public class Wolt {
    private List<User> allSystemusers;
    private List<FoodOrder> foodOrders;

    public Wolt() {
        this.allSystemusers = new ArrayList<>();
        this.foodOrders = new ArrayList<>();
    }
}
