package com.barbershop.app.controller;

import com.barbershop.app.model.ServiceItem;
import com.barbershop.app.model.User;
import com.barbershop.app.service.ServiceService;
import com.barbershop.app.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<ServiceItem> servicesTable;
    @FXML private TableColumn<ServiceItem, String> serviceNameCol;
    @FXML private TableColumn<ServiceItem, Object> servicePriceCol;
    @FXML private TableColumn<ServiceItem, Integer> serviceDurationCol;

    private final ServiceService serviceService = new ServiceService();
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getName());
        loadServices();
    }

    @FXML
    private void initialize() {
        serviceNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        servicePriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        serviceDurationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
    }

    private void loadServices() {
        servicesTable.setItems(FXCollections.observableArrayList(serviceService.getAll()));
    }

    @FXML
    private void handleBookAppointment() {
        BookingController ctrl = SceneManager.getInstance().switchSceneAndGetController("booking");
        ctrl.setCustomer(currentUser);
    }

    @FXML
    private void handleViewBookings() {
        MyBookingsController ctrl = SceneManager.getInstance().switchSceneAndGetController("my_bookings");
        ctrl.setCurrentUser(currentUser);
    }

    @FXML
    private void handleLogout() {
        SceneManager.getInstance().switchScene("login");
    }
}