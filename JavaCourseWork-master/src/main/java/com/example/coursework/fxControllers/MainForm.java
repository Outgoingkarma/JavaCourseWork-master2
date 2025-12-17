package com.example.coursework.fxControllers;

import com.example.coursework.HelloApplication;
import com.example.coursework.fxTablesParameters.UserTableParameters;
import com.example.coursework.hibernateControl.CustomHibernate;
import com.example.coursework.model.*;
import com.example.coursework.utils.FxUtils;
import com.example.coursework.utils.Validation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.coursework.fxControllers.CreateFoodOrder.PRICE_FORMAT;

public class MainForm implements Initializable {
    @FXML
    public TableColumn<UserTableParameters, Integer> userIdColumn;
    @FXML
    public TableColumn<UserTableParameters, String> userLoginColumn;
    @FXML
    public TableColumn<UserTableParameters, String> userPasswordColumn;
    @FXML
    public TableColumn<UserTableParameters, String> userNameColumn;
    @FXML
    public TableColumn<UserTableParameters, String> userSurnameColumn;
    @FXML
    public TableColumn<UserTableParameters, String> userEmailColumn;
    @FXML
    public TableColumn<UserTableParameters, String> userPhoneColumn;
    @FXML
    public TableColumn<UserTableParameters, String> userAddressColumn;
    @FXML
    public TableColumn<UserTableParameters, String> userTypeColumn;
    @FXML
    public TableColumn<UserTableParameters, String> userOrderHistoryColumn;
    @FXML
    public Button createNewUserButton;
    @FXML
    public Button editUserButton;
    @FXML
    public Button deleteUserButton;
    @FXML
    public TableColumn<FoodOrder, Integer> orderIDColumn;
    @FXML
    public TableColumn<FoodOrder, String> orderCustomerColumn;
    @FXML
    public TableColumn<FoodOrder, String> orderRestaurantColumn;
    @FXML
    public TableColumn<FoodOrder, String> orderDriverColumn;
    @FXML
    public TableColumn<FoodOrder, FoodOrderStatus> orderStatusColumn;
    @FXML
    public TableColumn<FoodOrder, LocalDateTime> orderDateColumn;
    @FXML
    public Button orderaddOrderButton;
    @FXML
    public Button orderUpdateStatusButton;
    @FXML
    public Tab userTab;
    @FXML
    public Tab orderTab;
    @FXML
    public TableView<UserTableParameters> userTable;
    @FXML
    public Button filterButton;
    @FXML
    public TextField filterName;
    @FXML
    public TextField filterSurname;
    @FXML
    public Button deleteOrderButton;
    @FXML
    public Button openChatButton;
    @FXML
    public ListView<Restaurant> restaurantsList;

    @FXML
    public Button addDishButton;
    @FXML
    public Button removeDishButton;
    @FXML
    public TableView<Dishes> restaurantMenuTable;
    @FXML
    public Tab restaurantMenuTab;
    @FXML
    public TextArea dishDescription;
    @FXML
    public ListView<DishesIngredients> allDishIngredientsList;
    @FXML
    public ListView<DishesIngredients> selectedDishIngredientsList;
    @FXML
    public Button removeIngredientsButton;
    @FXML
    public Button addIngredientsButton;

    private final ObservableList<UserTableParameters> data = FXCollections.observableArrayList();
    private final ObservableList<FoodOrder> orderData = FXCollections.observableArrayList();
    @FXML
    public TableView<FoodOrder> orderTable;
    @FXML
    public TableColumn<Dishes, Integer> dishIDColumn;
    @FXML
    public TableColumn<Dishes, String> dishNameColumn;
    @FXML
    public TableColumn<Dishes, DishType> dishTypeColumn;
    @FXML
    public TableColumn<Dishes, DishesPortionSize> dishPortionSizeColumn;
    @FXML
    public TableColumn<Dishes, Double> dishPriceColumn;
    @FXML
    public TableColumn<FoodOrder, Double> foodOrderPrice;

    private final ObservableList<Dishes> dishes = FXCollections.observableArrayList();
    private final ObservableList<DishesIngredients> dishesIngredients = FXCollections.observableArrayList();
    private final ObservableList<DishesIngredients> selectedDishIngredients = FXCollections.observableArrayList();
    private final ObservableList<Restaurant> restaurants = FXCollections.observableArrayList();
    @FXML
    public Button editDishButton;
    @FXML
    public ListView<Dishes> orderedFoodList;
    private final ObservableList<Dishes> orderedFoodItems = FXCollections.observableArrayList();

