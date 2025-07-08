package uz.pdp.bot.factory.inline;

import uz.pdp.bot.factory.InlineKeyboardMarkupFactory;
import uz.pdp.bot.factory.wrapper.RecordWrapper;
import uz.pdp.model.Product;

import java.util.List;

import static uz.pdp.enums.CallBackQueryStarting.PRODUCT;

public class ProductInlineKeyboardFactory extends InlineKeyboardMarkupFactory<Product> {
    public ProductInlineKeyboardFactory(List<Product> records, int columns) {
        super(records, columns);
    }

    @Override
    protected RecordWrapper wrapper(Product product) {
        return RecordWrapper.builder()
                .id(product.getId())
                .name(product.getName())
                .command(PRODUCT.getValue())
                .build();
    }
}
