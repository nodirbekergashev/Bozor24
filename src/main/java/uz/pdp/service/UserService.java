package uz.pdp.service;

import uz.pdp.baseAbs.BaseService;
import uz.pdp.enums.UserRole;
import uz.pdp.model.User;
import uz.pdp.wrapperLists.UserListWrapper;

import java.util.List;
import java.util.UUID;

import static uz.pdp.db.Lists.USERS;

import static uz.pdp.utils.FileUtil.writeToXml;

public class UserService implements BaseService<User> {
    private static final String pathName = "users.xml";

    static {
        if (USERS.isEmpty()) {
            USERS.add(new User("", "admin","0411", "1314", UserRole.ADMIN));
        }
    }

    @Override
    public boolean add(User user) {
        boolean existing = USERS.stream()
                .anyMatch(u -> u.equals(user));
        if (existing) {
            System.out.println("This user already exists!");
            return false;
        }
        USERS.add(user);
        saveToFile();
        return true;
    }

    public User addBotUser(User user) {
        boolean existing = USERS.stream()
                .anyMatch(u -> u.equals(user));
        if (existing) {
            System.out.println("This user already exists!");
            return null;
        }
        USERS.add(user);
        saveToFile();
        return user;
    }

    @Override
    public void update(UUID id, User user) {
        User u = getById(id);
        if (u != null) {
            u.setFullName(user.getFullName());
            u.setUserName(user.getUserName());
            u.setPassword(user.getPassword());
            u.setRole(user.getRole());
            saveToFile();
        }
    }

    @Override
    public boolean delete(UUID id) {
        User u = getById(id);
        if (u != null) {
            u.setActive(false);
            saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public User getById(UUID id) {
        return USERS.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> getAll() {
        return USERS;
    }

    @Override
    public void saveToFile() {
        try {
            writeToXml(pathName, new UserListWrapper(USERS));
        } catch (Exception e) {
            System.out.println("Error saving file " + e.getMessage());
        }
    }

    @Override
    public String getCreatedTimeById(UUID id) {
        return BaseService.super.getCreatedTimeById(id);
    }

    @Override
    public String getUpdatedTimeById(UUID id) {
        return BaseService.super.getUpdatedTimeById(id);
    }

    public User login(String username, String password) {
        return USERS.stream()
                .filter(User::isActive)
                .filter(user -> user.getUserName().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public User getUserByUsername(String username) {
        return USERS.stream()
                .filter(user -> user.getUserName().equals(username))
                .findFirst()
                .orElse(null);
    }

    public boolean isUsernameValid(String username) {
        return username.matches("^[a-z_]{4,13}$");
    }

    public boolean changeUserRole(UUID id, UserRole newRole) {
        boolean result = USERS.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .map(user -> {
                    user.setRole(newRole);
                    return true;
                })
                .orElse(false);

        if (result) {
            saveToFile();
        }
        return result;
    }

    public List<User> getByRole(UserRole role) {
        return USERS.stream()
                .filter(user -> user.getRole().equals(role))
                .toList();
    }

    public void changePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        saveToFile();
    }
}
