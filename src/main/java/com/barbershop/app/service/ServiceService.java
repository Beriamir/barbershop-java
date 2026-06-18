package com.barbershop.app.service;

import com.barbershop.app.dao.ServiceDAO;
import com.barbershop.app.model.ServiceItem;

import java.math.BigDecimal;
import java.util.List;

public class ServiceService {

    private final ServiceDAO serviceDAO = new ServiceDAO();

    public List<ServiceItem> getAll() {
        return serviceDAO.findAll();
    }

    public List<ServiceItem> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAll();
        return serviceDAO.search(keyword);
    }

    public ServiceItem create(String name, BigDecimal price, int duration) {
        validate(name, price, duration);
        return serviceDAO.create(new ServiceItem(0, name, price, duration));
    }

    public boolean update(ServiceItem item) {
        validate(item.getName(), item.getPrice(), item.getDuration());
        return serviceDAO.update(item);
    }

    public boolean delete(int id) {
        return serviceDAO.delete(id);
    }

    private void validate(String name, BigDecimal price, int duration) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Service name required.");
        if (price == null || price.signum() < 0) throw new IllegalArgumentException("Price must be >= 0.");
        if (duration <= 0) throw new IllegalArgumentException("Duration must be > 0.");
    }
}