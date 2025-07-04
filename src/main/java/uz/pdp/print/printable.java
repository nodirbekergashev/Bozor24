package uz.pdp.print;

import uz.pdp.model.User;

import java.util.List;

public class printable {
    public static String PrintUsers(List<User> users) {
        StringBuilder sb = new StringBuilder();

        // Table Header
        sb.append(String.format("%-20s %-15s %-10s %-25s %-25s%n",
                "Full Name", "Username", "Role", "Created At", "Updated At"));
        sb.append("=".repeat(95)).append(System.lineSeparator());

        // Table Rows
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
}
