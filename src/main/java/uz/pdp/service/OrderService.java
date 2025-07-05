package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.enums.OrderStatus;
import uz.pdp.itemClasses.OrderItem;
import uz.pdp.model.Order;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static uz.pdp.utils.FileUtil.readFromJson;
import static uz.pdp.utils.FileUtil.writeToJson;
import static uz.pdp.db.Lists.orders;

public class OrderService implements BaseService<Order> {
    private static final String pathName = "orders.json";

    public OrderService() {
        orders = readFromJson(pathName, Order.class);
    }

    @Override
    public boolean add(Order order) {
        if (order != null && !orderIsDefined(order.getUserId())) {
            orders.add(order);
            saveToFile();
        }
        return false;
    }

    @Override
    public void update(UUID id, Order order) {
        Order old = getById(id);
        if (old != null) {
            old.setActive(order.isActive());
            old.setUpdatedAt(new Date());
            old.setOrdersByUser(order.getOrdersByUser());
            saveToFile();
        }
    }

    @Override
    public boolean delete(UUID id) {
        return false;
    }

    @Override
    public Order getById(UUID id) {
        return orders.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Order> getAll() {
        return orders;
    }

    @Override
    public void saveToFile() {
        try {
            writeToJson(pathName, orders);
        } catch (Exception e) {
            System.out.println("File cannot find");
        }
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

    public OrderItem getOrder(List<OrderItem> items, UUID itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId) && item
                        .isActive() && item.getStatus() == OrderStatus.CANCELED)
                .findFirst().orElse(null);
    }
}
