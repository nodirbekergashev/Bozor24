package uz.pdp.bot.service;

import uz.pdp.db.Lists;
import uz.pdp.model.Cart;

import java.util.*;

import static uz.pdp.db.Lists.carts;
import static uz.pdp.utils.FileUtil.writeToJson;

public class CartServiceBot {

    public Cart getCartByUserId(UUID userId) {
        if (userId != null) {
            if (carts == null) {
                carts = new ArrayList<>();
            }

            for (Cart cart : carts) {
                if (cart.getUserId() != null && cart.getUserId().equals(userId)) {
                    return cart;
                }
            }

            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setProducts(new ArrayList<>());

            carts.add(newCart);

            return newCart;
        }
        return null;
    }

    public Cart getCartById(UUID id) {
        return carts.stream()
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
            carts.removeIf(c -> c.getUserId().equals(cart.getUserId()));
            carts.add(cart);
            saveToFile();
        }
    }

    public void saveToFile() {
        writeToJson("carts.json", carts);
    }
}
