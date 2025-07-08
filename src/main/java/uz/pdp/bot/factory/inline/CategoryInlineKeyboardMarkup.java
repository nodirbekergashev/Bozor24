package uz.pdp.bot.factory.inline;

import uz.pdp.bot.factory.InlineKeyboardMarkupFactory;
import uz.pdp.bot.factory.wrapper.RecordWrapper;
import uz.pdp.model.Category;

import java.util.List;

public class CategoryInlineKeyboardMarkup extends InlineKeyboardMarkupFactory<Category> {

    public CategoryInlineKeyboardMarkup(List<Category> records, int colCount) {
        super(records, colCount);
    }

    @Override
    protected RecordWrapper wrapper(Category category) {
        return RecordWrapper.builder()
                .id(category.getId())
                .name(category.getName())
                .command("CATEGORY")
                .build();
    }
}