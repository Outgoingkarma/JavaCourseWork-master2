package com.example.coursework.fxControllers;

import com.example.coursework.hibernateControl.GenericHibernate;
import com.example.coursework.model.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserForm implements Initializable {

    @FXML
    public RadioButton userRadio;
    @FXML
    public RadioButton restaurantRadio;
    @FXML
    public RadioButton clientRadio;
    @FXML
    public RadioButton driverRadio;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField phoneNumberField;
    @FXML
    public Button saveButton;
    @FXML
    public TextField addressField;
    @FXML
    public VBox restaurantInfoPane;
    @FXML
    public ToggleGroup userType;
    @FXML
    public VBox driverInfoPane1;
    @FXML
    public VBox clientInfoPane11;
    @FXML
    public TextField licensePlate;
    @FXML
    public TextField clientAddressField;
    @FXML
    public TextField restaurantNameField;
    @FXML
    public TextField restaurantOpeningTimeField;
    @FXML
    public TextField restaurantClosingTimeField;
    @FXML
    public TextField restaurantDescriptionField;
    @FXML
    public ComboBox<DriverVehicleType> vehicleTypeComboBox;
    @FXML
    public TextField emailField;

    private EntityManagerFactory entityManagerFactory;
    private GenericHibernate genericHibernate;


    public void setData(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.genericHibernate = new GenericHibernate(entityManagerFactory);
    }

    public void disableFields() {
        if (userRadio.isSelected()) {

            restaurantInfoPane.setVisible(false);
            driverInfoPane1.setVisible(false);
            clientInfoPane11.setVisible(false);

        } else if (restaurantRadio.isSelected()) {

            restaurantInfoPane.setVisible(true);
            driverInfoPane1.setVisible(false);
            clientInfoPane11.setVisible(false);

        } else if (clientRadio.isSelected()) {

            restaurantInfoPane.setVisible(false);
            driverInfoPane1.setVisible(false);
            clientInfoPane11.setVisible(true);

        } else {

            restaurantInfoPane.setVisible(false);
            driverInfoPane1.setVisible(true);
            clientInfoPane11.setVisible(false);
        }
    }

    public void createNewUser() {
        String login = safeTrim(loginField.getText());
        String plainPassword = passwordField.getText() == null ? "" : passwordField.getText();
        String name = safeTrim(nameField.getText());
        String surname = safeTrim(surnameField.getText());
        String phoneNumber = safeTrim(phoneNumberField.getText());
        String email = safeTrim(emailField.getText());
        String restaurantName = safeTrim(restaurantNameField.getText());
        String restaurantAddress = safeTrim(addressField.getText());
        String restaurantOpen = safeTrim(restaurantOpeningTimeField.getText());
        String restaurantClose = safeTrim(restaurantClosingTimeField.getText());
        String clientAddress = safeTrim(clientAddressField.getText());
        String driverLicensePlate = safeTrim(licensePlate.getText());
        DriverVehicleType vehicleType = vehicleTypeComboBox.getValue();

        List<String> validationErrors = validateInputs(login, plainPassword, name, surname, phoneNumber, email,
                restaurantName, restaurantAddress, restaurantOpen, restaurantClose, clientAddress, driverLicensePlate, vehicleType);

        if (!validationErrors.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, String.join("\n", validationErrors)).showAndWait();
            return;
        }

        // hash password once
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));

        if (userRadio.isSelected()) {
            User user = new User(
                    login,
                    hashedPassword,
                    name,
                    surname,
                    phoneNumber,
                    email,
                    true
            );
            genericHibernate.create(user);

        } else if (restaurantRadio.isSelected()) {
            Restaurant restaurant = new Restaurant(
                    login,,
                    hashedPassword,
                    name,
                    surname,
                    restaurantName,
                    phoneNumber,
                    email,
                    restaurantAddress,
                    restaurantOpen,
                    restaurantClose
            );
            genericHibernate.create(restaurant);

        } else if (clientRadio.isSelected()) {
            BasicUser basicUser = new BasicUser(
                    login,
                    hashedPassword,
                    name,
                    surname,
                    phoneNumber,
                    email,
                    clientAddress
            );
            genericHibernate.create(basicUser);

        } else {
            Driver driver = new Driver(
                    login,
                    hashedPassword,
                    name,
                    surname,
                    phoneNumber,
                    email,
                    driverLicensePlate,
                    vehicleType
            );
            genericHibernate.create(driver);
        }

        saveButton.getScene().getWindow().hide();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        disableFields();
        vehicleTypeComboBox.getItems().addAll(DriverVehicleType.values());


    }



    private List<String> validateInputs(String login, String password, String name, String surname, String phoneNumber,
                                        String email, String restaurantName, String restaurantAddress,
                                        String restaurantOpen, String restaurantClose, String clientAddress,
                                        String driverLicensePlate, DriverVehicleType vehicleType) {
        List<String> errors = new ArrayList<>();

        if (login.isEmpty()) errors.add("Login is required");
        if (password.isBlank()) errors.add("Password is required");
        if (!password.isBlank() && password.length() < 6) errors.add("Password must be at least 6 characters long");
        if (name.isEmpty()) errors.add("Name is required");
        if (surname.isEmpty()) errors.add("Surname is required");
        if (phoneNumber.isEmpty()) {
            errors.add("Phone number is required");
        } else if (!phoneNumber.matches("[\\d+\- ]+")) {
            errors.add("Phone number contains invalid characters");
        }

        if (email.isEmpty()) {
            errors.add("Email is required");
        } else if (!email.matches("^.+@.+\\..+$")) {
            errors.add("Email format is invalid");
        }

        if (restaurantRadio.isSelected()) {
            if (restaurantName.isEmpty()) errors.add("Restaurant name is required");
            if (restaurantAddress.isEmpty()) errors.add("Restaurant address is required");
            if (restaurantOpen.isEmpty()) errors.add("Restaurant opening time is required");
            if (restaurantClose.isEmpty()) errors.add("Restaurant closing time is required");
        }

        if (clientRadio.isSelected()) {
            if (clientAddress.isEmpty()) errors.add("Client address is required");
        }

        if (driverRadio.isSelected()) {
            if (driverLicensePlate.isEmpty()) errors.add("License plate is required");
            if (vehicleType == null) errors.add("Vehicle type must be selected");
        }

        return errors;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
