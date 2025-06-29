package uz.pdp.print;

import uz.pdp.model.User;

import java.util.List;

public class printable {
    public static String PrintUsers(List<User> users) {
        StringBuilder sb = new StringBuilder();
        users.stream()
                .filter(User::isActive)
                .forEach(user -> {
                    sb.append("fullName : ").append(user.getFullName()).append(" \t");
                    sb.append("username: ").append(user.getUserName()).append(" \t");
                    sb.append("role: ").append(user.getRole()).append(" \t");
                    sb.append("createdAt: ").append(user.getCreatedAt()).append(" \t");
                    sb.append("updatedAt: ").append(user.getUpdatedAt()).append(" \t");
                });
        return sb.toString();
    }
}
