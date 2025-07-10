package uz.pdp.db;

import uz.pdp.model.*;

import java.util.List;

import static uz.pdp.utils.FileUtil.*;

public class Lists {
    public static List<Cart> carts;
    public static List<Category> categories;
    public static List<Order> orders;
    public static List<Product> products;
    public static List<User> users;

    static {
        categories = readFromXml("categories.xml", Category.class);
        products = readFromJson("products.json", Product.class);
        orders = readFromJson("orders.json", Order.class);
        carts = readFromJson("carts.json", Cart.class);
        users = readFromXml("users.xml", User.class);
    }
//
//    static {
//        writeToJson("categories.xml",categories);
//        writeToJson("products.json",products);
//        writeToJson("orders.json",);
//        writeToXml("users.json");
//    }
}
