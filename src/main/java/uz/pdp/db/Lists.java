package uz.pdp.db;

import uz.pdp.bot.botModel.BotUser;
import uz.pdp.model.*;

import java.util.List;

import static uz.pdp.utils.FileUtil.*;

public class Lists {
    public static final List<Cart> CARTS;
    public static final List<Category> CATEGORIES;
    public static final List<Order> ORDERS;
    public static final List<BotUser> BOT_USERS;
    public static final List<Product> PRODUCTS;
    public static final List<User> USERS;

    static {
        CATEGORIES = readFromXml("categories.xml", Category.class);
        PRODUCTS = readFromJson("products.json", Product.class);
        ORDERS = readFromJson("orders.json", Order.class);
        BOT_USERS = readFromXml("botRecurse/botUsers.xml", BotUser.class);
        CARTS = readFromJson("carts.json", Cart.class);
        USERS = readFromXml("users.xml", User.class);
    }
}
