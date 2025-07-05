package uz.pdp.bot;

import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.model.Category;
import uz.pdp.service.CategoryService;


import java.util.*;

public class MainClassBot extends TelegramLongPollingBot {
    private static final String BOT_USERNAME = "bazar_24_bot";
    private static final String BOT_TOKEN = "7916545438:AAFvuOgnucYZdfFIrILAmygu7-DxL6vFujo";

    @Override
    public String getBotUsername() {
        return BOT_USERNAME; // Masalan: Bozor24_Bot
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            User user = update.getMessage().getFrom();
            if (text.equals("/start")) {


                sendMessage(chatId, "Salom, xush kelibsiz! " + user.getFirstName() + "  \n" +
                        "Bizning botimiz orqali mahsulotlar va kategoriyalar bilan tanishishingiz mumkin.\n" +
                        "Iltimos, menyudan biror tugmani tanlang.");
            }
        }
    }


    private void sendMenuButtons(Long chatId, String welcomeText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(welcomeText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false); // menyu doim chiqib turadi

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("🛍 Products");
        row1.add("🗂 Categories");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("🛒 My Cart");
        row2.add("📦 My Orders");

        keyboardRows.add(row1);
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(createReplyKeyboardMarkup());
        message.setParseMode("");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendCategoryMenu(Long chatId, String categoryName) {
        CategoryService categoryService = new CategoryService();
        List<Category> children;
        Category categoryByName = categoryService.getCategoryByName(categoryName);
        if (categoryByName != null) {
            children = categoryService.getSubCategories(categoryByName.getId());


            if (children == null || children.isEmpty()) {
                sendMessage(chatId, "⚠️ Bu bo‘limda hozircha kategoriya yoki mahsulot mavjud emas.");
                return;
            }

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("📂 Kategoriya: *" + categoryName + "*");
            message.setParseMode("Markdown_V2");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            for (Category child : children) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(child.getName());
                button.setCallbackData("CATEGORY_" + child);
                rows.add(List.of(button));
            }

            markup.setKeyboard(rows);
            message.setReplyMarkup(markup);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private ReplyKeyboardMarkup createReplyKeyboardMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setResizeKeyboard(true); // adjusts the keyboard size to fit the screen
        keyboardMarkup.setOneTimeKeyboard(false); // the keyboard remains visible after selection

        KeyboardRow row = new KeyboardRow();


        KeyboardRow row1 = new KeyboardRow();
        row1.add("🛍 Products");
        row1.add("🗂 Categories");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("🛒 My Cart");
        row2.add("📦 My Orders");

        rows.add(row1);
        rows.add(row2);
        return keyboardMarkup;
    }
}
