package uz.pdp.bot.service;

import uz.pdp.model.Cart;

import java.util.*;

import static uz.pdp.db.Lists.CARTS;
import static uz.pdp.utils.FileUtil.writeToJson;

public class CartServiceBot {

    public Cart getCartByUserId(UUID userId) {
        if (userId != null) {
            return CARTS.stream().
                    filter(cart -> cart.getUserId().equals(userId))
                    .findFirst().orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUserId(userId);
                        newCart.setProducts(new ArrayList<>());

                        CARTS.add(newCart);
                        return newCart;
                    });
        }
        return null;
    }

    public Cart getCartById(UUID id) {
        return CARTS.stream()
                .filter(cart -> cart.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void clearCart(UUID cartId) {
        Cart cartById = getCartById(cartId);
        if (cartById != null) {
            cartById.setProducts(new ArrayList<>());
            saveToFile();

        }
    }

    public void saveCart(Cart cart) {
        if (cart != null && cart.getUserId() != null) {
            CARTS.removeIf(c -> c.getUserId().equals(cart.getUserId()));
            CARTS.add(cart);
            saveToFile();
        }
    }

    public void saveToFile() {
        writeToJson("carts.json", CARTS);
    }
}
