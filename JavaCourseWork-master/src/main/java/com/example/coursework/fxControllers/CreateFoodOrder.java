package com.example.coursework.fxControllers;

import com.example.coursework.hibernateControl.CustomHibernate;
import com.example.coursework.hibernateControl.GenericHibernate;
import com.example.coursework.model.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateFoodOrder implements Initializable {
    @FXML
    public Button foodOrderSaveButton;
    @FXML
    public ComboBox<Restaurant> foodOrderRestaurant;
    @FXML
    public ComboBox<BasicUser> foodOrderClient;
    @FXML
    public ComboBox<FoodOrderStatus> foodOrderStatus;
    @FXML
    public ComboBox<Driver> foodOrderDriver;
    @FXML
    public ListView<Dishes> foodOrderList;
    @FXML
    public Button foodOrderDishAdd;
    @FXML
    public ListView<Dishes> dishList;
    @FXML
    public Button foodOrderDishDelete;
    @FXML
    public TextField foodOrderAddress;
    @FXML
    public Label foodOrderTotalPrice;
    private EntityManagerFactory entityManagerFactory;
    private GenericHibernate genericHibernate;
    private CustomHibernate customHibernate;
    private final ObservableList<Dishes> availableDishes = FXCollections.observableArrayList();
    private final ObservableList<Dishes> selectedDishes = FXCollections.observableArrayList();

    public static final DecimalFormat PRICE_FORMAT = new DecimalFormat("0.00");

    @FXML
    public void saveFoodOrder() {
        List<String> errors = new ArrayList<>();
        Restaurant restaurant = foodOrderRestaurant.getValue();
        BasicUser customer = foodOrderClient.getValue();
        Driver driver = foodOrderDriver.getValue();
        FoodOrderStatus status = foodOrderStatus.getValue();
        String address = foodOrderAddress.getText() == null ? "" : foodOrderAddress.getText().trim();
        if (restaurant == null) errors.add("Restaurant is not selected");
        if (customer == null) errors.add("Customer is not selected");
        if (status == null) errors.add("Status is not selected");
        if (address.isEmpty()) errors.add("Address is required");
        if (selectedDishes.isEmpty()) errors.add("No dishes selected");
        if (!errors.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, String.join("\n", errors)).showAndWait();
            return;
        }
        double totalPrice = calculateCurrentOrderPrice(restaurant);
        List<Dishes> dishesForOrder = new ArrayList<>(selectedDishes);
        try {
            FoodOrder foodOrder = new FoodOrder(foodOrderRestaurant.getValue(),
                    foodOrderClient.getValue(),
                    foodOrderDriver.getValue(),
                    foodOrderAddress.getText(),
                    foodOrderStatus.getValue(),
                    totalPrice,
                    dishesForOrder

            );
            foodOrder.ensureChat();
            genericHibernate.create(foodOrder);

            new Alert(Alert.AlertType.INFORMATION, "Order saved successfully with " + selectedDishes.size() + " dishes").showAndWait();

            selectedDishes.clear();
            foodOrderStatus.getSelectionModel().clearSelection();
            foodOrderDriver.getSelectionModel().clearSelection();
            foodOrderAddress.clear();
            updateTotalPriceLabel();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save order: " + e.getMessage()).showAndWait();
        }
        foodOrderSaveButton.getScene().getWindow().hide();
    }

    public void setData(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.genericHibernate = new GenericHibernate(entityManagerFactory);
        this.customHibernate = new CustomHibernate(entityManagerFactory);
        fillData();
    }

    private void fillData() {
        foodOrderRestaurant.getItems().addAll(genericHibernate.getAllRecords(Restaurant.class));
        foodOrderClient.getItems().addAll(genericHibernate.getAllRecords(BasicUser.class));
        foodOrderDriver.getItems().addAll(genericHibernate.getAllRecords(Driver.class));
    }

    @FXML
    public void addDishToFoodOrder() {
        List<Dishes> picked = new ArrayList<>(dishList.getSelectionModel().getSelectedItems());
        if (picked.isEmpty()) return;
        for (Dishes dish : picked) {
            selectedDishes.add(dish);
        }
        dishList.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        foodOrderStatus.getItems().addAll(FoodOrderStatus.values());

        dishList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        foodOrderList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        dishList.setItems(availableDishes);
        foodOrderList.setItems(selectedDishes);
        selectedDishes.addListener((ListChangeListener<Dishes>) change -> updateTotalPriceLabel());

        dishList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Dishes item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " " + item.getDishesPortionSize() + " " + PRICE_FORMAT.format(item.getPrice()));
            }
        });
        foodOrderList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Dishes item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " " + item.getDishesPortionSize() + " " + PRICE_FORMAT.format(item.getPrice()));
            }
        });
        foodOrderRestaurant.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Restaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getRestaurantName());
            }
        });
        foodOrderRestaurant.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Restaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getRestaurantName());
            }
        });
        foodOrderClient.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BasicUser item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " " + item.getSurname());
            }
        });
        foodOrderClient.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(BasicUser item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " " + item.getSurname());
            }
        });
        foodOrderDriver.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Driver item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " " + item.getSurname());
            }
        });
        foodOrderDriver.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Driver item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " " + item.getSurname());
            }
        });


        foodOrderRestaurant.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedDishes.clear();
            loadDishesForRestaurant(newValue);
            updateTotalPriceLabel();
        });
        updateTotalPriceLabel();

    }

    private void loadDishesForRestaurant(Restaurant restaurant) {
        availableDishes.clear();
        if (restaurant == null || customHibernate == null) return;
        List<Dishes> dishes = customHibernate.getDishesByRestaurant(restaurant.getId());
        availableDishes.addAll(dishes);
    }

    @FXML
    public void deleteDishesFromFoodOrder() {
        List<Integer> pickedIndiciess = new ArrayList<>(foodOrderList.getSelectionModel().getSelectedIndices());
        if (pickedIndiciess.isEmpty()) return;
        pickedIndiciess.sort((a, b) -> Integer.compare(b, a));
        for (Integer index : pickedIndiciess) {
            if (index >= 0 && index < selectedDishes.size()) {
                selectedDishes.remove(index.intValue());
            }
        }
        foodOrderList.getSelectionModel().clearSelection();
    }

    private double calculateCurrentOrderPrice(Restaurant restaurant) {
        double dishesTotal = selectedDishes.stream().mapToDouble(Dishes::getPrice).sum();
        double deliveryFee = restaurant != null ? restaurant.getDeliveryFee() : 0;
        return dishesTotal + deliveryFee;

    }

    private void updateTotalPriceLabel() {
        if (foodOrderTotalPrice == null) return;
        double price = calculateCurrentOrderPrice(foodOrderRestaurant.getValue());
        foodOrderTotalPrice.setText(PRICE_FORMAT.format(price));
    }
}
