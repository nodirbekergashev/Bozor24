package uz.pdp.bot.service;

import uz.pdp.model.Category;

import java.util.List;
import java.util.UUID;

import static uz.pdp.db.Lists.CATEGORIES;

public class CategoryServiceBot {
    public List<Category> getAllCategories() {
        return CATEGORIES;
    }

    public List<Category> getCategoriesByLevel(UUID id) {
        return CATEGORIES.stream()
                .filter(Category::isActive)
                .filter(category -> category.getParentId().equals(id))
                .toList();
    }

    public Category getCategoryById(UUID id) {
        return CATEGORIES.stream()
                .filter(category -> category.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
