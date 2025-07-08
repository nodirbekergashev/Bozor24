package uz.pdp.bot.service;

import uz.pdp.db.Lists;
import uz.pdp.model.Cart;

import java.util.*;

import static uz.pdp.db.Lists.carts;
import static uz.pdp.utils.FileUtil.writeToJson;
import static uz.pdp.utils.FileUtil.writeToXml;

public class CartServiceBot {

    public Cart getCartByUserId(UUID userId) {
        if (userId !=null) {
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

    public void saveCart(Cart cart) {
        if (cart != null && cart.getUserId() != null) {
            carts.removeIf(c -> c.getUserId().equals(cart.getUserId()));
            carts.add(cart);
            System.out.println("✅ Cart saved: " + cart.getProducts().size() + " items");
            saveToFile();
        }
    }

    public void saveToFile() {
        writeToJson("carts.json", carts);
    }
}
