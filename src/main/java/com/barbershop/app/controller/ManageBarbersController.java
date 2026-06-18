package com.barbershop.app.controller;

import com.barbershop.app.model.Barber;
import com.barbershop.app.service.BarberService;
import com.barbershop.app.util.SceneManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageBarbersController {

    // ── Form fields ──────────────────────────────────────────────────────────
    @FXML private TextField     nameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         messageLabel;

    // ── Table ────────────────────────────────────────────────────────────────
    @FXML private TableView<Barber>               barbersTable;
    @FXML private TableColumn<Barber, Integer>    idCol;
    @FXML private TableColumn<Barber, Integer>    userIdCol;
    @FXML private TableColumn<Barber, String>     nameCol;
    @FXML private TableColumn<Barber, String>     emailCol;

    private final BarberService barberService = new BarberService();

    // ── Initialize ───────────────────────────────────────────────────────────
    @FXML
    private void initialize() {
        // Wire columns
        idCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getId()).asObject());
        userIdCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getUserId()).asObject());
        nameCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));
        emailCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmail()));

        // Clicking a row populates the form fields
        barbersTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, selected) -> {
                    if (selected != null) {
                        nameField.setText(selected.getName());
                        emailField.setText(selected.getEmail());
                        passwordField.clear(); // never pre-fill password
                    }
                });

        loadAll();
    }

    // ── Data loading ─────────────────────────────────────────────────────────
    private void loadAll() {
        barbersTable.setItems(
                FXCollections.observableArrayList(barberService.getAll()));
        clearMessage();
    }

    // ── Button handlers ──────────────────────────────────────────────────────

    /**
     * Creates a new users row (role = BARBER) and a linked barbers row.
     * Password is required for new barbers.
     */
    @FXML
    private void handleAdd() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            showError("Name, email, and password are all required to add a barber.");
            return;
        }

        try {
            barberService.createBarber(name, email, password);
            showSuccess("Barber \"" + name + "\" created successfully.");
            clearFields();
            loadAll();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Updates the name and email of the selected barber's underlying user record.
     * If the password field is filled in, the password is updated too.
     */
    @FXML
    private void handleUpdate() {
        Barber selected = getSelected();
        if (selected == null) return;

        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (name.isBlank() || email.isBlank()) {
            showError("Name and email cannot be blank.");
            return;
        }

        try {
            // Update name/email (and optionally password)
            barberService.updateBarber(selected, name, email,
                    password.isBlank() ? null : password);
            showSuccess("Barber updated successfully.");
            clearFields();
            loadAll();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Deletes the barbers row AND the underlying users row.
     * All linked appointments are cascade-deleted by the DB foreign key.
     */
    @FXML
    private void handleDelete() {
        Barber selected = getSelected();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete barber \"" + selected.getName() + "\"?\n" +
                "This will also remove their user account and all linked appointments.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    barberService.deleteBarber(selected);
                    showSuccess("Barber deleted.");
                    clearFields();
                    loadAll();
                } catch (Exception e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        clearFields();
        barbersTable.getSelectionModel().clearSelection();
        clearMessage();
    }

    @FXML
    private void handleRefresh() {
        loadAll();
    }

    @FXML
    private void handleBack() {
        SceneManager.getInstance().switchScene("admin_dashboard");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private Barber getSelected() {
        Barber sel = barbersTable.getSelectionModel().getSelectedItem();
        if (sel == null) showError("No barber selected.");
        return sel;
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(msg);
    }

    private void showSuccess(String msg) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(msg);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }
}