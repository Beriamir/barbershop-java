package com.barbershop.app.controller;

import com.barbershop.app.model.Appointment;
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

public class ManageAppointmentsController {

    // ── Filter / Action bar ──────────────────────────────────────────────────
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> newStatusCombo;
    @FXML private Label messageLabel;

    // ── Table ────────────────────────────────────────────────────────────────
    @FXML private TableView<Appointment>               appointmentsTable;
    @FXML private TableColumn<Appointment, Integer>    idCol;
    @FXML private TableColumn<Appointment, String>     customerCol;
    @FXML private TableColumn<Appointment, String>     barberCol;
    @FXML private TableColumn<Appointment, String>     serviceCol;
    @FXML private TableColumn<Appointment, String>     dateCol;
    @FXML private TableColumn<Appointment, String>     timeCol;
    @FXML private TableColumn<Appointment, String>     statusCol;

    private final AppointmentService appointmentService = new AppointmentService();

    // All appointments currently loaded (before client-side filter)
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    // ── Allowed status values ────────────────────────────────────────────────
    private static final List<String> ALL_STATUSES =
            List.of("PENDING", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED");

    // ── Initialize ───────────────────────────────────────────────────────────
    @FXML
    private void initialize() {
        // Populate filter & update combos
        statusFilterCombo.setItems(FXCollections.observableArrayList(ALL_STATUSES));
        newStatusCombo.setItems(FXCollections.observableArrayList(ALL_STATUSES));

        // Wire table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        customerCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCustomerName()));
        barberCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getBarberName()));
        serviceCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getServiceName()));

        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDate().toString()));
        timeCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTime().toString()));

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Colour-code status cells
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

        loadAll();
    }

    // ── Data loading ─────────────────────────────────────────────────────────
    private void loadAll() {
        allAppointments = FXCollections.observableArrayList(
                appointmentService.getAllAppointments());
        appointmentsTable.setItems(allAppointments);
        clearMessage();
    }

    // ── Button handlers ──────────────────────────────────────────────────────

    /** Filter the table client-side by the chosen status. */
    @FXML
    private void handleFilter() {
        String filter = statusFilterCombo.getValue();
        if (filter == null) {
            showError("Please select a status to filter by.");
            return;
        }
        List<Appointment> filtered = allAppointments.stream()
                .filter(a -> filter.equals(a.getStatus()))
                .collect(Collectors.toList());
        appointmentsTable.setItems(FXCollections.observableArrayList(filtered));
        clearMessage();
    }

    /** Reset the table to show all appointments. */
    @FXML
    private void handleShowAll() {
        statusFilterCombo.setValue(null);
        appointmentsTable.setItems(allAppointments);
        clearMessage();
    }

    /** Set the selected appointment to whichever status is chosen in newStatusCombo. */
    @FXML
    private void handleUpdateStatus() {
        Appointment selected = getSelected();
        if (selected == null) return;

        String newStatus = newStatusCombo.getValue();
        if (newStatus == null) {
            showError("Choose a target status first.");
            return;
        }

        try {
            appointmentService.adminUpdateStatus(selected.getId(), newStatus);
            showSuccess("Status updated to " + newStatus + ".");
            loadAll();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    /** Convenience: immediately cancel the selected appointment. */
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

    /** Hard-delete the selected appointment row. Asks for confirmation first. */
    @FXML
    private void handleDelete() {
        Appointment selected = getSelected();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Permanently delete appointment #" + selected.getId() + "?",
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
    private void handleRefresh() {
        loadAll();
    }

    @FXML
    private void handleBack() {
        SceneManager.getInstance().switchScene("admin_dashboard");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Appointment getSelected() {
        Appointment sel = appointmentsTable.getSelectionModel().getSelectedItem();
        if (sel == null) showError("No appointment selected.");
        return sel;
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