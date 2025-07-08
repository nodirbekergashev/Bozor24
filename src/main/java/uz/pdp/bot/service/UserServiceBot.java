package uz.pdp.bot.service;

import uz.pdp.bot.botModel.BotUser;
import uz.pdp.enums.UserRole;
import uz.pdp.model.User;
import uz.pdp.service.UserService;
import uz.pdp.wrapperLists.BotUsersListWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uz.pdp.utils.FileUtil.readFromXml;
import static uz.pdp.utils.FileUtil.writeToXml;

public class UserServiceBot {
    private final UserService userService = new UserService();
    private static final String PATH_NAME = "botUsers.xml";
    static final List<BotUser> BOT_USERS = new ArrayList<>();

    static {
        readFromXml(PATH_NAME, BotUser.class);
    }

    public void add(String fullName, Long userId, UserRole role, Long chatId) {
        User user = userService.addBotUser(new User(fullName, "", "", role));
        if (user != null) {
            UUID id = user.getId();
            BotUser u = new BotUser(chatId, userId, id);
            BOT_USERS.add(u);
            saveToFile();
        }
    }

    public UUID getUserIdByTgUser(Long userId) {
        BotUser user = BOT_USERS.stream()
                .filter(botUser -> botUser.getUserId().equals(userId))
                .findFirst().orElse(null);
        if (user == null) {
            return null;
        }
        return user.getUserBaseId();
    }

    public void saveToFile() {
        writeToXml(PATH_NAME, new BotUsersListWrapper(BOT_USERS));
    }
}
