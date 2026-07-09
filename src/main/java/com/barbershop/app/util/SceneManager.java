package com.barbershop.app.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// SPA - Style
public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;
    private final Map<String, String> routes = new HashMap<>();

    private SceneManager() {
        routes.put("login", "/com/barbershop/app/fxml/login.fxml");
        routes.put("register", "/com/barbershop/app/fxml/register.fxml");
        routes.put("customer_dashboard", "/com/barbershop/app/fxml/customer_dashboard.fxml");
        routes.put("barber_dashboard", "/com/barbershop/app/fxml/barber_dashboard.fxml");
        routes.put("admin_dashboard", "/com/barbershop/app/fxml/admin_dashboard.fxml");
        routes.put("booking", "/com/barbershop/app/fxml/booking.fxml");
        routes.put("manage_barbers", "/com/barbershop/app/fxml/manage_barbers.fxml");
        routes.put("manage_services", "/com/barbershop/app/fxml/manage_services.fxml");
        routes.put("manage_appointments", "/com/barbershop/app/fxml/manage_appointments.fxml");
        routes.put("my_bookings", "/com/barbershop/app/fxml/my_bookings.fxml");
    }

    public static synchronized SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
    }

    public void switchScene(String routeKey) {
        try {
            String path = routes.get(routeKey);
            if (path == null) throw new IllegalArgumentException("Unknown route: " + routeKey);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 1000, 700));
            } else {
                primaryStage.getScene().setRoot(root);
            }
            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load scene: " + routeKey, e);
        }
    }

    public <T> T switchSceneAndGetController(String routeKey) {
        try {
            String path = routes.get(routeKey);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 1000, 700));
            } else {
                primaryStage.getScene().setRoot(root);
            }
            primaryStage.show();
            return loader.getController();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load scene: " + routeKey, e);
        }
    }

    public Stage getStage() {
        return primaryStage;
    }
}