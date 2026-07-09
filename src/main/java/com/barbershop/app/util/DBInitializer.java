package com.barbershop.app.util;

import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class DBInitializer {

    private static final String ADMIN_NAME = "admin";
    private static final String ADMIN_EMAIL = "admin@barbershop.com";
    private static final String ADMIN_PASSWORD = "admin";
    
    public static final String WALKIN_EMAIL = "walkin@barbershop.internal";
    public static final String WALKIN_NAME = "Walk-in Customer";

    public static void initialize() {
        try (Connection conn = DBConnection.getServerConnection();
             Statement stmt = conn.createStatement()) {

            String sql = readSchemaFile();

            String[] statements = sql.split(";");

            for (String rawStatement : statements) {
                String statement = stripComments(rawStatement).trim();
                if (!statement.isEmpty()) {
                    stmt.execute(statement);
                }
            }

            System.out.println("Database initialized successfully.");

        } catch (Exception e) {
            throw new RuntimeException("Database initialization failed", e);
        }

        seedAdmin();
        seedWalkInUser();
    }

    private static void seedAdmin() {
        String checkSql = "SELECT COUNT(*) FROM barbershop_db.users WHERE email = ?";
        String insertSql = "INSERT INTO barbershop_db.users (name, email, password, role) VALUES (?, ?, ?, 'ADMIN')";

        try ( Connection conn = DBConnection.getServerConnection()) {
            try ( PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, ADMIN_EMAIL);
                try ( ResultSet rs = check.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        System.out.println("Admin user already exists, skipping seed.");
                        return;
                    }
                }
            }

            String hashed = BCrypt.hashpw(ADMIN_PASSWORD, BCrypt.gensalt(10));

            try ( PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setString(1, ADMIN_NAME);
                insert.setString(2, ADMIN_EMAIL);
                insert.setString(3, hashed);
                insert.executeUpdate();
            }

            System.out.println("Admin user seeded: " + ADMIN_EMAIL + " / " + ADMIN_PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed admin user", e);
        }
    }
    
    private static void seedWalkInUser() {
        String check = "SELECT COUNT(*) FROM barbershop_db.users WHERE email = ?";
        String insert = "INSERT INTO barbershop_db.users (name, email, password, role) " + 
                "VALUES (?, ?, ?, 'WALKIN')";
        
        try (Connection conn = DBConnection.getServerConnection()) {
            if (emailExists(conn, check, WALKIN_EMAIL)) {
                System.out.println("Walk-in sentinel already seeded -- skipping.");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, WALKIN_NAME);
                ps.setString(2, WALKIN_EMAIL);
                ps.setString(3, BCrypt.hashpw(java.util.UUID.randomUUID().toString(), BCrypt.gensalt(10)));
                ps.executeUpdate();
            }
            System.out.println("Walk-in sentinel seeded -> " + WALKIN_EMAIL);
        } catch(Exception e) {
            throw new RuntimeException("Failed to seed walk-in user.", e);
        }
    }
    
    private static boolean emailExists(Connection conn, String sql, String email) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private static String readSchemaFile() throws Exception {
        InputStream is = DBInitializer.class.getResourceAsStream("/com/barbershop/app/schema.sql");
        if (is == null) {
            throw new RuntimeException("schema.sql not found in resources");
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private static String stripComments(String block) {
        StringBuilder sb = new StringBuilder();
        for (String line : block.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("--")) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}