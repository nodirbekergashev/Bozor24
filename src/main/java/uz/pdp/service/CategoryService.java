package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.model.Category;
import uz.pdp.wrapperLists.CategoryListWrapper;

import java.util.List;
import java.util.UUID;

import static uz.pdp.db.Lists.categories;
import static uz.pdp.utils.FileUtil.readFromXml;
import static uz.pdp.utils.FileUtil.writeToXml;

public class CategoryService implements BaseService<Category> {
    private static final String pathName = "categories.xml";

    public CategoryService() {
        categories = readFromXml(pathName, Category.class);
    }

    @Override
    public boolean add(Category category) {
        if (category == null || isDefined(category.getName())) {
            return false;
        }
        categories.add(category);
        saveToFile();
        return true;
    }

    @Override
    public void update(UUID id, Category category) {

    }

    @Override
    public boolean delete(UUID id) {
        Category category = getById(id);
        if (category != null) {
            category.setActive(false);
            killSubcategories(category.getId());
            saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public Category getById(UUID id) {
        return categories.stream()
                .filter(category -> category.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Category> getAll() {
        return categories;
    }

    @Override
    public void saveToFile() {
        try {
            writeToXml(pathName, new CategoryListWrapper(categories));
        } catch (Exception e) {
            System.out.println("Error saving file " + e.getMessage());
        }

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

    public List<Category> getSubCategories(UUID parentId) {
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
        List<Category> childCategories = getSubCategories(parentId);
        if (childCategories.isEmpty()) {
            return;
        }
        for (Category child : childCategories) {
            child.setActive(false);
            killSubcategories(child.getId());
        }
    }

}
