package com.barbershop.app.dao;

import com.barbershop.app.model.Appointment;
import com.barbershop.app.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentDAO {

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    public Appointment create(Appointment a) {
        String sql = "INSERT INTO appointments (customer_id, barber_id, service_id, date, time, status, notes) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getCustomerId());
            ps.setInt(2, a.getBarberId());
            ps.setInt(3, a.getServiceId());
            ps.setDate(4, Date.valueOf(a.getDate()));
            ps.setTime(5, Time.valueOf(a.getTime()));
            ps.setString(6, a.getStatus());
            ps.setString(7, a.getNotes());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getInt(1));
            }
            return a;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating appointment", e);
        }
    }

    public Optional<Appointment> findById(int id) {
        String sql = baseJoinSql() + " WHERE a.id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding appointment", e);
        }
        return Optional.empty();
    }

    public List<Appointment> findByCustomer(int customerId) {
        String sql = baseJoinSql() + " WHERE a.customer_id = ? ORDER BY a.date DESC, a.time DESC";
        return queryList(sql, customerId);
    }

    public List<Appointment> findByBarber(int barberId) {
        String sql = baseJoinSql() + " WHERE a.barber_id = ? ORDER BY a.date, a.time";
        return queryList(sql, barberId);
    }

    public List<Appointment> findAll() {
        String sql = baseJoinSql() + " ORDER BY a.date DESC, a.time DESC";
        List<Appointment> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching appointments", e);
        }
        return list;
    }

    public boolean existsConflict(int barberId, LocalDate date, LocalTime time) {
        String sql = "SELECT COUNT(*) FROM appointments " +
                      "WHERE barber_id = ? AND date = ? AND time = ? AND status != 'CANCELLED'";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, barberId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(time));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking conflict", e);
        }
    }

    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating appointment status", e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting appointment", e);
        }
    }

    public List<Object[]> countBookingsPerBarber() {
        String sql = "SELECT u.name, COUNT(*) AS total FROM appointments a " +
                      "JOIN users u ON a.barber_id = u.id " +
                      "GROUP BY u.name ORDER BY total DESC";
        List<Object[]> result = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[]{ rs.getString("name"), rs.getInt("total") });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error computing stats", e);
        }
        return result;
    }

    public List<Object[]> countBookingsPerDay() {
        String sql = "SELECT date, COUNT(*) AS total FROM appointments " +
                      "GROUP BY date ORDER BY date DESC LIMIT 30";
        List<Object[]> result = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[]{ rs.getDate("date").toLocalDate(), rs.getInt("total") });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error computing stats", e);
        }
        return result;
    }

    private List<Appointment> queryList(String sql, int param) {
        List<Appointment> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching appointments", e);
        }
        return list;
    }

    private String baseJoinSql() {
        return "SELECT a.*, cu.name AS customer_name, bu.name AS barber_name, s.name AS service_name " +
               "FROM appointments a " +
               "JOIN users cu ON a.customer_id = cu.id " +
               "JOIN users bu ON a.barber_id = bu.id " +
               "JOIN services s ON a.service_id = s.id";
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment(
                rs.getInt("id"),
                rs.getInt("customer_id"),
                rs.getInt("barber_id"),
                rs.getInt("service_id"),
                rs.getDate("date").toLocalDate(),
                rs.getTime("time").toLocalTime(),
                rs.getString("status"),
                rs.getString("notes")
        );
        a.setCustomerName(rs.getString("customer_name"));
        a.setBarberName(rs.getString("barber_name"));
        a.setServiceName(rs.getString("service_name"));
        return a;
    }
}