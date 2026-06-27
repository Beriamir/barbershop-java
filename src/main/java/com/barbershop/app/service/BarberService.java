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

    public Barber createBarber(String name, String email, String rawPassword) {
        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }
        User user = userDAO.create(new User(0, name, email, PasswordUtil.hash(rawPassword), "BARBER"));
        return barberDAO.create(user.getId());
    }

    public boolean updateBarber(Barber barber, String name, String email, String rawPassword) {
        User user = userDAO.findById(barber.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
        user.setName(name);
        user.setEmail(email);
    
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(PasswordUtil.hash(rawPassword));
        }
    
        return userDAO.update(user);
    }

    public boolean deleteBarber(Barber barber) {
        barberDAO.delete(barber.getId());
        return userDAO.delete(barber.getUserId());
    }
}