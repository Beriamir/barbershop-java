package com.barbershop.app.controller;

import com.barbershop.app.model.Appointment;
import com.barbershop.app.model.Barber;
import com.barbershop.app.model.ServiceItem;
import com.barbershop.app.service.AppointmentService;
import com.barbershop.app.service.BarberService;
import com.barbershop.app.service.ServiceService;
import com.barbershop.app.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageAppointmentsController {

    // ── Walk-in panel ────────────────────────────────────────────────────────
    @FXML private TextField            walkInNameField;
    @FXML private ComboBox<ServiceItem> walkInServiceCombo;
    @FXML private ComboBox<Barber>     walkInBarberCombo;
    @FXML private DatePicker           walkInDatePicker;
    @FXML private ComboBox<LocalTime>  walkInTimeCombo;
    @FXML private Label                walkInMessageLabel;

    // ── Filter / action bar ──────────────────────────────────────────────────
    @FXML private ComboBox<String>     statusFilterCombo;
    @FXML private ComboBox<String>     newStatusCombo;
    @FXML private Label                messageLabel;

    // ── Table ────────────────────────────────────────────────────────────────
    @FXML private TableView<Appointment>            appointmentsTable;
    @FXML private TableColumn<Appointment, Integer> idCol;
    @FXML private TableColumn<Appointment, String>  customerCol;
    @FXML private TableColumn<Appointment, String>  barberCol;
    @FXML private TableColumn<Appointment, String>  serviceCol;
    @FXML private TableColumn<Appointment, String>  dateCol;
    @FXML private TableColumn<Appointment, String>  timeCol;
    @FXML private TableColumn<Appointment, String>  statusCol;
    @FXML private TableColumn<Appointment, String>  notesCol;

    private final AppointmentService appointmentService = new AppointmentService();
    private final BarberService      barberService      = new BarberService();
    private final ServiceService     serviceService     = new ServiceService();

    private static final List<String> ALL_STATUSES =
            List.of("PENDING", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED");

    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    // ── Initialize ───────────────────────────────────────────────────────────
    @FXML
    private void initialize() {
        setupWalkInPanel();
        setupFilterBar();
        setupTable();
        loadAll();
    }

    // ── Walk-in panel setup ──────────────────────────────────────────────────
    private void setupWalkInPanel() {
        walkInServiceCombo.setItems(
                FXCollections.observableArrayList(serviceService.getAll()));
        walkInBarberCombo.setItems(
                FXCollections.observableArrayList(barberService.getAll()));
        walkInDatePicker.setValue(LocalDate.now());
        walkInTimeCombo.setItems(
                FXCollections.observableArrayList(generateTimeSlots()));
        walkInTimeCombo.setValue(roundedNow());
    }

    // ── Filter bar setup ─────────────────────────────────────────────────────
    private void setupFilterBar() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(ALL_STATUSES));
        newStatusCombo.setItems(FXCollections.observableArrayList(ALL_STATUSES));
    }

    // ── Table setup ──────────────────────────────────────────────────────────
    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Use getDisplayCustomerName() so walk-ins show guest name + "(Walk-in)" tag
        customerCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDisplayCustomerName()));
        barberCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getBarberName()));
        serviceCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getServiceName()));
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDate().toString()));
        timeCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTime().toString()));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        notesCol.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getNotes() != null ? c.getValue().getNotes() : ""));

        // Colour-code status column
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setText(null); setStyle(""); return; }
                setText(status);
                String style;

                switch (status) {
                    case "PENDING":
                        style = "-fx-text-fill: #e67e22;";
                        break; // orange
                    case "CONFIRMED":
                        style = "-fx-text-fill: #2980b9;";
                        break; // blue
                    case "IN_PROGRESS":
                        style = "-fx-text-fill: #8e44ad;";
                        break; // purple
                    case "COMPLETED":
                        style = "-fx-text-fill: #27ae60;";
                        break; // green
                    case "CANCELLED":
                        style = "-fx-text-fill: #c0392b;";
                        break; // red
                    default:
                        style = "";
                };

                setStyle(style);
            }
        });

        // Highlight walk-in rows in the customer column
        customerCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) { setText(null); setStyle(""); return; }
                setText(value);
                setStyle(value.contains("(Walk-in)")
                        ? "-fx-text-fill: #2980b9; -fx-font-style: italic;"
                        : "");
            }
        });
    }

    // ── Data loading ─────────────────────────────────────────────────────────
    private void loadAll() {
        allAppointments = FXCollections.observableArrayList(
                appointmentService.getAllAppointments());
        appointmentsTable.setItems(allAppointments);
        clearMessage();
    }

    // ── Walk-in handlers ─────────────────────────────────────────────────────
    @FXML
    private void handleBookWalkIn() {
        String      guestName = walkInNameField.getText().trim();
        ServiceItem service   = walkInServiceCombo.getValue();
        Barber      barber    = walkInBarberCombo.getValue();
        LocalDate   date      = walkInDatePicker.getValue();
        LocalTime   time      = walkInTimeCombo.getValue();

        if (guestName.isBlank() || service == null ||
            barber == null || date == null || time == null) {
            showWalkInError("Please fill in all walk-in fields.");
            return;
        }
        
        if (date.isBefore(LocalDate.now())) {
            showWalkInError("Cannot book a date in the past.");
            return;
        }

        try {
            Appointment appt = appointmentService.bookWalkIn(
                    guestName, barber.getUserId(), service.getId(), date, time);

            showWalkInSuccess("Walk-in booked for \"" + guestName + "\" → #" + appt.getId()
                    + " with " + barber.getName()
                    + " at " + time + " on " + date + ".");
            handleClearWalkIn();
            loadAll();

        } catch (IllegalArgumentException | IllegalStateException e) {
            showWalkInError(e.getMessage());
        }
    }

    @FXML
    private void handleClearWalkIn() {
        walkInNameField.clear();
        walkInServiceCombo.setValue(null);
        walkInBarberCombo.setValue(null);
        walkInDatePicker.setValue(LocalDate.now());
        walkInTimeCombo.setValue(roundedNow());
        walkInMessageLabel.setText("");
    }

    // ── Filter handlers ───────────────────────────────────────────────────────
    @FXML
    private void handleFilter() {
        String filter = statusFilterCombo.getValue();
        if (filter == null) { showError("Select a status to filter by."); return; }
        List<Appointment> filtered = allAppointments.stream()
                .filter(a -> filter.equals(a.getStatus()))
                .collect(Collectors.toList());
        appointmentsTable.setItems(FXCollections.observableArrayList(filtered));
        clearMessage();
    }

    @FXML
    private void handleShowAll() {
        statusFilterCombo.setValue(null);
        appointmentsTable.setItems(allAppointments);
        clearMessage();
    }

    // ── Status update handlers ────────────────────────────────────────────────
    @FXML
    private void handleUpdateStatus() {
        Appointment selected = getSelected();
        if (selected == null) return;
        String newStatus = newStatusCombo.getValue();
        if (newStatus == null) { showError("Choose a target status first."); return; }
        try {
            appointmentService.adminUpdateStatus(selected.getId(), newStatus);
            showSuccess("Status updated to " + newStatus + ".");
            loadAll();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleCancelSelected() {
        Appointment selected = getSelected();
        if (selected == null) return;
        try {
            appointmentService.cancelAppointment(selected.getId());
            showSuccess("Appointment cancelled.");
            loadAll();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ── Delete handler ────────────────────────────────────────────────────────
    @FXML
    private void handleDelete() {
        Appointment selected = getSelected();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Permanently delete appointment #" + selected.getId() +
                " for " + selected.getDisplayCustomerName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    appointmentService.deleteAppointment(selected.getId());
                    showSuccess("Appointment deleted.");
                    loadAll();
                } catch (Exception e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleRefresh() { loadAll(); }

    @FXML
    private void handleBack() {
        SceneManager.getInstance().switchScene("admin_dashboard");
    }

    // ── Utilities ─────────────────────────────────────────────────────────────
    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime t = LocalTime.of(9, 0);
        while (!t.isAfter(LocalTime.of(18, 0))) {
            slots.add(t);
            t = t.plusMinutes(30);
        }
        return slots;
    }

    /** Returns the nearest 30-min slot at or after the current time, capped to 18:00. */
    private LocalTime roundedNow() {
        LocalTime now = LocalTime.now();
        int mins = (now.getMinute() < 30) ? 30 : 0;
        int hour = (now.getMinute() < 30) ? now.getHour() : now.getHour() + 1;
        LocalTime rounded = LocalTime.of(Math.min(hour, 18), mins == 30 && hour > 18 ? 0 : mins);
        return rounded.isAfter(LocalTime.of(18, 0)) ? LocalTime.of(18, 0) : rounded;
    }

    private Appointment getSelected() {
        Appointment sel = appointmentsTable.getSelectionModel().getSelectedItem();
        if (sel == null) showError("No appointment selected.");
        return sel;
    }

    private void showWalkInError(String msg) {
        walkInMessageLabel.setStyle("-fx-text-fill: red;");
        walkInMessageLabel.setText(msg);
    }

    private void showWalkInSuccess(String msg) {
        walkInMessageLabel.setStyle("-fx-text-fill: green;");
        walkInMessageLabel.setText(msg);
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(msg);
    }

    private void showSuccess(String msg) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(msg);
    }

    private void clearMessage() { messageLabel.setText(""); }
}