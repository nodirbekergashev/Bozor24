package uz.pdp.yRunnableClasses;

import uz.pdp.enums.UserRole;
import uz.pdp.model.User;
import uz.pdp.service.*;


import java.util.Scanner;

public class Core {
    static UserService userService = new UserService();
    static CartService cartService = new CartService();
    static ProductService productService = new ProductService();
    static OrderService orderService = new OrderService();
    static User currentUser;

    static Scanner scannerStr = new Scanner(System.in);
    static Scanner scannerInt = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            int option = printWelcomeMenu();
            switch (option) {
                case 1 -> register();

                case 2 -> login();

                case 0 -> System.exit(13);

            }
        }
    }

    private static int printWelcomeMenu() {
        while (true) {
            System.out.println("""
                    
                    Welcome to Bozor24!
                    1. Register
                    2. Login
                    0. Exit
                    """);
            System.out.print("Please select an option: ");
            try {
                int option = scannerInt.nextInt();
                if (option < 0 || option > 2) {
                    System.out.println("Invalid option, please try again.");
                    continue;
                }
                return option;
            } catch (Exception e) {
                System.out.println("Invalid input, please enter a number.");
            }
        }
    }

    private static void register() {

        mainDashboard();
    }

    private static void mainDashboard() {
        System.out.println("Welcome to the main dashboard!");
        UserRole role = currentUser.getRole();

        if (role == UserRole.ADMIN) {
            /// admins page
            //todo create menu for admin & write methods for admin like this
            adminPage();

        } else if (role == UserRole.SELLER) {
            /// Sellers page
            //todo create menu for seller & write methods for seller like this
            sellerPage();
        } else {
            /// customers page
            //todo create menu for customer & write methods for customer like this
            customerPage();
        }
    }

    private static void login() {
        while (true) {
            currentUser = new User();
            if (userService == null) {
                System.out.println("Username or password is incorrect, please try again.");
                System.out.println("Or if you want to register, press 0");
                int option;
                try {
                    option = scannerInt.nextInt();
                } catch (Exception e) {
                    System.out.println("Invalid input, please enter a number.");
                    continue;
                }
                if (option == 0) {
                    return;
                }
                continue;
            }
            break;
        }
        mainDashboard();
    }

    private static void adminPage() {
        //todo
        // printAdminMenu()
        // |
        // |-- 1 -> manageUsers()
        // |        |-- view all users by role
        // |        |     |-- view admins()
        // |        |     |-- view sellers()
        // |        |     |-- view customers()
        // |        |-- delete user()
        // |        |-- change user role()
        // |
        // |-- 2 -> manageCategories()
        // |        |-- view category list()
        // |        |-- browse category (tree view)
        // |        |-- add category()
        // |        |     |-- choose parent category()
        // |        |-- update category ->>>>> do not write this because it's needn't
        // |        |-- delete category with child categories
        // |
        // |-- 3 -> manageProducts()
        // |        |-- view all products
        // |        |-- search by name
        // |        |-- show by category
        // |        |-- delete product
        // |
        // |-- 4 -> manageCarts()
        // |        |-- view all carts
        // |        |-- view cart by userId
        // |        |-- clear cart
        // |
        // |-- 5 -> searchGlobal()
        // |        |-- enter keyword
        // |        |-- show matches from:
        // |
        // |-- 6 -> changePassword(currentUser)
        // |
        // |-- 7 -> logout() → back to printWelcomeMenu()
        // |
        // |-- 0 -> exit program
    }

    private static void sellerPage() {
        //todo
        // printSellerMenu()
        // |-- 1 -> add product
        // |
        // |-- 2 -> view my products
        // |
        // |-- 3 -> update product
        // |
        // |-- 4 -> delete product
        // |
        // |-- 5 -> change password
        // |
        // |-- 0 -> logout() → back to printWelcomeMenu()
    }

    private static void customerPage() {
        //todo
        // printCustomerMenu()
        // |-- 1 -> browse categories
        // |
        // |-- 2 -> search products by name
        // |
        // |-- 3 -> view cart
        // |        |-- delete product from cart
        // |        |-- make order
        // |-- 4 -> view my active orders
        // |        |-- just not delivered orders
        // |
        // |-- 5 -> view my order history
        // |        |-- show all orders
        // |
        // |-- 6 -> change password
        // |
        // |-- 0 -> logout() → back to printWelcomeMenu()
    }

    private static void printAdminMenu() {
        System.out.println("""
                Admin Menu:
                1. Manage Users
                2. Manage Categories
                3. Manage Products
                4. Manage Carts
                5. Search Global
                6. Change Password
                7. Logout
                0. Exit
                """);
        System.out.print("Please select an option: ");
    }

    private static void printSellerMenu() {
        System.out.println("""
                Seller Menu:
                1. Add Product
                2. View My Products
                3. Update Product
                4. Delete Product
                5. Change Password
                0. Logout
                """);
        System.out.print("Please select an option: ");
    }

    private static void printCustomerMenu() {
        System.out.println("""
                Customer Menu:
                1. Browse Categories
                2. Search Products by Name
                3. View Cart
                4. View My Active Orders
                5. View My Order History
                6. Change Password
                0. Logout
                """);
        System.out.print("Please select an option: ");
    }
}