package uz.pdp;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.model.Product;
import uz.pdp.other.Bozor24Bot;
import uz.pdp.service.ProductService;

import java.util.UUID;


public class DevControl {

    public static void main(String[] args) {
        ProductService productService = new ProductService();
        startBot();
    }

    private static void startBot() {
        ProductService productService = new ProductService(); // kerakli implementatsiya
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bozor24Bot(productService));
            System.out.println("✅ Bozor24Bot ishga tushdi");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}