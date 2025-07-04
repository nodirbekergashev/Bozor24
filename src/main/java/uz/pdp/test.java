package uz.pdp;

import uz.pdp.model.Category;
import uz.pdp.service.CategoryService;

import java.util.UUID;

import static uz.pdp.model.Category.ROOT_CATEGORY_ID;

public class test {
    public static void main(String[] args) {
        UUID id  = null;
        System.out.println(id);
        CategoryService categoryService = new CategoryService();
        categoryService.add(new Category("Electronics", ROOT_CATEGORY_ID));
        categoryService.add(new Category("Transport", ROOT_CATEGORY_ID));
        categoryService.add(new Category("Sport", ROOT_CATEGORY_ID));

    }
}
