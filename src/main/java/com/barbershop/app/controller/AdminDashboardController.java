package com.barbershop.app.controller;

import com.barbershop.app.service.AppointmentService;
import com.barbershop.app.util.SceneManager;
import com.barbershop.app.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML
    private BarChart<String, Number> bookingsChart;
    @FXML
    private Label welcomeLabel;

    private final AppointmentService appointmentService = new AppointmentService();
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + user.getName());
        }
        loadChart();
    }

    @FXML
    private void initialize() {
        loadChart();
    }

    private void loadChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bookings");
        for (Object[] row : appointmentService.bookingsPerBarber()) {
            series.getData().add(new XYChart.Data<>((String) row[0], (Integer) row[1]));
        }
        bookingsChart.setData(FXCollections.observableArrayList(series));
    }

    @FXML
    private void handleManageBarbers() {
        SceneManager.getInstance().switchScene("manage_barbers");
    }

    @FXML
    private void handleManageServices() {
        SceneManager.getInstance().switchScene("manage_services");
    }

    @FXML
    private void handleManageAppointments() {
        SceneManager.getInstance().switchScene("manage_appointments");
    }

    @FXML
    private void handleLogout() {
        SceneManager.getInstance().switchScene("login");
    }
}