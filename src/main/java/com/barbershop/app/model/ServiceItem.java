package com.barbershop.app.model;

import java.math.BigDecimal;

public class ServiceItem {
    private int id;
    private String name;
    private BigDecimal price;
    private int duration; // minutes

    public ServiceItem() {}

    public ServiceItem(int id, String name, BigDecimal price, int duration) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.duration = duration;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    @Override
    public String toString() {
        return name + " - PHP" + price + " (" + duration + " min)";
    }
}