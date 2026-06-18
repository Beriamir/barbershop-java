package com.barbershop.app.model;

public class Barber {
    private int id;       // barbers.id
    private int userId;   // users.id
    private String name;  // joined from users.name
    private String email; // joined from users.email

    public Barber() {}

    public Barber(int id, int userId, String name, String email) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() { return name; }
}