package com.barbershop.app.controller;

import com.barbershop.app.model.Barber;
import com.barbershop.app.model.ServiceItem;
import com.barbershop.app.model.User;
import com.barbershop.app.service.AppointmentService;
import com.barbershop.app.service.BarberService;
import com.barbershop.app.service.ServiceService;
import com.barbershop.app.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingController {

    @FXML private ComboBox<ServiceItem> serviceComboBox;
    @FXML private ComboBox<Barber> barberComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<LocalTime> timeComboBox;
    @FXML private Label messageLabel;

    private final ServiceService serviceService = new ServiceService();
    private final BarberService barberService = new BarberService();
    private final AppointmentService appointmentService = new AppointmentService();

    private User currentCustomer;

    public void setCustomer(User customer) {
        this.currentCustomer = customer;
    }

    @FXML
    private void initialize() {
        serviceComboBox.setItems(FXCollections.observableArrayList(serviceService.getAll()));
        barberComboBox.setItems(FXCollections.observableArrayList(barberService.getAll()));
        timeComboBox.setItems(FXCollections.observableArrayList(generateTimeSlots()));
    }

    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime t = LocalTime.of(9, 0);
        while (!t.isAfter(LocalTime.of(18, 0))) {
            slots.add(t);
            t = t.plusMinutes(30);
        }
        return slots;
    }

    @FXML
    private void handleConfirmBooking() {
        ServiceItem service = serviceComboBox.getValue();
        Barber barber = barberComboBox.getValue();
        var date = datePicker.getValue();
        LocalTime time = timeComboBox.getValue();

        if (service == null || barber == null || date == null || time == null) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            appointmentService.bookAppointment(
                    currentCustomer.getId(), barber.getUserId(), service.getId(), date, time);
            messageLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            messageLabel.setText("Booking confirmed!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        CustomerDashboardController ctrl = SceneManager.getInstance()
                .switchSceneAndGetController("customer_dashboard");
        ctrl.setCurrentUser(currentCustomer);
    }
}