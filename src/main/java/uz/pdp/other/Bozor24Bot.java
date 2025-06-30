package uz.pdp.other;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.model.Product;
import uz.pdp.service.ProductService;

import java.util.List;

public class Bozor24Bot extends TelegramLongPollingBot {

    private final ProductService productService;

    // Constructor orqali ProductService ni ulaymiz
    public Bozor24Bot(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public String getBotUsername() {
        return "bazar_24_bot"; // Masalan: Bozor24_Bot
    }

    @Override
    public String getBotToken() {
        return "7916545438:AAFvuOgnucYZdfFIrILAmygu7-DxL6vFujo"; // BotFatherdan olingan token
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            String response = handleCommand(userMessage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText(response);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private String handleCommand(String command) {
        switch (command) {
            case "/start":
                return "👋 Assalomu alaykum! Bozor24 botiga xush kelibsiz!\n/commands ni yozib buyruqlar ro‘yxatini ko‘ring.";

            case "/commands":
                return """
                        🧾 Bot buyruqlari:
                        /products - Mahsulotlar ro‘yxati
                        /help - Yordam
                        """;

            case "/products":
                return getProductList();

            default:
                return "❌ Noto‘g‘ri buyruq. /help ni yozib ko‘ring.";
        }
    }

    private String getProductList() {
        List<Product> productList = productService.getAll();
        if (productList.isEmpty()) {
            return "❗ Hozircha mahsulotlar mavjud emas.";
        }

        StringBuilder sb = new StringBuilder("📦 Mahsulotlar:\n\n");
        for (Product product : productList) {
            sb.append("🔸 ").append(product.getName())
                    .append(" – ").append(product.getPrice()).append(" so'm\n");
        }
        return sb.toString();
    }
}
