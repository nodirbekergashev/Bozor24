package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.model.User;

import java.util.List;
import java.util.UUID;

public class UserService implements BaseService<User> {
    @Override
    public boolean add(User user) {
        return false;
    }

    @Override
    public void update(UUID id, User user) {

    }

    @Override
    public boolean delete(UUID id) {
        return false;
    }

    @Override
    public User getById(UUID id) {
        return null;
    }

    @Override
    public List<User> getAll() {
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
