package com.barbershop.app.service;

import com.barbershop.app.dao.AppointmentDAO;
import com.barbershop.app.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    /** Customer books an appointment. Enforces no double-booking per barber/date/time. */
    public Appointment bookAppointment(int customerId, int barberId, int serviceId,
                                        LocalDate date, LocalTime time) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book a date in the past.");
        }
        if (appointmentDAO.existsConflict(barberId, date, time)) {
            throw new IllegalStateException("This barber is already booked at the selected date/time.");
        }
        Appointment appt = new Appointment(0, customerId, barberId, serviceId, date, time, "PENDING");
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

    /** Customer cancels — allowed only if not already COMPLETED */
    public boolean cancelAppointment(int appointmentId) {
        Appointment appt = appointmentDAO.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        if ("COMPLETED".equals(appt.getStatus())) {
            throw new IllegalStateException("Cannot cancel a completed appointment.");
        }
        return appointmentDAO.updateStatus(appointmentId, "CANCELLED");
    }

    /** Admin/Barber confirms a pending appointment */
    public boolean confirmAppointment(int appointmentId) {
        return transition(appointmentId, "PENDING", "CONFIRMED");
    }
    
    public boolean deleteAppointment(int appointmentId) {
        return appointmentDAO.delete(appointmentId);
    }

    /** Barber starts service */
    public boolean startService(int appointmentId, int barberId) {
        Appointment appt = appointmentDAO.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        if (appt.getBarberId() != barberId) {
            throw new SecurityException("You can only manage your own appointments.");
        }
        return transition(appointmentId, "CONFIRMED", "IN_PROGRESS");
    }

    /** Barber completes service */
    public boolean completeService(int appointmentId, int barberId) {
        Appointment appt = appointmentDAO.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        if (appt.getBarberId() != barberId) {
            throw new SecurityException("You can only manage your own appointments.");
        }
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

    /** Enforces valid state transitions: PENDING → CONFIRMED → IN_PROGRESS → COMPLETED */
    private boolean transition(int appointmentId, String expectedCurrent, String newStatus) {
        Appointment appt = appointmentDAO.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        if (!expectedCurrent.equals(appt.getStatus())) {
            throw new IllegalStateException("Invalid transition: cannot move from " + appt.getStatus() + " to " + newStatus);
        }
        return appointmentDAO.updateStatus(appointmentId, newStatus);
    }
}