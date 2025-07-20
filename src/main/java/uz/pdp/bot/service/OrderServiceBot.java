package uz.pdp.bot.service;

import uz.pdp.itemClasses.CartItem;
import uz.pdp.itemClasses.OrderItem;
import uz.pdp.model.Cart;
import uz.pdp.model.Order;
import uz.pdp.model.Product;
import uz.pdp.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uz.pdp.db.Lists.ORDERS;
import static uz.pdp.enums.OrderStatus.PENDING;
import static uz.pdp.utils.FileUtil.writeToJson;

public class OrderServiceBot {
    ProductService service = new ProductService();

    public List<Order> getOrdersByUserId(UUID id) {
        return ORDERS.stream()
                .filter(order -> order.getUserId().equals(id))
                .toList();
    }

    public Order createOrderFromCart(UUID userId, Cart cart) {
        List<OrderItem> items = new ArrayList<>();
        OrderItem orderItem;
        double totalPrice = 0;
        String orderNum = String.valueOf(Math.random()).substring(3);
        Product product;
        for (CartItem item : cart.getProducts()) {
            product = service.getById(item.getProductId());
            totalPrice += product.getPrice();
            orderItem = new OrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setProductCount(item.getQuantity());
            items.add(orderItem);
        }
        ORDERS.add(new Order(userId, orderNum, items, totalPrice, PENDING));
        return new Order(userId, orderNum, items, totalPrice, PENDING);
    }

    public List<Order> getUserOrders(UUID id) {
        return ORDERS.stream()
                .filter(order -> order.getUserId().equals(id))
                .toList();
    }


    public void saveToFile() {
        writeToJson("orders.json", ORDERS);
    }
}
