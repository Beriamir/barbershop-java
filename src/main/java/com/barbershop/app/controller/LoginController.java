package com.barbershop.app.controller;

import com.barbershop.app.model.User;
import com.barbershop.app.service.UserService;
import com.barbershop.app.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        userService.login(email, password).ifPresentOrElse(this::redirectByRole,
                () -> errorLabel.setText("Invalid email or password."));
    }

    private void redirectByRole(User user) {
        switch (user.getRole()) {
            case "CUSTOMER": {
                CustomerDashboardController ctrl = SceneManager.getInstance()
                        .switchSceneAndGetController("customer_dashboard");
                ctrl.setCurrentUser(user);
                break;
            }
            case "BARBER": {
                BarberDashboardController ctrl = SceneManager.getInstance()
                        .switchSceneAndGetController("barber_dashboard");
                ctrl.setCurrentUser(user);
                break;
            }
            case "ADMIN":
                AdminDashboardController ctrl = SceneManager.getInstance()
                        .switchSceneAndGetController("admin_dashboard");
                ctrl.setCurrentUser(user);
                break;
            default:
                errorLabel.setText("Unknown role.");
        }
    }

    @FXML
    private void goToRegister() {
        SceneManager.getInstance().switchScene("register");
    }
}