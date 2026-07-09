package com.barbershop.app.service;

import com.barbershop.app.dao.AppointmentDAO;
import com.barbershop.app.dao.UserDAO;
import com.barbershop.app.model.Appointment;
import com.barbershop.app.util.DBInitializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final UserDAO userDAO = new UserDAO();

    public Appointment bookAppointment(int customerId, int barberId, int serviceId,
                                        LocalDate date, LocalTime time) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book a date in the past.");
        }
        if (appointmentDAO.existsConflict(barberId, date, time)) {
            throw new IllegalStateException("This barber is already booked at the selected date/time.");
        }
        Appointment appt = new Appointment(0, customerId, barberId, serviceId, date, time, "PENDING", null);
        return appointmentDAO.create(appt);
    }
    
    // Walk-in booking (admin only)
    public Appointment bookWalkIn(String guestName, int barberId, int serviceId, LocalDate date, LocalTime time) {
        if (guestName == null || guestName.isBlank()) {
            throw new IllegalArgumentException("Guest name is required for walk-in booking.");
        }
        
        if (appointmentDAO.existsConflict(barberId, date, time)) {
            throw new IllegalStateException("This barber is already booked at the selected date time.");
        }
        
        int walkInUserId = userDAO.findByEmail(DBInitializer.WALKIN_EMAIL)
                .orElseThrow(() -> 
                    new RuntimeException("Walk-in sentinel user not found. Re-run DB initialization."
                ))
                .getId();
        
        Appointment appt = new Appointment(0, walkInUserId, barberId, serviceId, date, time, "CONFIRMED", guestName.trim());
        
        return appointmentDAO.create(appt);
    }

    public List<Appointment> getCustomerAppointments(int customerId) {
        return appointmentDAO.findByCustomer(customerId);
    }

    public List<Appointment> getBarberAppointments(int barberId) {
        return appointmentDAO.findByBarber(barberId);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.findAll();
    }

    public boolean cancelAppointment(int appointmentId) {
        Appointment appt = requireById(appointmentId);
        if ("COMPLETED".equals(appt.getStatus())) {
            throw new IllegalStateException("Cannot cancel a completed appointment.");
        }
        return appointmentDAO.updateStatus(appointmentId, "CANCELLED");
    }

    public boolean confirmAppointment(int appointmentId) {
        return transition(appointmentId, "PENDING", "CONFIRMED");
    }
    
    public boolean deleteAppointment(int appointmentId) {
        return appointmentDAO.delete(appointmentId);
    }

    public boolean startService(int appointmentId, int barberId) {
        Appointment appt = requireById(appointmentId);
        enforceBarberOwnership(appt, barberId);
        return transition(appointmentId, "CONFIRMED", "IN_PROGRESS");
    }

    public boolean completeService(int appointmentId, int barberId) {
        Appointment appt = requireById(appointmentId);
        enforceBarberOwnership(appt, barberId);
        return transition(appointmentId, "IN_PROGRESS", "COMPLETED");
    }

    public boolean adminUpdateStatus(int appointmentId, String newStatus) {
        return appointmentDAO.updateStatus(appointmentId, newStatus);
    }

    public List<Object[]> bookingsPerBarber() {
        return appointmentDAO.countBookingsPerBarber();
    }

    public List<Object[]> bookingsPerDay() {
        return appointmentDAO.countBookingsPerDay();
    }
    
    private Appointment requireById(int id) {
        return appointmentDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("Appointment not found for id=" + id));
    }
    
    private void enforceBarberOwnership(Appointment appt, int barberId) {
        if (appt.getBarberId() != barberId) {
            throw new SecurityException("You can only manage your own appointments.");
        }
    }

    private boolean transition(int appointmentId, String expectedCurrent, String newStatus) {
        Appointment appt = appointmentDAO.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        if (!expectedCurrent.equals(appt.getStatus())) {
            throw new IllegalStateException("Invalid transition: cannot move from " + appt.getStatus() + " to " + newStatus);
        }
        return appointmentDAO.updateStatus(appointmentId, newStatus);
    }
}