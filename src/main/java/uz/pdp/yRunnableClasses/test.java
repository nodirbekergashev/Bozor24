package uz.pdp.yRunnableClasses;

import uz.pdp.model.Category;
import uz.pdp.service.CategoryService;

import java.util.List;
import java.util.UUID;

import static uz.pdp.print.Printable.printCategories;

public class test {
    public static void main(String[] args) {
        UUID id = null;
        System.out.println(id);
        CategoryService categoryService = new CategoryService();
        categoryService.add(new Category("Lap-Tops", UUID.fromString("2ee9f291-d167-4062-bd53-3a87e32b617c")));
        categoryService.add(new Category("Trucks", UUID.fromString("0f013bde-adb9-4f8f-bfc1-799a13a5f157")));
        categoryService.add(new Category("Balls", UUID.fromString("40b9a47a-725e-4f29-a201-59d62aba36df")));

        List<Category> all = categoryService.getAll();

        System.out.println(printCategories(all));
    }
}
