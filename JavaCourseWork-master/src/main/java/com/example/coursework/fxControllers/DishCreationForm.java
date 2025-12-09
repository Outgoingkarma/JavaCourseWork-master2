package com.example.coursework.fxControllers;

import com.example.coursework.hibernateControl.GenericHibernate;
import com.example.coursework.model.DishType;
import com.example.coursework.model.Dishes;
import com.example.coursework.model.DishesPortionSize;
import com.example.coursework.model.Restaurant;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.ToString;

import java.net.URL;
import java.util.ResourceBundle;

public class DishCreationForm implements Initializable {
    public TextField dishName;
    public ComboBox<DishType> dishType;
    public ComboBox<DishesPortionSize> dishSize;
    public TextField dishPrice;
    public Button saveDishButton;
    public TextArea dishDescription;
    public ComboBox<Restaurant> restaurantDishBelongTo;
    private EntityManagerFactory entityManagerFactory;
    private GenericHibernate genericHibernate;

    public void saveDish() {
        Dishes dish = new Dishes(dishName.getText().trim(),
                dishDescription.getText().trim(),
                dishSize.getValue(),
                dishType.getValue(),
                Double.parseDouble(dishPrice.getText().trim()),
                restaurantDishBelongTo.getValue());
        genericHibernate.create(dish);
        saveDishButton.getScene().getWindow().hide();
        saveDishButton.getScene().getWindow().hide();
    }

    public void setData(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.genericHibernate = new GenericHibernate(entityManagerFactory);

        loadRestaurants();
    }

    private void loadRestaurants() {
        restaurantDishBelongTo.getItems().addAll(genericHibernate.getAllRecords(Restaurant.class));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dishType.getItems().addAll(DishType.values());
        dishSize.getItems().addAll(DishesPortionSize.values());
    }


}
