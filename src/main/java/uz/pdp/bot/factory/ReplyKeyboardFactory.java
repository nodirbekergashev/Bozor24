package uz.pdp.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;


public class ReplyKeyboardFactory {

    public static ReplyKeyboardMarkup createReplyKeyboard(List<String> buttons, int columns) {
        ReplyKeyboardMarkup r = new ReplyKeyboardMarkup();
        r.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        r.setKeyboard(rows);

        int index = 0;
        KeyboardRow row = new KeyboardRow();
        for (String button : buttons) {
            index++;
            row.add(button);
            if (index % columns == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        return r;
    }
}
