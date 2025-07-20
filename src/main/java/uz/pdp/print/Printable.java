package uz.pdp.print;

import uz.pdp.model.Category;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.service.CategoryService;

import java.util.List;

import static uz.pdp.model.Category.ROOT_CATEGORY_ID;

public class Printable {
    public static String printUsers(List<User> users) {
        StringBuilder sb = new StringBuilder();


        sb.append(String.format("%-20s %-15s %-10s %-25s %-25s%n",
                "Full Name", "Username", "Role", "Created At", "Updated At"));
        sb.append("=".repeat(95)).append(System.lineSeparator());

        users.stream()
                .filter(User::isActive)
                .forEach(user -> sb.append(String.format("%-20s %-15s %-10s %-25s %-25s%n",
                        user.getFullName(),
                        user.getUserName(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                )));

        return sb.toString();
    }

    public static String printCategories(List<Category> categories) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-36s %-20s %n", "Category Name", "Parent Category"));
        sb.append("=".repeat(52)).append(System.lineSeparator());
        CategoryService categoryService = new CategoryService();
        categories.stream()
                .filter(Category::isActive)
                .forEach(category -> sb.append(String.format("%-36s %-20s %n",
                        category.getName(),
                        category.getParentId().equals(ROOT_CATEGORY_ID) ? "ROOT"
                                : categoryService.getById(category.getParentId()).getName()
                )));
        return sb.toString();
    }

    public static String printProducts(List<Product> products) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-36s %-20s %-20s %n", "Product Name", "Price", "Count"));
        sb.append("=".repeat(40)).append(System.lineSeparator());
        products.stream()
                .filter(Product::isActive)
                .forEach(product -> sb.append(String.format("%-36s %-20s %-20s %n",product.getName(),product.getPrice(),product.getQuantity())));
        return sb.toString();
    }
}
