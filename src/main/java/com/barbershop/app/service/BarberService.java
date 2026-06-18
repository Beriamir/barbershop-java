package com.barbershop.app.service;

import com.barbershop.app.dao.BarberDAO;
import com.barbershop.app.dao.UserDAO;
import com.barbershop.app.model.Barber;
import com.barbershop.app.model.User;
import com.barbershop.app.util.PasswordUtil;

import java.util.List;

public class BarberService {

    private final BarberDAO barberDAO = new BarberDAO();
    private final UserDAO userDAO = new UserDAO();

    public List<Barber> getAll() {
        return barberDAO.findAll();
    }

    /** Creates a user (role=BARBER) + linked barbers row */
    public Barber createBarber(String name, String email, String rawPassword) {
        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }
        User user = userDAO.create(new User(0, name, email, PasswordUtil.hash(rawPassword), "BARBER"));
        return barberDAO.create(user.getId());
    }

    /** Updates the underlying user record (name/email) for a barber */
    public boolean updateBarber(Barber barber, String name, String email, String rawPassword) {
        User user = userDAO.findById(barber.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
        user.setName(name);
        user.setEmail(email);
    
        // Only re-hash and update password if a new one was provided
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(PasswordUtil.hash(rawPassword));
        }
    
        return userDAO.update(user);
    }

    /** Deletes barbers row + underlying user (cascades appointments) */
    public boolean deleteBarber(Barber barber) {
        barberDAO.delete(barber.getId());
        return userDAO.delete(barber.getUserId());
    }
}