    private EntityManagerFactory entityManagerFactory;
    private CustomHibernate customHibernate;
    private User currentUser;

    private void configureUserTabVisibility() {
        boolean allowUserTab = currentUser != null && currentUser.isAdmin();


//        userTab.setVisible(allowUserTab);
//        userTab.setManaged(allowUserTab);
        userTab.setDisable(!allowUserTab);

        if (!allowUserTab && userTab.getTabPane() != null && userTab.isSelected()) {
            userTab.getTabPane().getSelectionModel().select(orderTab);
        }
    }

//    private final ObservableList<Restaurant> restaurants = FXCollections.observableArrayList();
//    private final ObservableList<Driver> drivers = FXCollections.observableArrayList();
//    private final ObservableList<BasicUser> customers = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userTable.setEditable(true);
        //userTable.setItems(data);
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userLoginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        userAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        userTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
        //userOrderHistoryColumn.setCellValueFactory(new PropertyValueFactory<>("orderHistory"));


        userLoginColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userLoginColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setLogin(event.getNewValue());
            User user = customHibernate.getRecordById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setPassword(event.getNewValue());
            customHibernate.update(user);
        });

        userPasswordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userPasswordColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setPassword(event.getNewValue());
            User user = customHibernate.getRecordById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setPassword(event.getNewValue());
            customHibernate.update(user);
        });

        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
            User user = customHibernate.getRecordById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setName(event.getNewValue());
            customHibernate.update(user);
        });

        userSurnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userSurnameColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setSurname(event.getNewValue());
            User user = customHibernate.getRecordById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setName(event.getNewValue());
            customHibernate.update(user);
        });

        userEmailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userEmailColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setEmail(event.getNewValue());
            User user = customHibernate.getRecordById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setName(event.getNewValue());
            customHibernate.update(user);
        });

        userPhoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userPhoneColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setPhoneNumber(event.getNewValue());
            User user = customHibernate.getRecordById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setName(event.getNewValue());
            customHibernate.update(user);
        });

        userAddressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userAddressColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setAddress(event.getNewValue());
            User user = customHibernate.getRecordById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            user.setName(event.getNewValue());
            customHibernate.update(user);
        });

