package com.barbershop.app.dao;

import com.barbershop.app.model.ServiceItem;
import com.barbershop.app.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceDAO {

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    public ServiceItem create(ServiceItem item) {
        String sql = "INSERT INTO services (name, price, duration) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setBigDecimal(2, item.getPrice());
            ps.setInt(3, item.getDuration());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) item.setId(rs.getInt(1));
            }
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating service", e);
        }
    }

    public Optional<ServiceItem> findById(int id) {
        String sql = "SELECT * FROM services WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding service", e);
        }
        return Optional.empty();
    }

    public List<ServiceItem> findAll() {
        String sql = "SELECT * FROM services ORDER BY name";
        List<ServiceItem> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching services", e);
        }
        return list;
    }

    public List<ServiceItem> search(String keyword) {
        String sql = "SELECT * FROM services WHERE name LIKE ? ORDER BY name";
        List<ServiceItem> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching services", e);
        }
        return list;
    }

    public boolean update(ServiceItem item) {
        String sql = "UPDATE services SET name = ?, price = ?, duration = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setBigDecimal(2, item.getPrice());
            ps.setInt(3, item.getDuration());
            ps.setInt(4, item.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating service", e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM services WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting service", e);
        }
    }

    private ServiceItem mapRow(ResultSet rs) throws SQLException {
        return new ServiceItem(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getBigDecimal("price"),
                rs.getInt("duration")
        );
    }
}