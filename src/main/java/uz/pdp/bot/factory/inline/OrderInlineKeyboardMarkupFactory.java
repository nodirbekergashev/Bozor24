package uz.pdp.bot.factory.inline;

import uz.pdp.bot.factory.InlineKeyboardMarkupFactory;
import uz.pdp.bot.factory.wrapper.RecordWrapper;
import uz.pdp.model.Order;

import java.util.List;

import static uz.pdp.enums.CallBackQueryStarting.ORDER;

public class OrderInlineKeyboardMarkupFactory extends InlineKeyboardMarkupFactory<Order> {
    public OrderInlineKeyboardMarkupFactory(List<Order> orders, int colCount) {
        super(orders, colCount);
    }

    @Override
    protected RecordWrapper wrapper(Order order) {
        return RecordWrapper.builder()
                .name(order.getOrderNumber())
                .command(ORDER.getValue())
                .id(order.getId())
                .build();
    }
}
