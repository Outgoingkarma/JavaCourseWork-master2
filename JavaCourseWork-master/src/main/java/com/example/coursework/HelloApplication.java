package com.example.coursework;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login-form.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login Form!");
        stage.setScene(scene);
        stage.show();
    }
}
