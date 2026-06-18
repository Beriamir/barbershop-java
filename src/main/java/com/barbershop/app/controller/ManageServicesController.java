package com.barbershop.app.controller;

import com.barbershop.app.model.ServiceItem;
import com.barbershop.app.service.ServiceService;
import com.barbershop.app.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;

public class ManageServicesController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField durationField;
    @FXML private Label messageLabel;

    @FXML private TableView<ServiceItem> servicesTable;
    @FXML private TableColumn<ServiceItem, Integer> idCol;
    @FXML private TableColumn<ServiceItem, String> nameCol;
    @FXML private TableColumn<ServiceItem, Object> priceCol;
    @FXML private TableColumn<ServiceItem, Integer> durationCol;

    private final ServiceService serviceService = new ServiceService();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));

        servicesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                nameField.setText(sel.getName());
                priceField.setText(sel.getPrice().toString());
                durationField.setText(String.valueOf(sel.getDuration()));
            }
        });

        refresh();
    }

    private void refresh() {
        servicesTable.setItems(FXCollections.observableArrayList(serviceService.getAll()));
    }

    @FXML
    private void handleAdd() {
        try {
            serviceService.create(nameField.getText(), new BigDecimal(priceField.getText()),
                    Integer.parseInt(durationField.getText()));
            refresh();
            messageLabel.setText("");
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        ServiceItem selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { messageLabel.setText("Select a service first."); return; }
        try {
            selected.setName(nameField.getText());
            selected.setPrice(new BigDecimal(priceField.getText()));
            selected.setDuration(Integer.parseInt(durationField.getText()));
            serviceService.update(selected);
            refresh();
            messageLabel.setText("");
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        ServiceItem selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { messageLabel.setText("Select a service first."); return; }
        serviceService.delete(selected.getId());
        refresh();
    }

    @FXML
    private void handleBack() {
        SceneManager.getInstance().switchScene("admin_dashboard");
    }
}