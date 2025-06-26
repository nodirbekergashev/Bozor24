package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.model.Order;

import java.util.List;
import java.util.UUID;

public class OrderService implements BaseService<Order> {
    @Override
    public boolean add(Order order) {
        return false;
    }

    @Override
    public void update(UUID id, Order order) {

    }

    @Override
    public boolean delete(UUID id) {
        return false;
    }

    @Override
    public Order getById(UUID id) {
        return null;
    }

    @Override
    public List<Order> getAll() {
        return List.of();
    }

    @Override
    public void saveToFile() {

    }

    @Override
    public String getCreatedTimeById() {
        return BaseService.super.getCreatedTimeById();
    }

    @Override
    public String getUpdatedTimeById() {
        return BaseService.super.getUpdatedTimeById();
    }
}
