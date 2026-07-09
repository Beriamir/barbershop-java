package com.barbershop.app.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private int id;
    private int customerId;
    private int barberId;
    private int serviceId;
    private LocalDate date;
    private LocalTime time;
    private String status; // PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    private String notes;

    private String customerName;
    private String barberName;
    private String serviceName;

    public Appointment() {}

    public Appointment(int id, int customerId, int barberId, int serviceId,
                        LocalDate date, LocalTime time, String status, String notes) {
        this.id = id;
        this.customerId = customerId;
        this.barberId = barberId;
        this.serviceId = serviceId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getBarberId() { return barberId; }
    public void setBarberId(int barberId) { this.barberId = barberId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getBarberName() { return barberName; }
    public void setBarberName(String barberName) { this.barberName = barberName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getDisplayCustomerName() {
        if (notes != null && !notes.isBlank()) return notes + " (Walk-in)";
        return customerName != null ? customerName : "Unknown";
    }
}