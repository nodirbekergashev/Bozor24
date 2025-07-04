package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.model.Category;

import java.util.List;
import java.util.UUID;
import static uz.pdp.db.Lists.categories;

public class CategoryService implements BaseService<Category> {

    @Override
    public boolean add(Category category) {
        return false;
    }

    @Override
    public void update(UUID id, Category category) {

    }

    @Override
    public boolean delete(UUID id) {
        return false;
    }

    @Override
    public Category getById(UUID id) {
        return null;
    }

    @Override
    public List<Category> getAll() {
        return List.of();
    }

    @Override
    public void saveToFile() {

    }

    public Category getCategoryByName(String name) {
        return categories.stream()
                .filter(category -> category.get().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    /**
     * getCategoryByName
     * isDefined
     * getChildCategories
     * getParentCategory
     */
}
