package com.example.coursework.fxTablesParameters;

import com.example.coursework.model.BasicUser;
import com.example.coursework.model.Driver;
import com.example.coursework.model.FoodOrderStatus;
import com.example.coursework.model.Restaurant;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.SimpleDateFormat;

public class OrderTableParameters {
    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleObjectProperty<BasicUser> customer = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Restaurant> restaurant = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Driver> driver = new SimpleObjectProperty<>();
    private SimpleObjectProperty<FoodOrderStatus> orderStatus = new SimpleObjectProperty<>();
    private SimpleDateFormat orderDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public BasicUser getCustomer() {
        return customer.get();
    }

    public SimpleObjectProperty<BasicUser> customerProperty() {
        return customer;
    }

    public void setCustomer(BasicUser customer) {
        this.customer.set(customer);
    }

    public Restaurant getRestaurant() {
        return restaurant.get();
    }

    public SimpleObjectProperty<Restaurant> restaurantProperty() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant.set(restaurant);
    }

    public Driver getDriver() {
        return driver.get();
    }

    public SimpleObjectProperty<Driver> driverProperty() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver.set(driver);
    }

    public FoodOrderStatus getOrderStatus() {
        return orderStatus.get();
    }

    public SimpleObjectProperty<FoodOrderStatus> orderStatusProperty() {
        return orderStatus;
    }

    public void setOrderStatus(FoodOrderStatus orderStatus) {
        this.orderStatus.set(orderStatus);
    }

    public SimpleDateFormat getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(SimpleDateFormat orderDate) {
        this.orderDate = orderDate;
    }
}
