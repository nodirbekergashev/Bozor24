package uz.pdp.yRunnableClasses;

import uz.pdp.enums.UserRole;
import uz.pdp.model.Cart;
import uz.pdp.model.Category;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;
import static java.lang.Thread.sleep;

public class Core {
    private static final Logger log = LoggerFactory.getLogger(Core.class);
    static UserService userService = new UserService();
    static CartService cartService = new CartService();
    static ProductService productService = new ProductService();
    static CategoryService categoryService = new CategoryService();
    static final String INVALID_INPUT_ERROR = "Invalid input, please enter a number.";
    static User currentUser;

    static Scanner scannerStr = new Scanner(System.in);
    static Scanner scannerInt = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            int option = printWelcomeMenu();
            switch (option) {
                case 1 -> register();
                case 2 -> login();
                case 0 -> exit(13);
            }
        }
    }

    /// ////////////////////// WELCOME MENU //////////////////////////////////

    private static int printWelcomeMenu() {
        while (true) {
            System.out.println("""
                    
                    1. Register
                    2. Login
                    0. Exit
                    """);
            System.out.println(("Loading ...\n"));
            try {
                sleep(3000);
            } catch (InterruptedException e) {
               log.error("error",e);
            }
            System.out.print("Please select an option: ");
            try {
                int option = scannerInt.nextInt();
                if (option < 0 || option > 2) {
                    log.info("Invalid option, please try again.");
                    continue;
                }
                return option;
            } catch (Exception e) {
                System.out.println((INVALID_INPUT_ERROR));
            }
        }
    }

    /// ///////////////////// REGISTER  /////////////////////////

    private static void register() {
        System.out.print("Enter your full name: ");
        String fullName = scannerStr.nextLine();
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = scannerStr.nextLine();
            if (userService.isUsernameValid(username)) {
                break;
            } else {
                log.info("Invalid username. Try again!");
                log.info("Username should be contains lowercase english letters and '_' ");
            }
        }
        System.out.print("Enter password: ");
        String password = scannerStr.nextLine();
        UserRole roleOption;
        while (true) {
            log.info("""
                    Select your role
                    1. Customer
                    2. Seller""");
            try {
                int option = scannerInt.nextInt();
                if (option > 2 || option < 0) {
                    log.info("Enter 1 or 2");
                    continue;
                }
                switch (option) {
                    case 1 -> roleOption = UserRole.CUSTOMER;
                    case 2 -> roleOption = UserRole.SELLER;
                    default -> {
                        continue;
                    }
                }
                break;
            } catch (InputMismatchException e) {
                log.info("Enter a valid number!");
            }
        }
        userService.add(new User(fullName, username, password, roleOption));
        currentUser = userService.login(username, password);
        log.info("Registration successful! Welcome, {} !\n", currentUser.getFullName());
        mainDashboard();
    }

    /// ////////////////////////// MAIN DASHBOARD ////////////////////////////

    private static void mainDashboard() {
        log.info("Welcome to the main dashboard!");
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

    /// //////////////////// LOGIN //////////////////////////

    private static void login() {
        log.info("Enter username: ");
        String username = scannerStr.nextLine();
        System.out.print("Enter password: ");
        String password = scannerStr.nextLine();
        currentUser = userService.login(username, password);
        if (currentUser == null) {
            log.info("Username or password is incorrect, please try again.");
            return;
        }
        mainDashboard();
    }

    /// ////////////////// ADMIN PAGE //////////////////////////////

    private static void adminPage() {
        while (true) {
            printAdminMenu();
            int option;
            try {
                option = scannerInt.nextInt();
                scannerStr.nextLine();
            } catch (InputMismatchException e) {
                log.info("Invalid input, please enter a number. ");
                scannerStr.nextLine();
                continue;
            }
            switch (option) {
                case 1 -> manageUsers();
                case 2 -> manageCategories();
                case 3 -> manageProducts();
                case 4 -> manageCarts();
                case 5 -> searchGlobal();
                case 6 -> changePassword();
                case 7 -> {
                    logout();
                    return;
                }
                case 0 -> exit(0);
                default -> log.info("Invalid option. Try again.");
            }
        }


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

    private static void logout() {
        currentUser = null;
        log.info("Logged out successfully.");
    }

    private static void changePassword() {
        log.info("Enter current password: ");
        String oldPassword = scannerStr.nextLine();
        if (!currentUser.getPassword().equals(oldPassword)) {
            log.info("Incorrect current password! ");
            return;
        }
        log.info("Enter new password: ");
        String newPassword = scannerStr.nextLine();
        userService.changePassword(currentUser, newPassword);
        log.info("Password changed successfully! ");
    }

    private static void searchGlobal() {
    }

    private static void manageCarts() {
        while (true) {
            log.info("""
                    Manage Carts
                    1. View all carts
                    2. View user's cart
                    3. Clear user's cart
                    0. Back
                    """);
            log.info("Choose option: ");
            int option;
            try {
                option = scannerInt.nextInt();
                scannerStr.nextLine();
            } catch (InputMismatchException e) {
                log.info(INVALID_INPUT_ERROR);
                scannerStr.nextLine();
                continue;
            }
            switch (option) {
                case 1 -> {
                    List<Cart> carts = cartService.getAll();
                    for (Cart cart : carts) {
                        System.out.println(cart);
                    }
                }
                case 2 -> {
                    log.info("Enter username: ");
                    String username = scannerStr.nextLine();
                    User user = userService.getUserByUsername(username);
                    Cart cart = cartService.getCartByUserId(user.getId());
                    if (cart == null) {
                        log.info("Cart not found.");
                    } else {
                        System.out.println(cart);
                    }
                }
                case 3 -> {
                    log.info("Enter username: ");
                    String username = scannerStr.nextLine();
                    User user = userService.getUserByUsername(username);
                    if (cartService.clearCart(user.getId())) {
                        log.info("Cart cleared.");
                    } else {
                        log.info("Cart not found.");
                    }
                }
                case 0 -> {
                    return;
                }
                default -> log.info("Invalid option.Try again.");
            }
        }
    }

    private static void manageProducts() {
        while (true) {
            log.info("""
                    Manage Products
                    1. View all products
                    2. Search product
                    3. Show products by category
                    4. Delete product
                    0. Back
                    """);
            log.info("Choose option: ");
            int option;
            try {
                option = scannerInt.nextInt();
                scannerStr.nextLine();
            } catch (InputMismatchException e) {
                System.out.print(INVALID_INPUT_ERROR);
                scannerStr.nextLine();
                continue;
            }
            switch (option) {
                case 1 -> {
                    List<Product> products = productService.getAll();
                    for (Product product : products) {
                        System.out.println(product);
                    }
                }
                case 2 -> {
                    log.info("Enter product name: ");
                    String productName = scannerStr.nextLine();
                    List<Product> products = productService.searchByProductName(productName);
                    for (Product product : products) {
                        System.out.println(product);
                    }
                }
                case 3 -> {
                    log.info("Enter category name: ");
                    String productCategory = scannerStr.nextLine();
                    List<Product> products = productService.getByCategory(productCategory);
                    for (Product product : products) {
                        System.out.println(product);
                    }
                }
                case 4 -> {
                    log.info("Enter product to delete: ");
                    String deleteProduct = scannerStr.nextLine();
                    if (userService.delete(userService.getUserByUsername(deleteProduct).getId())) {
                        log.info("Product deleted.");
                    } else {
                        log.info("Product not found");
                    }
                }
                case 0 -> {
                    return;
                }
                default -> log.info("Invalid option. Try again.");
            }
        }
    }

    private static void manageCategories() {
        while (true) {
            log.info("""
                    Manage Categories:
                    1. View category list
                    2. Browse category
                    3. Add category
                    4. Delete category
                    0. Back
                    """);
            log.info("Choose option: ");
            int option;
            try {
                option = scannerInt.nextInt();
                scannerStr.nextLine();
            } catch (InputMismatchException e) {
                log.info("Invalid input, please enter a number.");
                scannerStr.nextLine();
                continue;
            }
            switch (option) {
                case 1 -> {
                    List<Category> categories = categoryService.getAll();
                    for (Category c : categories) {
                        log.info(c.getName());
                    }
                }
                case 2 -> {
                    // todo something
                }
                case 3 -> {
                    // todo something in here
                }
                case 4 -> {
                    // todo
                }
                case 0 -> {
                    return;
                }
                default -> log.info("Wrong option");
            }
        }
    }

    private static void manageUsers() {
        while (true) {
            log.info("""
                    Manage Users
                    1. View all users by role
                    2. Delete user
                    3. Change user role
                    0. Back
                    
                    """);
            System.out.print("Choose option: ");
            int adminChoice;
            try {
                adminChoice = scannerInt.nextInt();
                scannerStr.nextLine();
            } catch (InputMismatchException e) {
                log.error("Invalid input, please enter a number.");
                scannerStr.nextLine();
                continue;
            }
            switch (adminChoice) {
                case 1 -> viewUsersByRole();

                case 2 -> deleteUser();

                case 3 -> changeUserRole();

                case 0 -> {
                    return;
                }

                default -> log.info("Invalid option try again.");
            }
        }
    }

    private static void changeUserRole() {
        while (true) {
            log.info("Enter user to change role: ");
            String username = scannerStr.nextLine();
            User user = userService.getUserByUsername(username);
            if (user != null) {
                log.info("""
                        Select new role:
                        1. ADMIN
                        2. SELLER
                        3. COSTUMER""");
                int newRole;
                try {
                    newRole = scannerInt.nextInt();
                    scannerStr.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println(INVALID_INPUT_ERROR);
                    scannerStr.nextLine();
                    continue;
                }
                UserRole role = switch (newRole) {
                    case 1 -> UserRole.ADMIN;
                    case 2 -> UserRole.SELLER;
                    case 3 -> UserRole.CUSTOMER;
                    default -> null;
                };
                if (role != null) {
                    userService.changeUserRole(user.getId(), role);
                    log.info("Role updated successfully.");
                    return;
                } else {
                    log.error("Invalid, role selected.");
                }
            } else {
                log.info("User not found.");
                return;
            }
        }
    }

    private static void deleteUser() {
        while (true) {
            log.info("Enter username to delete: ");
            String username = scannerStr.nextLine();
            if (username.equals("admin")) {
                log.info("You cannot delete the admin user.");
                continue;
            }
            if (userService.delete(userService.getUserByUsername(username).getId())) {
                log.info("User deleted successfully.");
                return;
            } else {
                log.error("User not found.");
            }
        }

    }

    private static void viewUsersByRole() {
        log.info("""
                1. View Admins
                2. View Sellers
                3. View Costumers
                0. Back""");
        log.info("Choose option: ");
        int choice;
        try {
            choice = scannerInt.nextInt();
            scannerStr.nextLine();
        } catch (InputMismatchException e) {
            System.out.println(INVALID_INPUT_ERROR);
            scannerStr.nextLine();
            return;
        }
        UserRole role = switch (choice) {
            case 1 -> UserRole.ADMIN;
            case 2 -> UserRole.SELLER;
            case 3 -> UserRole.CUSTOMER;
            default -> null;
        };
        if (role == null && choice != 0) {
            log.info("Invalid option.");
            return;
        }
        if (choice == 0) return;
        List<User> users = userService.getByRole(role);
        if (users.isEmpty()) {
            log.info("No users found for role: {}", role);
        } else {
            log.info("Users with role: {}:", role);
            for (User user : users) {
                log.info("- name: {}, username: {}", user.getFullName(), user.getUserName());
            }
        }
    }

    /// //////////////////////// SELLER PAGE ////////////////////////

    private static void sellerPage() {
        printSellerMenu();
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

    /// ///////////////// CUSTOMER PAGE //////////////////

    private static void customerPage() {
        printCustomerMenu();
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

    /// ////////////////// PRINT MENUS ////////////////////////////

    private static void printAdminMenu() {
        log.info("""
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
        log.info("""
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
        log.info("""
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