package com.barbershop.app.controller;

import com.barbershop.app.service.UserService;
import com.barbershop.app.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        
        if (name.isBlank() || email.isBlank()) {
            errorLabel.setText("Name and email are required.");
            return;
        }

        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        if (password.length() < 4) {
            errorLabel.setText("Password must be at least 4 characters");
            return;
        }

        try {
            userService.register(name, email, password, "CUSTOMER");
            SceneManager.getInstance().switchScene("login");
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        SceneManager.getInstance().switchScene("login");
    }
}