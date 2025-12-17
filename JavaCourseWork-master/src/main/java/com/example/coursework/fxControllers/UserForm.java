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
        String plainPassword = passwordField.getText();

        // hash password once
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));

        if (userRadio.isSelected()) {
            User user = new User(
                    loginField.getText(),
                    hashedPassword,
                    nameField.getText(),
                    surnameField.getText(),
                    phoneNumberField.getText(),
                    emailField.getText(),
                    true
            );
            genericHibernate.create(user);

        } else if (restaurantRadio.isSelected()) {
            Restaurant restaurant = new Restaurant(
                    loginField.getText(),
                    hashedPassword,
                    nameField.getText(),
                    surnameField.getText(),
                    restaurantNameField.getText(),
                    phoneNumberField.getText(),
                    emailField.getText(),
                    addressField.getText(),
                    restaurantOpeningTimeField.getText(),
                    restaurantClosingTimeField.getText()
            );
            genericHibernate.create(restaurant);

        } else if (clientRadio.isSelected()) {
            BasicUser basicUser = new BasicUser(
                    loginField.getText(),
                    hashedPassword,
                    nameField.getText(),
                    surnameField.getText(),
                    phoneNumberField.getText(),
                    emailField.getText(),
                    clientAddressField.getText()
            );
            genericHibernate.create(basicUser);

        } else {
            Driver driver = new Driver(
                    loginField.getText(),
                    hashedPassword,
                    nameField.getText(),
                    surnameField.getText(),
                    phoneNumberField.getText(),
                    emailField.getText(),
                    licensePlate.getText(),
                    vehicleTypeComboBox.getValue()
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
}
