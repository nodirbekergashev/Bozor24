package uz.pdp.bot.service;

import uz.pdp.bot.factory.inline.ProductInlineKeyboardFactory;
import uz.pdp.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uz.pdp.db.Lists.products;

public class ProductServiceBot {
    public List<Product> getProductsByCategoryId(UUID id) {
        return products.stream()
                .filter(product -> product.getCategoryId().equals(id))
                .toList();
    }

    public Product getProductById(UUID id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
