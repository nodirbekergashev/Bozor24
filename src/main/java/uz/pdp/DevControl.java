package uz.pdp;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import uz.pdp.model.User;
import uz.pdp.bot.Bozor24Bot;
import uz.pdp.service.ProductService;

import static uz.pdp.db.Lists.users;


public class DevControl {

    public static void main(String[] args) {
        ProductService productService = new ProductService();
        startBot();


    }

    private static void startBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bozor24Bot());
            System.out.println("✅ Bozor24Bot ishga tushdi");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}