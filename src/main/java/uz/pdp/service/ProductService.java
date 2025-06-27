package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.model.Product;

import java.util.List;
import java.util.UUID;

public class ProductService implements BaseService<Product> {
    @Override
    public boolean add(Product product) {
        return false;
    }

    @Override
    public void update(UUID id, Product product) {

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
}
