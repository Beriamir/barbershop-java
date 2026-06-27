package com.barbershop.app.controller;

import com.barbershop.app.model.Appointment;
import com.barbershop.app.model.User;
import com.barbershop.app.service.AppointmentService;
import com.barbershop.app.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class MyBookingsController {

    @FXML private Label              titleLabel;
    @FXML private ComboBox<String>   statusFilterCombo;
    @FXML private Label              messageLabel;

    @FXML private TableView<Appointment>            bookingsTable;
    @FXML private TableColumn<Appointment, Integer> idCol;
    @FXML private TableColumn<Appointment, String>  barberCol;
    @FXML private TableColumn<Appointment, String>  serviceCol;
    @FXML private TableColumn<Appointment, String>  dateCol;
    @FXML private TableColumn<Appointment, String>  timeCol;
    @FXML private TableColumn<Appointment, String>  statusCol;

    private final AppointmentService appointmentService = new AppointmentService();

    private User currentUser;
    private ObservableList<Appointment> allBookings = FXCollections.observableArrayList();

    private static final List<String> ALL_STATUSES =
            List.of("PENDING", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED");

    @FXML
    private void initialize() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(ALL_STATUSES));

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        barberCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getBarberName()));
        serviceCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getServiceName()));
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDate().toString()));
        timeCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTime().toString()));

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(status);

                String style;

                switch (status) {
                    case "PENDING": {
                        style = "-fx-text-fill: #e67e22;";
                        break;
                    }
                    case "CONFIRMED": {
                        style = "-fx-text-fill: #2980b9;";
                        break;
                    }

                    case "IN_PROGRESS": {
                        style = "-fx-text-fill: #8e44ad;";
                        break;
                    }
                    case "COMPLETED": {
                        style = "-fx-text-fill: #27ae60;";
                        break;
                    }
                    case "CANCELLED": {
                        style = "-fx-text-fill: #c0392b;";
                        break;
                    }
                    default:
                        style = "";
                };

                setStyle(style);
            }
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        titleLabel.setText("My Bookings — " + user.getName());
        loadAll();
    }

    private void loadAll() {
        allBookings = FXCollections.observableArrayList(
                appointmentService.getCustomerAppointments(currentUser.getId()));
        bookingsTable.setItems(allBookings);
        clearMessage();
    }


    @FXML
    private void handleFilter() {
        String filter = statusFilterCombo.getValue();
        if (filter == null) {
            showError("Select a status to filter by.");
            return;
        }
        List<Appointment> filtered = allBookings.stream()
                .filter(a -> filter.equals(a.getStatus()))
                .collect(Collectors.toList());
        bookingsTable.setItems(FXCollections.observableArrayList(filtered));
        clearMessage();
    }

    @FXML
    private void handleClearFilter() {
        statusFilterCombo.setValue(null);
        bookingsTable.setItems(allBookings);
        clearMessage();
    }

    @FXML
    private void handleCancel() {
        Appointment selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No booking selected.");
            return;
        }
        if ("COMPLETED".equals(selected.getStatus()) ||
            "CANCELLED".equals(selected.getStatus())) {
            showError("This booking cannot be cancelled (status: " + selected.getStatus() + ").");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Cancel your booking with " + selected.getBarberName() +
                " on " + selected.getDate() + " at " + selected.getTime() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Cancellation");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    appointmentService.cancelAppointment(selected.getId());
                    showSuccess("Booking cancelled successfully.");
                    loadAll();
                } catch (Exception e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadAll();
    }

    @FXML
    private void handleBack() {
        CustomerDashboardController ctrl = SceneManager.getInstance()
                .switchSceneAndGetController("customer_dashboard");
        ctrl.setCurrentUser(currentUser);
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