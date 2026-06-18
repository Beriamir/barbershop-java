package com.barbershop.app.dao;

import com.barbershop.app.model.Barber;
import com.barbershop.app.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BarberDAO {

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    /** Creates a barbers row linking to an existing users row (role=BARBER) */
    public Barber create(int userId) {
        String sql = "INSERT INTO barbers (user_id) VALUES (?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            int barberId;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                barberId = rs.getInt(1);
            }
            return findById(barberId).orElseThrow();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating barber", e);
        }
    }

    public Optional<Barber> findById(int barberId) {
        String sql = "SELECT b.id, b.user_id, u.name, u.email FROM barbers b " +
                      "JOIN users u ON b.user_id = u.id WHERE b.id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, barberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding barber", e);
        }
        return Optional.empty();
    }

    public Optional<Barber> findByUserId(int userId) {
        String sql = "SELECT b.id, b.user_id, u.name, u.email FROM barbers b " +
                      "JOIN users u ON b.user_id = u.id WHERE b.user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding barber by user", e);
        }
        return Optional.empty();
    }

    public List<Barber> findAll() {
        String sql = "SELECT b.id, b.user_id, u.name, u.email FROM barbers b " +
                      "JOIN users u ON b.user_id = u.id ORDER BY u.name";
        List<Barber> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching barbers", e);
        }
        return list;
    }

    public boolean delete(int barberId) {
        String sql = "DELETE FROM barbers WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, barberId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting barber", e);
        }
    }

    private Barber mapRow(ResultSet rs) throws SQLException {
        return new Barber(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email")
        );
    }
}