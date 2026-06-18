package com.barbershop.app;

import com.barbershop.app.util.DBInitializer;
import com.barbershop.app.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Step 1: Initialize DB (creates DB + tables if not exist)
        DBInitializer.initialize();

        // Step 2: Set up SceneManager with the single primary stage
        SceneManager.getInstance().init(primaryStage);

        primaryStage.setTitle("Barbershop Booking System");
        SceneManager.getInstance().switchScene("login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}