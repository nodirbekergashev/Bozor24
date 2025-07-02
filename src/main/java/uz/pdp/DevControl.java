package uz.pdp;

import uz.pdp.enums.UserRole;
import uz.pdp.model.User;

import java.util.ArrayList;
import java.util.List;

public class DevControl {

    public static void main(String[] args) {
        List<User> users =  new ArrayList<>();

        User user = new User("Nargiza Allambergenova","thngza","123", UserRole.ADMIN);
        User user1 = new User("Madina Ismailova","medina","123", UserRole.CUSTOMER);
        User user2 = new User("Muzaffar Ismailov","muza","123", UserRole.CUSTOMER);





    }
}
