package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductService implements BaseService<Product> {
    private List<Product> products = new ArrayList<>();
    @Override
    public boolean add(Product product) {
        if (!isDefined(product.getSellerId(), product.getName())) {
            products.add(product);
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
            old.setCatId(product.getCatId());
            old.setCreatedAt(product.getCreatedAt());
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
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Product> getAll() {
        return products;
    }

    @Override
    public void saveToFile() {

    }

    @Override
    public String getCreatedTimeById() {
        return BaseService.super.getCreatedTimeById();
    }

    @Override
    public String getUpdatedTimeById() {
        return BaseService.super.getUpdatedTimeById();
    }

    public List<Product> getByCategory(String categoryName) {
        return products.stream()
                .filter(product -> product.getName().equals(categoryName) && product.isActive())
                .collect(Collectors.toList());
    }

    public boolean isDefined(UUID sellerId, String productName) {
        return products.stream()
                .anyMatch(product -> product.getSellerId().equals(sellerId) &&
                product.getName().equalsIgnoreCase(productName));
    }

    public List<Product> getProductBySellerId(UUID sellerId) {
        return products.stream()
                .filter(product -> product.getSellerId().equals(sellerId))
                .collect(Collectors.toList());
    }
}
