package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.model.Cart;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CartService implements BaseService<Cart> {
    private ArrayList<Cart> carts = new ArrayList<>();

    @Override
    public boolean add(Cart cart) {
        return false;
    }

    @Override
    public void update(UUID id, Cart cart) {

    }

    @Override
    public boolean delete(UUID id) {
        return false;
    }

    @Override
    public Cart getById(UUID id) {
        return null;
    }

    @Override
    public List<Cart> getAll() {
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
