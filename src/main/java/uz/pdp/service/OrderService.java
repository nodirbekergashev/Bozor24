package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.enums.OrderStatus;
import uz.pdp.model.Order;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static uz.pdp.utils.FileUtil.writeToJson;
import static uz.pdp.db.Lists.ORDERS;

public class OrderService implements BaseService<Order> {
    private static final String pathName = "orders.json";


    @Override
    public boolean add(Order order) {
        if (order != null && !orderIsDefined(order.getUserId())) {
            ORDERS.add(order);
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
        return ORDERS.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Order> getAll() {
        return ORDERS;
    }

    @Override
    public void saveToFile() {
        try {
            writeToJson(pathName, ORDERS);
        } catch (Exception e) {
            System.out.println("File cannot find");
        }
    }

    public boolean orderIsDefined(UUID userId) {
        return ORDERS.stream()
                .anyMatch(order -> order.getUserId().equals(userId));
    }

    public Order getOrdersByUserId(UUID userId) {
        return ORDERS.stream()
                .filter(order -> order.getUserId().equals(userId))
                .findFirst().orElseThrow(RuntimeException::new);
    }

    public void changeOrderStatus(UUID userId, OrderStatus status) {
        Order ordersByUserId = getOrdersByUserId(userId);
        if (ordersByUserId == null) {
            return;
        }
        ordersByUserId.setStatus(status);
    }

//    public List<OrderItem> getActiveOrdersByUserId(UUID userId) {
//        Order ordersByUserId = getOrdersByUserId(userId);
//        if (ordersByUserId == null) {
//            return new ArrayList<>();
//        }
//        List<OrderItem> items = ordersByUserId.getOrdersByUser();
//
//        return items.stream()
//                .filter(orderItem -> orderItem.isActive() && orderItem.getStatus() != OrderStatus.CANCELED)
//                .toList();
//    }
//
//    public OrderItem getOrder(List<OrderItem> items, UUID itemId) {
//        return items.stream()
//                .filter(item -> item.getId().equals(itemId) && item
//                        .isActive() && item.getStatus() == OrderStatus.CANCELED)
//                .findFirst().orElse(null);
//    }
}
