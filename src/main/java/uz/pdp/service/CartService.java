package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.itemClasses.CartItem;
import uz.pdp.model.Cart;
import uz.pdp.model.Order;

import static uz.pdp.db.Lists.carts;

import java.util.List;
import java.util.UUID;

import static uz.pdp.utils.FileUtil.readFromJson;
import static uz.pdp.utils.FileUtil.writeToJson;

public class CartService implements BaseService<Cart> {
    private static final String pathName = "carts.json";

    public CartService() {
        carts = readFromJson(pathName, Cart.class);
    }

    @Override
    public boolean add(Cart cart) {
        boolean exists = carts.stream()
                .anyMatch(c -> c.getUserId().equals(cart.getUserId()));

        if (exists) {
            System.out.println("Cart for this customer already exists.");
            return false;
        }
        carts.add(cart);
        saveToFile();
        return true;
    }

    @Override
    public void update(UUID id, Cart cart) {
        Cart existingCart = getById(id);
        if (existingCart != null) {
            existingCart.setProducts(cart.getProducts());
            saveToFile();
        }
    }

    @Override
    public boolean delete(UUID id) {
        Cart c = getById(id);
        if (c != null) {
            c.setActive(false);
            saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public Cart getById(UUID id) {
        return carts.stream()
                .filter(cart -> cart.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Cart> getAll() {
        return carts;
    }

    @Override
    public void saveToFile() {
        try {
            writeToJson(pathName, carts);
        } catch (Exception e) {
            System.out.println("Error saving file" + e.getMessage());
        }
    }

    @Override
    public String getCreatedTimeById(UUID id) {
        return BaseService.super.getCreatedTimeById(id);
    }

    @Override
    public String getUpdatedTimeById(UUID id) {
        return BaseService.super.getUpdatedTimeById(id);
    }

    public Cart getCartByUserId(UUID userId) {
        return carts.stream()
                .filter(cart -> cart.getUserId().equals(userId) && cart.isActive())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart not found for user. "));
    }

    public boolean addItemToCart(UUID userId, CartItem item) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) return false;

        cart.getProducts().add(item);
        saveToFile();
        return true;
    }

    public boolean removeItemFromCart(UUID userId, UUID productId) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) return false;

        boolean removed = cart.getProducts().removeIf(item -> item.getProductId().equals(productId));
        if (removed) saveToFile();
        return true;
    }

    public boolean clearCart(UUID userId) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) return false;

        cart.getProducts().clear();
        saveToFile();
        return true;
    }
}


