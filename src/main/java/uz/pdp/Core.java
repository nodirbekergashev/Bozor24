package uz.pdp;

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
        return scannerInt.nextInt();
    }

    private static void register() {
        System.out.println("gdf");
        scannerStr.nextLine();
        mainDashboard();
    }

    private static void mainDashboard() {
        //todo write a method for check user role and work with switch-case like this

        if (false) {

            /// admins page
            //todo create menu for admin & write methods for admin like this
            // adminPage()

            /// Sellers page
            //todo create menu for seller & write methods for seller like this
            // sellerPage()
        } else {
            System.out.println("Password or username incorrect!");
        }
    }

    private static void login() {
        while (true) {
//
//
//
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
        // |
        // |-- 4 -> add to cart
        // |        |-- select product
        // |        |-- enter quantity
        // |
        // |-- 5 -> make order
        // |        |-- confirm cart
        // |
        // |-- 6 -> view my active orders
        // |        |-- just not delivered orders
        // |
        // |-- 7 -> view my order history
        // |        |-- show all orders
        // |
        // |-- 8 -> change password
        // |
        // |-- 9 -> logout() → back to printWelcomeMenu()
    }

    private static void printAdminMenu() {

    }

    private static void printSellerMenu() {

    }

    private static void printCustomerMenu() {

    }
}