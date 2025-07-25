package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.model.Product;

import java.util.List;
import java.util.UUID;

import static uz.pdp.db.Lists.PRODUCTS;
import static uz.pdp.utils.FileUtil.writeToJson;

public class ProductService implements BaseService<Product> {
    private static final String PATHNAME = "products.json";

    @Override
    public boolean add(Product product) {
        if (!isDefined(product.getSellerId(), product.getName())) {
            PRODUCTS.add(product);
            saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public void update(UUID id, Product product) {
        Product old = getById(id);
        if (old != null) {
            old.setName(product.getName());
            old.setPrice(product.getPrice());
            old.setSellerId(product.getSellerId());
            old.setQuantity(product.getQuantity());
            old.setUpdatedAt(product.getUpdatedAt());
            old.setActive(product.isActive());
            old.setCategoryId(product.getCategoryId());
            old.setCreatedAt(product.getCreatedAt());
            saveToFile();
        }
    }

    @Override
    public boolean delete(UUID id) {
        Product deletingProduct = getById(id);
        if (deletingProduct != null) {
            deletingProduct.setActive(false);
            return true;
        }
        return false;
    }

    @Override
    public Product getById(UUID id) {
        return PRODUCTS.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Product> getAll() {
        return PRODUCTS;
    }

    @Override
    public void saveToFile() {
        writeToJson(PATHNAME, PRODUCTS);
    }

    @Override
    public String getCreatedTimeById(UUID id) {
        return BaseService.super.getCreatedTimeById(id);
    }

    @Override
    public String getUpdatedTimeById(UUID id) {
        return BaseService.super.getUpdatedTimeById(id);
    }

    public List<Product> getByCategory(String categoryName) {
        return PRODUCTS.stream()
                .filter(product -> product.getName().equals(categoryName) && product.isActive())
                .toList();
    }

    public boolean isDefined(UUID sellerId, String productName) {
        return PRODUCTS.stream()
                .anyMatch(product -> product.getSellerId().equals(sellerId) &&
                        product.getName().equalsIgnoreCase(productName));
    }

    public List<Product> getProductBySellerId(UUID sellerId) {
        return PRODUCTS.stream()
                .filter(product -> product.getSellerId().equals(sellerId))
                .toList();
    }

    public List<Product> searchByProductName(String keyword) {
        String search = keyword.trim().toLowerCase();
        return PRODUCTS.stream()
                .filter(product -> product.isActive() && product.getName().toLowerCase().contains(search))
                .toList();
    }

    public Product getByProductName(String productName) {
        return PRODUCTS.stream()
                .filter(product -> product.isActive() && product.getName().equalsIgnoreCase(productName))
                .findFirst()
                .orElse(null);
    }


}
