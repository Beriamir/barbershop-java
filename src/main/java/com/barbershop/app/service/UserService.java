package com.barbershop.app.service;

import com.barbershop.app.dao.UserDAO;
import com.barbershop.app.model.User;
import com.barbershop.app.util.PasswordUtil;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User register(String name, String email, String rawPassword, String role) {
        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (name == null || name.isBlank() || email == null || email.isBlank() || rawPassword == null || rawPassword.length() < 4) {
            throw new IllegalArgumentException("Invalid registration details.");
        }
        String hashed = PasswordUtil.hash(rawPassword);
        return userDAO.create(new User(0, name, email, hashed, role));
    }

    public Optional<User> login(String email, String rawPassword) {
        return userDAO.findByEmail(email)
                .filter(u -> PasswordUtil.verify(rawPassword, u.getPassword()));
    }

    public List<User> getAllByRole(String role) {
        return userDAO.findByRole(role);
    }

    public Optional<User> getById(int id) {
        return userDAO.findById(id);
    }

    public boolean update(User user) {
        return userDAO.update(user);
    }

    public boolean delete(int id) {
        return userDAO.delete(id);
    }
}