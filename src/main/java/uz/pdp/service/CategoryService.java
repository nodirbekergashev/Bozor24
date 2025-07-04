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
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean isDefined(String name) {
        return categories.stream()
                .anyMatch(category -> category.getName().equalsIgnoreCase(name));
    }

    public List<Category> getChildCategories(UUID parentId) {
        return categories.stream()
                .filter(category -> category.getParentId().equals(parentId))
                .toList();
    }

    public Category getParentCategory(UUID childId) {
        return categories.stream()
                .filter(category -> category.getId().equals(childId))
                .findFirst()
                .orElse(null);
    }

    public void killSubcategories(UUID parentId) {
        while(true) {
            List<Category> childCategories = getChildCategories(parentId);
            if (childCategories.isEmpty()) {
                break;
            }
            for (Category child : childCategories) {
                child.setActive(false);
                killSubcategories(child.getId());
            }
        }
    }

    /**
     * kill subcategories
     * getCategoryByName
     * isDefined
     * getChildCategories
     * getParentCategory
     */
}
