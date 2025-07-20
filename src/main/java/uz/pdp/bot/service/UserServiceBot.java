package uz.pdp.bot.service;

import org.telegram.telegrambots.meta.api.objects.Contact;
import uz.pdp.bot.botModel.BotUser;
import uz.pdp.enums.UserRole;
import uz.pdp.model.User;
import uz.pdp.service.UserService;
import uz.pdp.wrapperLists.BotUsersListWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uz.pdp.db.Lists.BOT_USERS;
import static uz.pdp.utils.FileUtil.writeToXml;

public class UserServiceBot {
    private final UserService userService = new UserService();
    private static final String PATH_NAME = "botRecurse/botUsers.xml";


    public void add(Contact contact, String userName, UserRole role, Long chatId) {
        String fullName = contact.getFirstName() != null ? contact.getFirstName() : " " + contact.getLastName() != null ? contact.getLastName() : " ";
        User user = userService.addBotUser(new User(fullName, userName, contact.getPhoneNumber(), "", role));
        if (user != null) {
            BotUser u = new BotUser(contact.getPhoneNumber(), fullName, chatId, contact.getUserId(), user.getId());
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

    public boolean isUseBefore(Long userId) {
        return BOT_USERS.stream()
                .anyMatch(botUser -> botUser.getUserId().equals(userId));
    }

    public void saveToFile() {
        writeToXml(PATH_NAME, new BotUsersListWrapper(BOT_USERS));
    }
}
