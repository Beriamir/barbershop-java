package com.barbershop.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/barbershop_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "barbershop_password";

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to barbershop_db", e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reconnect", e);
        }
        return connection;
    }

    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
    }

    public static String getUser() { return USER; }
    public static String getPassword() { return PASSWORD; }
}