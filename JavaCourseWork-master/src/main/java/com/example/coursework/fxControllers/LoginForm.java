package com.example.coursework.fxControllers;


import com.example.coursework.HelloApplication;
import com.example.coursework.hibernateControl.CustomHibernate;
import com.example.coursework.model.User;
import com.example.coursework.utils.FxUtils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginForm {
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;

    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("wolt");

    public void validateAndLoad() throws IOException {

        CustomHibernate customHibernate = new CustomHibernate(entityManagerFactory);
        User user = customHibernate.getUserByCredentials(usernameField.getText(), passwordField.getText());
        if (user != null || (usernameField.getText().isBlank() && passwordField.getText().isBlank())) {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-form.fxml"));
            Parent parent = fxmlLoader.load();
            MainForm mainForm = fxmlLoader.getController();
            mainForm.setData(entityManagerFactory, user);
            Scene scene = new Scene(parent);
            Stage stage = (Stage) passwordField.getScene().getWindow();
            stage.setTitle("Main form!");
            stage.setScene(scene);
            stage.show();
        } else {
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Wrong credentials", "Login", "No such user or wrong password");

        }
    }

    public void registerNewUser() throws IOException {
        registerNewUser(null);
    }

    public void registerNewUser(User currentUser) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-form.fxml"));
        Parent parent = fxmlLoader.load();
        UserForm userForm = fxmlLoader.getController();
        CustomHibernate customHibernate = new CustomHibernate(entityManagerFactory);
        boolean allowUserRegistration = !customHibernate.hasBaseUser()
                || (currentUser != null && currentUser.getClass().equals(User.class));

        userForm.setData(entityManagerFactory, allowUserRegistration);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle("Register form!");
        stage.setScene(scene);
        stage.show();
    }
}