//        userTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
//        userTypeColumn.setOnEditCommit(event -> {
//            event.getTableView().getItems().get(event.getTablePosition().getRow()).setUserType(event.getNewValue());
//            User user = customHibernate.getRecordById(User.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
//            user.setName(event.getNewValue());
//            customHibernate.update(user);
//        });
        // Kol kas manau kad nenoriu kad butu galima keisti userio tipa

        //userTable.getItems().addAll(data);


        orderIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderCustomerColumn.setCellValueFactory(param -> new SimpleStringProperty(formatUser(param.getValue().getCustomer())));
        orderRestaurantColumn.setCellValueFactory(param -> new SimpleStringProperty(formatRestaurant(param.getValue().getRestaurant())));
        orderDriverColumn.setCellValueFactory(param -> new SimpleStringProperty(formatUser(param.getValue().getDriver())));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderCreationDate"));
        foodOrderPrice.setCellValueFactory(new PropertyValueFactory<>("orderPrice"));
        orderTable.setItems(orderData);
        orderTable.setEditable(true);

        orderStatusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FoodOrderStatus.values()));
        orderStatusColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setOrderStatus(event.getNewValue());
            FoodOrder foodOrder = customHibernate.getFoodOrderWithDishes(event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            foodOrder.setOrderStatus(event.getNewValue());
            customHibernate.update(foodOrder);
        });


        restaurantsList.setCellFactory(lv -> new ListCell<Restaurant>() {
            @Override
            protected void updateItem(Restaurant restaurant, boolean empty) {
                super.updateItem(restaurant, empty);
                setText(empty || restaurant == null ? null : restaurant.getRestaurantName());
            }
        });
        restaurantsList.setItems(restaurants);

        dishIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dishNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dishTypeColumn.setCellValueFactory(new PropertyValueFactory<>("dishType"));
        dishPortionSizeColumn.setCellValueFactory(new PropertyValueFactory<>("dishesPortionSize"));
        dishPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        restaurantMenuTable.setItems(dishes);
        restaurantMenuTable.setEditable(true);

        dishNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dishNameColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
            Dishes dish = customHibernate.getRecordById(Dishes.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            dish.setName(event.getNewValue());
            customHibernate.update(dish);
        });

        dishTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(DishType.values()));
        dishTypeColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setDishType(event.getNewValue());
            Dishes dish = customHibernate.getRecordById(Dishes.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            dish.setDishType(event.getNewValue());
            customHibernate.update(dish);
        });

        dishPortionSizeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(DishesPortionSize.values()));
        dishPortionSizeColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setDishesPortionSize(event.getNewValue());
            Dishes dish = customHibernate.getRecordById(Dishes.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            dish.setDishesPortionSize(event.getNewValue());
            customHibernate.update(dish);
        });

        dishPriceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        dishPriceColumn.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setPrice(event.getNewValue());
            Dishes dish = customHibernate.getRecordById(Dishes.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            dish.setPrice(event.getNewValue());
            customHibernate.update(dish);
        });


        restaurantsList.getSelectionModel().selectedItemProperty().addListener((obs, previousRestaurant, selectedRestaurant) -> {
            loadDishesForRestaurant(selectedRestaurant);
        });

        restaurantMenuTable.getSelectionModel().selectedItemProperty().addListener((obs, o, d) -> {
            showDishDetails(d);
        });

        allDishIngredientsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedDishIngredientsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedDishIngredientsList.setItems(selectedDishIngredients);
        allDishIngredientsList.setItems(dishesIngredients);

        orderedFoodList.setItems(orderedFoodItems);
        orderedFoodList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Dishes dish, boolean empty) {
                super.updateItem(dish, empty);
                setText(empty || dish == null ? null : dish.getName() + " " + dish.getDishesPortionSize() + " " + PRICE_FORMAT.format(dish.getPrice()));
            }
        });
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, previousValue, selected) -> showOrderDetails(selected));
    }


    public void reloadOrderTab() {
        if (!orderTab.isSelected() || customHibernate == null) return;


        FoodOrder previouslySelected = orderTable.getSelectionModel().getSelectedItem();
        Integer previousId = (previouslySelected != null) ? previouslySelected.getId() : null;
        List<FoodOrder> orders = customHibernate.getAllRecords(FoodOrder.class);
        orderData.setAll(orders);

        if (orders.isEmpty()) {
            orderTable.getSelectionModel().clearSelection();
            orderedFoodItems.clear();
            showOrderDetails(null);
            return;
        }
        if (previousId != null) {
            for (FoodOrder o : orders) {
                if (o.getId() == previousId) {
                    orderTable.getSelectionModel().select(o);
                    break;
                }
            }
        }

        if (orderTable.getSelectionModel().getSelectedItem() == null) {
            orderTable.getSelectionModel().selectFirst();
        }
        FoodOrder current = orderTable.getSelectionModel().getSelectedItem();
        showOrderDetails(current);
    }

    private void showOrderDetails(FoodOrder selectedOrder) {
        orderedFoodItems.clear();
        if (selectedOrder == null || customHibernate == null) return;

        FoodOrder foodOrder = customHibernate.getFoodOrderWithDishes(selectedOrder.getId());
        if (foodOrder != null && foodOrder.getDishes() != null)
            orderedFoodItems.addAll(foodOrder.getDishes());
    }

    private String formatUser(User user) {
        if (user == null) return "-";
        StringBuilder builder = new StringBuilder();
        if (user.getName() != null && !user.getName().isBlank()) builder.append(user.getName());
        if (user.getSurname() != null && !user.getSurname().isBlank()) builder.append(" ").append(user.getSurname());
        if (builder.length() == 0) {
            builder.append(user.getLogin() == null ? "-" : user.getLogin());
        }
        return builder.toString();
    }

    private String formatRestaurant(Restaurant restaurant) {
        if (restaurant == null) return "-";

        return restaurant.getRestaurantName() == null ? "-" : restaurant.getRestaurantName();

    }


    public void setData(EntityManagerFactory emf, User user) {
        this.entityManagerFactory = emf;
        this.customHibernate = new CustomHibernate(emf);
        this.currentUser = user;


        if (userTab.isSelected()) {
            Platform.runLater(this::reloadUserTab);
//            reloadRestaurantTab();
        }
    }

    public void reloadUserTab() {
        data.clear();
        if (userTab.isSelected()) {
            List<User> users = customHibernate.getAllRecords(User.class);
            for (User user : users) {
                UserTableParameters userTableParameters = new UserTableParameters();
                userTableParameters.setId(user.getId());
                userTableParameters.setLogin(user.getLogin());
                userTableParameters.setPassword(user.getPassword());
                userTableParameters.setName(user.getName());
                userTableParameters.setSurname(user.getSurname());
                userTableParameters.setEmail(user.getEmail());
                userTableParameters.setPhoneNumber(user.getPhone_number());
                if (user instanceof BasicUser) {
                    userTableParameters.setAddress(((BasicUser) user).getAddress());
                }
                if (user instanceof Driver) {


                }
                if (user instanceof Restaurant) {
                    userTableParameters.setAddress(((Restaurant) user).getAddress());
                }


                userTableParameters.setUserType(user.getClass().getSimpleName());
                data.add(userTableParameters);
            }
            //userTable.setItems(data);

            userTable.getItems().clear();
            userTable.getItems().addAll(data);


        }
    }


    public void deleteUser() {

        {
            if (customHibernate == null) {
                new Alert(Alert.AlertType.ERROR, "Data layer not initialized.").showAndWait();
                return;
            }

            UserTableParameters selected = userTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.INFORMATION, "Select a user to delete.").showAndWait();
                return;
            }

            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Delete user \"" + selected.getLogin() + "\" (id " + selected.getId() + ")?",
                    ButtonType.OK, ButtonType.CANCEL
            );
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) return;

            try {
                customHibernate.delete(User.class, selected.getId());
                reloadUserTab();


            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Could not delete user. " +
                                "There may be related records (orders, etc.).\n\nDetails: " + ex.getMessage()
                ).showAndWait();
            }
            reloadUserTab();

        }
    }

    public void createNewUser() throws IOException {
        LoginForm loginForm = new LoginForm();
        loginForm.registerNewUser();
        reloadUserTab();
    }

    public void editUser() {
        var selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select a user to edit.").showAndWait();
            return;
        }

        User entity = customHibernate.getRecordById(User.class, selected.getId());
        if (entity == null) {
            new Alert(Alert.AlertType.ERROR, "User not found (id " + selected.getId() + ").").showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit user — " + selected.getLogin());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfLogin = new TextField(entity.getLogin());
        PasswordField pfPass = new PasswordField();
        pfPass.setText(entity.getPassword());
        TextField tfName = new TextField(entity.getName());
        TextField tfSurname = new TextField(entity.getSurname());
        TextField tfEmail = new TextField(entity.getEmail());
        TextField tfPhone = new TextField(entity.getPhone_number());
        TextField tfAddress = new TextField();
        boolean isClient = (entity instanceof BasicUser);
        if (isClient) tfAddress.setText(((BasicUser) entity).getAddress());

        tfPhone.setTextFormatter(new TextFormatter<>(change -> {
            String n = change.getText();
            if (n != null && !n.matches("[0-9+()\\- ]*")) return null;
            return change;
        }));

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));
        int r = 0;
        grid.addRow(r++, new Label("Login"), tfLogin);
        grid.addRow(r++, new Label("Password"), pfPass);
        grid.addRow(r++, new Label("Name"), tfName);
        grid.addRow(r++, new Label("Surname"), tfSurname);
        grid.addRow(r++, new Label("Email"), tfEmail);
        grid.addRow(r++, new Label("Phone"), tfPhone);
        if (isClient) grid.addRow(r++, new Label("Address"), tfAddress);


        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("text-red-600");
        grid.add(errorLabel, 0, r, 2, 1);

        dialog.getDialogPane().setContent(grid);


        Button ok = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        Runnable validate = () -> {
            String addr = isClient ? tfAddress.getText() : null;
            var errors = Validation.validateUserFields(
                    tfLogin.getText(), pfPass.getText(),
                    tfName.getText(), tfSurname.getText(),
                    tfEmail.getText(), tfPhone.getText(),
                    addr
            );
            if (errors.isEmpty()) {
                errorLabel.setText("");
                ok.setDisable(false);
            } else {
                errorLabel.setText(String.join("\n", errors));
                ok.setDisable(true);
            }
        };
        tfLogin.textProperty().addListener((o, a, b) -> validate.run());
        pfPass.textProperty().addListener((o, a, b) -> validate.run());
        tfName.textProperty().addListener((o, a, b) -> validate.run());
        tfSurname.textProperty().addListener((o, a, b) -> validate.run());
        tfEmail.textProperty().addListener((o, a, b) -> validate.run());
        tfPhone.textProperty().addListener((o, a, b) -> validate.run());
        if (isClient) tfAddress.textProperty().addListener((o, a, b) -> validate.run());
        validate.run();

        var result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;


        String addr = isClient ? tfAddress.getText() : null;
        var errors = Validation.validateUserFields(
                tfLogin.getText(), pfPass.getText(),
                tfName.getText(), tfSurname.getText(),
                tfEmail.getText(), tfPhone.getText(),
                addr
        );
        if (!errors.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, String.join("\n", errors)).showAndWait();
            return;
        }
        try {
            customHibernate.updateById(User.class, selected.getId(), u -> {
                u.setLogin(tfLogin.getText());
                u.setPassword(pfPass.getText());
                u.setName(tfName.getText());
                u.setSurname(tfSurname.getText());
                u.setEmail(tfEmail.getText());
                u.setPhone_number(tfPhone.getText());
                if (u instanceof BasicUser bu) bu.setAddress(addr);
            });
            selected.setLogin(tfLogin.getText());
            selected.setPassword(pfPass.getText());
            selected.setName(tfName.getText());
            selected.setSurname(tfSurname.getText());
            selected.setEmail(tfEmail.getText());
            selected.setPhoneNumber(tfPhone.getText());
            if (isClient) selected.setAddress(addr);
            userTable.refresh();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to update user:\n" + ex.getMessage()).showAndWait();
        }
    }

    public void filterData() {
        data.clear();
        if (!filterName.getText().isEmpty() && filterSurname.getText().isEmpty()) {
            List<User> users = customHibernate.findByName(User.class, filterName.getText());
            for (User user : users) {
                UserTableParameters userTableParameters = new UserTableParameters();
                userTableParameters.setId(user.getId());
                userTableParameters.setLogin(user.getLogin());
                userTableParameters.setPassword(user.getPassword());
                userTableParameters.setName(user.getName());
                userTableParameters.setSurname(user.getSurname());
                userTableParameters.setEmail(user.getEmail());
                userTableParameters.setPhoneNumber(user.getPhone_number());
                if (user instanceof BasicUser) {
                    userTableParameters.setAddress(((BasicUser) user).getAddress());
                }

                userTableParameters.setUserType(user.getClass().getSimpleName());
                data.add(userTableParameters);
            }
            userTable.getItems().clear();
            userTable.getItems().addAll(data);
        } else if (filterName.getText().isEmpty() && !filterSurname.getText().isEmpty()) {
            List<User> users = customHibernate.findBySurname(User.class, filterSurname.getText());
            for (User user : users) {
                UserTableParameters userTableParameters = new UserTableParameters();
                userTableParameters.setId(user.getId());
                userTableParameters.setLogin(user.getLogin());
                userTableParameters.setPassword(user.getPassword());
                userTableParameters.setName(user.getName());
                userTableParameters.setSurname(user.getSurname());
                userTableParameters.setEmail(user.getEmail());
                userTableParameters.setPhoneNumber(user.getPhone_number());
                if (user instanceof BasicUser) {
                    userTableParameters.setAddress(((BasicUser) user).getAddress());
                }

                userTableParameters.setUserType(user.getClass().getSimpleName());
                data.add(userTableParameters);

            }
            userTable.getItems().clear();
            userTable.getItems().addAll(data);
        } else if (!filterName.getText().isEmpty() && !filterSurname.getText().isEmpty()) {
            List<User> users = customHibernate.findByNameAndSurname(User.class, filterName.getText(), filterSurname.getText());
            for (User user : users) {
                UserTableParameters userTableParameters = new UserTableParameters();
                userTableParameters.setId(user.getId());
                userTableParameters.setLogin(user.getLogin());
                userTableParameters.setPassword(user.getPassword());
                userTableParameters.setName(user.getName());
                userTableParameters.setSurname(user.getSurname());
                userTableParameters.setEmail(user.getEmail());
                userTableParameters.setPhoneNumber(user.getPhone_number());
                if (user instanceof BasicUser) {
                    userTableParameters.setAddress(((BasicUser) user).getAddress());
                }

                userTableParameters.setUserType(user.getClass().getSimpleName());
                data.add(userTableParameters);
            }
            userTable.getItems().clear();
            userTable.getItems().addAll(data);
        } else {
            reloadUserTab();
        }


    }

    public void createOrder() throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("food-order.fxml"));
        Parent parent = fxmlLoader.load();
        CreateFoodOrder createFoodOrder = fxmlLoader.getController();
        createFoodOrder.setData(entityManagerFactory);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle("Order creation!");
        stage.setScene(scene);
        stage.show();
    }

    public void updateOrder() {
    }

    public void deleteOrder() {
        if (customHibernate == null || entityManagerFactory == null) {
            new Alert(Alert.AlertType.ERROR, "Data layer not initialized.").showAndWait();
            return;
        }

        FoodOrder selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select an order to delete.").showAndWait();
            return;
        }

        if (!confirm("Delete order #" + selectedOrder.getId() + "?")) return;

        EntityManager em = null;
        try {
            em = entityManagerFactory.createEntityManager();
            em.getTransaction().begin();

            FoodOrder managedOrder = em.find(FoodOrder.class, selectedOrder.getId());
            if (managedOrder == null) {
                em.getTransaction().rollback();
                new Alert(Alert.AlertType.ERROR, "Selected order no longer exists.").showAndWait();
                reloadOrderTab();
                return;
            }

            if (managedOrder.getDishes() != null) {
                for (Dishes dish : new ArrayList<>(managedOrder.getDishes())) {
                    if (dish.getOrders() != null) {
                        dish.getOrders().remove(managedOrder);
                    }
                }
                managedOrder.getDishes().clear();
            }

            em.remove(managedOrder);
            em.getTransaction().commit();
            reloadOrderTab();
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            new Alert(Alert.AlertType.ERROR, "Failed to delete order:\n" + ex.getMessage()).showAndWait();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        reloadOrderTab();
    }

    public void openChat() {
        FoodOrder selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select an order to open a chat.").showAndWait();
            return;
        }
        if (entityManagerFactory == null) {
            new Alert(Alert.AlertType.ERROR, "Data layer not initialized").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("chat-view.fxml"));
            Parent parent = loader.load();
            ChatController chatController = loader.getController();
            chatController.setData(entityManagerFactory, selectedOrder, currentUser);
            Stage stage = new Stage();
            stage.setTitle("Chat for order #" + selectedOrder.getId());
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            FxUtils.generateExceptionAlert(e);
        }
    }

    public void reloadRestaurantTab() {
        int selectedIndex = restaurantsList.getSelectionModel().getSelectedIndex();
        restaurants.setAll(customHibernate.getAllRecords(Restaurant.class));
        if (!restaurants.isEmpty()) restaurantsList.getSelectionModel().select(selectedIndex);
    }

    public void addDishToMenu() throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("dish-form.fxml"));
        Parent parent = fxmlLoader.load();
        DishCreationForm dishCreationForm = fxmlLoader.getController();
        dishCreationForm.setData(entityManagerFactory);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle("Dish creation!");
        stage.setScene(scene);
        stage.show();
        stage.showAndWait();
        reloadRestaurantTab();

    }

    public void removeDishFromMenu() {
        Dishes dish = restaurantMenuTable.getSelectionModel().getSelectedItem();
        if (dish == null) {
            alert("Select a dish to remove first.");
            return;
        }
        if (!confirm("Delete dish \"" + dish.getName() + "\"?")) return;

        try {
            customHibernate.delete(dish.getClass(), dish.getId());
            reloadRestaurantTab();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete dish:\n" + ex.getMessage()).showAndWait();
        }
        reloadRestaurantTab();
    }

    public void editDishInMenu() {
        Dishes dish = restaurantMenuTable.getSelectionModel().getSelectedItem();
        if (dish == null) {
            alert("Select a dish to edit first.");
            return;
        }
        String newDescription = dishDescription.getText() == null ? "" : dishDescription.getText().trim();

        try {
            customHibernate.updateById(Dishes.class, dish.getId(), description -> description.setDescription(newDescription));
            dish.setDescription(newDescription);
            restaurantMenuTable.refresh();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to update dish description:\n" + ex.getMessage()).showAndWait();
        }
        reloadRestaurantTab();
    }

    public void removeIngredientsFromDish() {
        Dishes dish = restaurantMenuTable.getSelectionModel().getSelectedItem();
        if (dish == null) {
            alert("Select a dish to remove ingredients from first.");
            return;
        }
        ObservableList<DishesIngredients> selectedIngredients = selectedDishIngredientsList.getSelectionModel().getSelectedItems();
        if (selectedIngredients == null || selectedIngredients.isEmpty()) {
            alert("Select at least one ingredient to remove.");
            return;
        }
        assert selectedIngredients != null;
        List<DishesIngredients> ingredientsToRemove = List.copyOf(selectedIngredients);
        try {
            customHibernate.updateById(Dishes.class, dish.getId(), managedDish -> {
                List<DishesIngredients> ingredients = managedDish.getDishesIngredients();
                if (ingredients != null) {
                    ingredients.removeAll(ingredientsToRemove);
                }
            });
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to remove ingredients from dish:\n" + ex.getMessage()).showAndWait();
            return;
        }

        if (dish.getDishesIngredients() != null) {
            dish.getDishesIngredients().removeAll(ingredientsToRemove);
        }
        selectedDishIngredients.removeAll(ingredientsToRemove);
        for (DishesIngredients ingredient : ingredientsToRemove) {
            if (!dishesIngredients.contains(ingredient)) {
                dishesIngredients.add(ingredient);
            }
        }
        selectedDishIngredientsList.getSelectionModel().clearSelection();
        //selectedDishIngredientsList.refresh();
        //allDishIngredientsList.refresh();
        reloadRestaurantTab();

    }

    public void addIngredientsToDish() {
        if (customHibernate == null) {
            alert("Data layer not initialized.");
            return;
        }
        Dishes dish = restaurantMenuTable.getSelectionModel().getSelectedItem();
        if (dish == null) {
            alert("Select a dish to edit first.");
            return;
        }
        var selectedIngredients = allDishIngredientsList.getSelectionModel().getSelectedItems();
        if (selectedIngredients == null || selectedIngredients.isEmpty()) {
            alert("Select at least one ingredient to add.");
        }
        assert selectedIngredients != null;
        List<DishesIngredients> ingredientsToAdd = List.copyOf(selectedIngredients);
        customHibernate.updateById(Dishes.class, dish.getId(), managedDish -> {
            List<DishesIngredients> managedIngredients = managedDish.getDishesIngredients();
            if (managedIngredients == null) {
                managedIngredients = new ArrayList<>();
                managedDish.setDishesIngredients(managedIngredients);
            }
            for (DishesIngredients dishesIngredients : ingredientsToAdd) {
                if (!managedIngredients.contains(dishesIngredients)) {
                    managedIngredients.add(dishesIngredients);
                }
            }
        });

        dishesIngredients.removeAll(ingredientsToAdd);
        allDishIngredientsList.getSelectionModel().clearSelection();
        selectedDishIngredients.removeAll(ingredientsToAdd);
        selectedDishIngredientsList.getSelectionModel().clearSelection();
        //selectedDishIngredientsList.refresh();
        reloadRestaurantTab();
    }

    private void loadDishesForRestaurant(Restaurant restaurant) {
        dishes.clear();
        if (restaurantMenuTable != null) {
            restaurantMenuTable.getSelectionModel().clearSelection();
        }
        if (customHibernate == null) {
            clearDishDetails();
            return;
        }
        if (restaurant == null) {
            clearDishDetails();
            return;
        }
        List<Dishes> list = customHibernate.getDishesByRestaurant(restaurant.getId());
        dishes.setAll(list);
        if (!dishes.isEmpty() && restaurantMenuTable != null) {
            restaurantMenuTable.getSelectionModel().selectFirst();
        } else {
            clearDishDetails();
        }

    }

    private void showDishDetails(Dishes dishes) {
        if (dishes == null) {
            clearDishDetails();
            return;
        }
        dishDescription.setText(dishes.getDescription() == null ? "" : dishes.getDescription());

        List<DishesIngredients> dishIngredients = customHibernate.getDishIngredients(dishes.getId());
        if (dishIngredients == null) {
            dishIngredients = List.of();
        }

        selectedDishIngredients.setAll(dishIngredients);

        var availableIngredients = FXCollections.observableArrayList(DishesIngredients.values());
        availableIngredients.removeAll(selectedDishIngredients);
        dishesIngredients.setAll(availableIngredients);


    }

    private void clearDishDetails() {
        dishDescription.clear();
        selectedDishIngredients.clear();
        dishesIngredients.clear();
        selectedDishIngredientsList.getItems().clear();
        allDishIngredientsList.getItems().clear();
    }


    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }


}
