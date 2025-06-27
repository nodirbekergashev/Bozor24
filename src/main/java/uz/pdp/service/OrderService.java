package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.enums.OrderStatus;
import uz.pdp.itemClasses.OrderItem;
import uz.pdp.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderService implements BaseService<Order> {
    private static List<Order> orders;
    private static final String pathName = "orders.json";

//    public OrderService() {
//        orders = readFromJsonFile(pathName, Order.class);
//    }

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

    public boolean orderIsDefined(UUID userId) {
        return orders.stream()
                .anyMatch(order -> order.getUserId().equals(userId));
    }

    public Order getOrdersByUserId(UUID userId) {
        return orders.stream()
                .filter(order -> order.getUserId().equals(userId))
                .findFirst().orElseThrow(RuntimeException::new);
    }

    public void changeOrderStatus(UUID userId, UUID orderItemId, OrderStatus status) {
        Order ordersByUserId = getOrdersByUserId(userId);
        if (ordersByUserId == null) {
            return;
        }
        List<OrderItem> items = ordersByUserId.getOrdersByUser();

        items.stream()
                .filter(orderItem -> orderItem.getId().equals(orderItemId))
                .findFirst().ifPresent(item -> item.setStatus(status));
    }
    public List<OrderItem> getActiveOrdersByUserId(UUID userId) {
        Order ordersByUserId = getOrdersByUserId(userId);
        if (ordersByUserId == null) {
            return new ArrayList<>();
        }
        List<OrderItem> items = ordersByUserId.getOrdersByUser();

        return items.stream()
                .filter(orderItem -> orderItem.isActive() && orderItem.getStatus() != OrderStatus.CANCELED)
                .toList();
    }
}
