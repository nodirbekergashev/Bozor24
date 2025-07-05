package uz.pdp.yRunnableClasses;

import uz.pdp.enums.UserRole;
import uz.pdp.model.Cart;
import uz.pdp.model.Category;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.service.*;


import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;
import static java.lang.Thread.sleep;

public class Core {
    static UserService userService = new UserService();
    static CartService cartService = new CartService();
    static ProductService productService = new ProductService();
    static OrderService orderService = new OrderService();
    static CategoryService categoryService = new CategoryService();
    static User currentUser;

    static Scanner scannerStr = new Scanner(System.in);
    static Scanner scannerInt = new Scanner(System.in);

    public static void main(String[] args) {
        User admin = new User("Atabek M", "ata", "123", UserRole.ADMIN);
        userService.add(admin);

        while (true) {
            int option = printWelcomeMenu();
            switch (option) {
                case 1 -> register();

                case 2 -> login();

                case 0 -> exit(13);

            }
        }
    }
///////////////////////// WELCOME MENU //////////////////////////////////

    private static int printWelcomeMenu() {
        while (true) {
            System.out.println("""
                    
                    Welcome to Bozor24!
                    1. Register
                    2. Login
                    0. Exit
                    """);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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

//////////////////////// REGISTER  /////////////////////////

    private static void register() {
        System.out.println("Enter your full name: ");
        String fullName = scannerStr.nextLine();
        String username;
        while (true) {
            System.out.println("Enter username: ");
            username = scannerStr.nextLine();
            if (userService.isUsernameValid(username)) {
                break;
            } else {
                System.out.println("Invalid username. Try again!");
                System.out.println("Username should be contains lowercase english letters and '_' ");
            }
        }
        System.out.println("Enter password: ");
        String password = scannerStr.nextLine();
        UserRole roleOption = UserRole.CUSTOMER;
        while (true) {
            System.out.println("""
                    Select your role
                    1. Customer
                    2. Seller""");
            try {
                int option = scannerInt.nextInt();
                if (option > 2 || option < 0) {
                    System.out.println("Enter 1 or 2");
                    continue;
                }
                switch (option) {
                    case 1 -> roleOption = UserRole.CUSTOMER;
                    case 2 -> roleOption = UserRole.SELLER;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Enter a valid number!");
            }
        }
        userService.add(new User(fullName, username, password, roleOption));
        currentUser = userService.login(username, password);
        mainDashboard();
    }
///////////////////////////// MAIN DASHBOARD ////////////////////////////

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

    /////////////////////// LOGIN //////////////////////////

    private static void login() {
        System.out.println("Enter username: ");
        String username = scannerStr.nextLine();
        System.out.println("Enter password: ");
        String password = scannerStr.nextLine();
        currentUser = userService.login(username, password);
        if (currentUser == null) {
            System.out.println("Username or password is incorrect, please try again.");
            return;
        }
        mainDashboard();
    }

    ///////////////////// ADMIN PAGE //////////////////////////////

    private static void adminPage() {
        while (true) {
            printAdminMenu();
            int option;
            try {
                option = scannerInt.nextInt();
                scannerStr.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a number. ");
                scannerStr.nextLine();
                continue;
            }
            System.out.println(""" 
                    Choose option: 
                    """);

            switch (option) {
                case 1 -> manageUsers();
                case 2 -> manageCategories();
                case 3 -> manageProducts();
                case 4 -> manageCarts();
                case 5 -> searchGlobal();
                case 6 -> changePassword(currentUser);
                case 7 -> {
                    logout();
                    return;
                }
                case 0 -> exit(0);
                default -> System.out.println("Invalid option. Try again.");

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
        System.out.println("Logged out successfully.");
    }

    private static void changePassword(User user) {
        System.out.println("Enter current password: ");
        String oldPassword = scannerStr.nextLine();
        if (!user.getPassword().equals(oldPassword)) {
            System.out.println("Incorrect current password! ");
            return;
        }
        System.out.println("Enter new password: ");
        String newPassword = scannerStr.nextLine();
        userService.changePassword(user,newPassword);
        System.out.println("Password changed successfully! ");
    }

    private static void searchGlobal() {
    }

    private static void manageCarts() {
        while (true) {
            System.out.println("""
                    Manage Carts
                    1. View all carts
                    2. View user's cart
                    3. Clear user's cart
                    0. Back
                    """);
            System.out.println("Choose option: ");
            int option;
            try {
                option = scannerInt.nextInt();
                scannerStr.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a number.");
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
                    System.out.println("Enter username: ");
                    String username = scannerStr.nextLine();
                    User user = userService.getUserByUsername(username);
                    Cart cart = cartService.getCartByUserId(user.getId());
                    if (cart == null) {
                        System.out.println("Cart not found.");
                    } else {
                        System.out.println(cart);
                    }
                }
                case 3 -> {
                    System.out.println("Enter username: ");
                    String username = scannerStr.nextLine();
                    User user = userService.getUserByUsername(username);
                    if (cartService.clearCart(user.getId())) {
                        System.out.println("Cart cleared.");
                    } else {
                        System.out.println("Cart not found.");
                    }
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option.Try again.");
            }
        }
    }

    private static void manageProducts() {
        while (true) {
            System.out.println("""
                    Manage Products
                    1. View all products
                    2. Search product
                    3. Show products by category
                    4. Delete product
                    0. Back
                    """);
            System.out.println("Choose option: ");
            int option;
            try {
                option = scannerInt.nextInt();
                scannerStr.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a number.");
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
                    System.out.println("Enter product name: ");
                    String productName = scannerStr.nextLine();
                    List<Product> products = productService.searchByProductName(productName);
                    for (Product product : products) {
                        System.out.println(product);
                    }
                }
                case 3 -> {
                    System.out.println("Enter category name: ");
                    String productCategory = scannerStr.nextLine();
                    List<Product> products = productService.getByCategory(productCategory);
                    for (Product product : products) {
                        System.out.println(product);
                    }
                }
                case 4 -> {
                    System.out.println("Enter product to delete: ");
                    String deleteProduct = scannerStr.nextLine();
                    if (userService.delete(userService.getUserByUsername(deleteProduct).getId())){
                        System.out.println("Product deleted.");
                    } else {
                        System.out.println("Product not found");
                    }
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void manageCategories() {
        while (true) {
            System.out.println("""
                    Manage Categories: 
                    1. View category list
                    2. Browse category
                    3. Add category
                    4. Delete category
                    0. Back
                    """);
            System.out.println("Choose option: ");
            int option;
            try {
                option = scannerInt.nextInt();
                scannerStr.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a number.");
                scannerStr.nextLine();
                continue;
            }
            switch (option) {
                case 1 -> {
                    List<Category> categories = categoryService.getAll();
                    for (Category c : categories) {
                        System.out.println(c.getName());
                    }
                }
                case 2 -> {
                    /// / to do
                }
                case 3 -> {

                }
            }
        }
    }

    private static void manageUsers() {
        while (true) {
            System.out.println("""
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
                System.out.println("Invalid input, please enter a number.");
                scannerStr.nextLine();
                continue;
            }
            switch (adminChoice) {
                case 1 -> {
                    viewUsersByRole();
                }
                case 2 -> {
                    System.out.println("Enter user to delete: ");
                    String username = scannerStr.nextLine();
                    if(userService.delete(userService.getUserByUsername(username).getId())){
                       System.out.println("User deleted successfully.");
                    } else {
                       System.out.println("User not found.");
                    }
                }
                case 3 -> {
                    System.out.println("Enter user to change role: ");
                    String username = scannerStr.nextLine();
                    User user = userService.getUserByUsername(username);
                    if (user != null) {
                        System.out.println("""
                                Select new role: 
                                1. ADMIN
                                2. SELLER
                                3. COSTUMER """);
                        int newRole;
                        try {
                            newRole = scannerInt.nextInt();
                            scannerStr.nextLine();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input, please enter a number.");
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
                            userService.changeUserRole(user.getId(),role);
                            System.out.println("Role updated successfully.");
                        } else {
                            System.out.println("Invalid, role selected.");
                        }
                    } else {
                        System.out.println("User not found.");
                    }
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option try again.");
            }
        }
    }

    private static void viewUsersByRole() {
        System.out.println("""
                1. View Admins
                2. View Sellers
                3. View Costumers
                0. Back """);
        System.out.println("Choose option: ");
        int choice;
        try {
            choice = scannerInt.nextInt();
            scannerStr.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input, please enter a number.");
            scannerStr.nextLine();
            return;
        }
        UserRole role = switch (choice) {
            case 1 -> UserRole.ADMIN;
            case 2 -> UserRole.SELLER;
            case 3 -> UserRole.CUSTOMER;
            default -> null;
        };
        if ( role == null && choice != 0) {
            System.out.println("Invalid option.");
            return;
        }
        if (choice == 0) return;
        List<User> users = userService.getByRole(role);
        if (users.isEmpty()) {
            System.out.println("No users found for role: " + role);
        } else {
            System.out.println("Users with role: " + role + ": ");
            for (User user : users) {
                System.out.println("-" + user.getFullName() + " ( " + user.getUserName() + " )");
            }
        }
    }

    /////////////////////////// SELLER PAGE ////////////////////////

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

    //////////////////// CUSTOMER PAGE //////////////////

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

    ///////////////////// PRINT MENUS ////////////////////////////

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