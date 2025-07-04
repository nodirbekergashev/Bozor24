package uz.pdp.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.service.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.*;

public class Bozor24Bot extends TelegramLongPollingBot {

    private final ProductService productService;
    private final OrderService orderService;

    public Bozor24Bot() {
        this.productService = new ProductService();
        this.orderService = new OrderService(); // OrderService ni ham yaratamiz
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            switch (text) {
                case "/start" -> sendMenuButtons(chatId, "👋 Xush kelibsiz! Quyidagilardan birini tanlang:");
                case "🛍 Products" -> sendMessage(chatId,"productService.getAllProductForBot()"); // product daraxt menyusi
                case "🗂 Categories" -> sendCategoryMenu(chatId, "root"); // yoki oddiy kategoriya ro‘yxati
//                case "🛒 My Cart" -> sendMessage(chatId, getCartMessage(chatId)); // bu methodni siz yozasiz
//                case "📦 My Orders" -> sendMessage(chatId, orderService.getOrderHistory(chatId)); // bu method ham sizga bog‘liq
                default -> sendMessage(chatId, "❗ Noma'lum buyruq. Iltimos, /start ni bosing.");
            }
        } else if (update.hasCallbackQuery()) {
//            handleCallback(update.getCallbackQuery()); // kategoriya va mahsulotlar uchun
        }
    }


    private String getProductList() {
        return"products not found"; // productService dan ma'lumot olish
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
        message.setParseMode("Markdown");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendCategoryMenu(Long chatId, String categoryName) {
        List<String> children;

        // Root kategoriya bo‘lsa, ildiz (asosiy) kategoriyalarni olamiz
        if (categoryName.equals("root")) {
//            children = categoryService.getPArentCategories();
        } else {
//            children = categoryService.getSubCategoriesByParent(categoryName);
        }

//        if (children == null || children.isEmpty()) {
//            sendMessage(chatId, "⚠️ Bu bo‘limda hozircha kategoriya yoki mahsulot mavjud emas.");
//            return;
//        }

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("📂 Kategoriya: *" + categoryName + "*");
        message.setParseMode("Markdown");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

//        for (String child : children) {
//            InlineKeyboardButton button = new InlineKeyboardButton();
//            button.setText(child);
//            button.setCallbackData("CATEGORY_" + child);
//            rows.add(List.of(button));
//        }

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "bazar_24_bot"; // Masalan: Bozor24_Bot
    }

    @Override
    public String getBotToken() {
        return "7916545438:AAFvuOgnucYZdfFIrILAmygu7-DxL6vFujo"; // BotFatherdan olingan token
    }
}
