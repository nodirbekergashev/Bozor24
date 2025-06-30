package uz.pdp;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.model.Category;
import uz.pdp.model.Product;
import uz.pdp.other.Bozor24Bot;
import uz.pdp.service.ProductService;

import java.util.Locale;
import java.util.UUID;


public class DevControl {

    public static void main(String[] args) {
        ProductService productService = new ProductService();

        productService.add(new Product("Sariq olma", 12000, 15000.0, UUID.randomUUID(), UUID.randomUUID(), new Category()));
        productService.add(new Product("tapchka", 12000, 150000, UUID.randomUUID(), UUID.randomUUID(), new Category()));
        productService.add(new Product("shahslik", 12000, 6000, UUID.randomUUID(), UUID.randomUUID(), new Category()));
        productService.add(new Product("banan", 12000, 15000, UUID.randomUUID(), UUID.randomUUID(), new Category()));
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