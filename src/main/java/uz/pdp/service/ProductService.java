package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        for (Product old : products) {
            if (old.getId().equals(id)) {

            }
        }
    }

    @Override
    public boolean delete(UUID id) {
        return false;
    }

    @Override
    public Product getById(UUID id) {
        return null;
    }

    @Override
    public List<Product> getAll() {
        return List.of();
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

    public boolean isDefined(UUID sellerId, String productName) {
        return products.stream()
                .anyMatch(product -> product.getSellerId().equals(sellerId) &&
                product.getName().equalsIgnoreCase(productName));
    }
}
