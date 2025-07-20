package uz.pdp.bot.service;

import uz.pdp.model.Product;

import java.util.List;
import java.util.UUID;

import static uz.pdp.db.Lists.PRODUCTS;
import static uz.pdp.utils.FileUtil.writeToJson;

public class ProductServiceBot {
    public List<Product> getProductsByCategoryId(UUID id) {
        return PRODUCTS.stream()
                .filter(product -> product.getCategoryId().equals(id))
                .toList();
    }

    public Product getProductById(UUID id) {
        return PRODUCTS.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void editProductCount(UUID productId, int quantity){
        Product editedProduct = getProductById(productId);
        editedProduct.setQuantity(editedProduct.getQuantity() - quantity);
        saveToFile();
    }

    private void saveToFile() {
        writeToJson("products.json", PRODUCTS);
    }
}
