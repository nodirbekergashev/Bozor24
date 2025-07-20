package uz.pdp.bot.factory;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.bot.factory.wrapper.RecordWrapper;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class InlineKeyboardMarkupFactory<T> {
    private final List<T> records;
    private final int columns;

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        if (!records.isEmpty()) {

            InlineKeyboardMarkup i = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            i.setKeyboard(rows);

            RecordWrapper wrapper = null;
            int index = 0;
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (T record : records) {
                index++;
                wrapper = wrapper(record);
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(wrapper.getName());
                button.setCallbackData(wrapper.getCommand() + wrapper.getId());
                row.add(button);
                if (index % columns == 0) {
                    rows.add(row);
                    row = new ArrayList<>();
                }
            }
            if (!row.isEmpty()) {
                rows.add(row);
            }

            return i;
        }
        return new InlineKeyboardMarkup();
    }

    protected abstract RecordWrapper wrapper(T t);
}
