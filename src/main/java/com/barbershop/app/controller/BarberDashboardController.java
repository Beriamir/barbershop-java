package com.barbershop.app.controller;

import com.barbershop.app.model.Appointment;
import com.barbershop.app.model.User;
import com.barbershop.app.service.AppointmentService;
import com.barbershop.app.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class BarberDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> dateCol;
    @FXML private TableColumn<Appointment, String> timeCol;
    @FXML private TableColumn<Appointment, String> customerCol;
    @FXML private TableColumn<Appointment, String> serviceCol;
    @FXML private TableColumn<Appointment, String> statusCol;
    @FXML private TableColumn<Appointment, Void> actionCol;

    private final AppointmentService appointmentService = new AppointmentService();
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getName());
        loadAppointments();
    }

    @FXML
    private void initialize() {
        dateCol.setCellValueFactory(a -> new javafx.beans.property.SimpleStringProperty(
                a.getValue().getDate().toString()));
        timeCol.setCellValueFactory(a -> new javafx.beans.property.SimpleStringProperty(
                a.getValue().getTime().toString()));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        serviceCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button startBtn = new Button("Start");
            private final Button completeBtn = new Button("Complete");
            private final HBox box = new HBox(5, startBtn, completeBtn);

            {
                startBtn.setOnAction(e -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    try {
                        appointmentService.startService(appt.getId(), currentUser.getId());
                        loadAppointments();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                    }
                });
                completeBtn.setOnAction(e -> {
                    Appointment appt = getTableView().getItems().get(getIndex());
                    try {
                        appointmentService.completeService(appt.getId(), currentUser.getId());
                        loadAppointments();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                Appointment appt = getTableView().getItems().get(getIndex());
                startBtn.setDisable(!"CONFIRMED".equals(appt.getStatus()));
                completeBtn.setDisable(!"IN_PROGRESS".equals(appt.getStatus()));
                setGraphic(box);
            }
        });
    }

    private void loadAppointments() {
        appointmentsTable.setItems(FXCollections.observableArrayList(
                appointmentService.getBarberAppointments(currentUser.getId())));
    }

    @FXML
    private void handleRefresh() {
        loadAppointments();
    }

    @FXML
    private void handleLogout() {
        SceneManager.getInstance().switchScene("login");
    }
}