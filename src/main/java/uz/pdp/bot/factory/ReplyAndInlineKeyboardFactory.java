package uz.pdp.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ReplyAndInlineKeyboardFactory {

    public static ReplyKeyboardMarkup createReplyKeyboard(List<String> buttons, int columns) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rows);

        int index = 0;
        KeyboardRow row = new KeyboardRow();
        for (String button : buttons) {
            index++;

            if (index % columns == 0) {
                rows.add(new KeyboardRow());
            }
        }
        return replyKeyboardMarkup;
    }

    public static InlineKeyboardMarkup createInlineKeyboard(List<List<InlineKeyboardButton>> buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }
}
