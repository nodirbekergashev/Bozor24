package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;

import java.util.List;
import java.util.UUID;
import static uz.pdp.db.Lists.categories;

public class CategoryService implements BaseService {
    @Override
    public boolean add(Object o) {
        return false;
    }

    @Override
    public void update(UUID id, Object o) {

    }

    @Override
    public boolean delete(UUID id) {
        return false;
    }

    @Override
    public Object getById(UUID id) {
        return null;
    }

    @Override
    public List getAll() {
        return List.of();
    }

    @Override
    public void saveToFile() {

    }
}
