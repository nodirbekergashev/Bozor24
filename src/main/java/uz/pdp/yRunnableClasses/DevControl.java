package uz.pdp.yRunnableClasses;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import uz.pdp.bot.MainClassBot;


public class DevControl {

    public static void main(String[] args) {
        startBot();


    }

    private static void startBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MainClassBot());
            System.out.println("✅ MainClassBot ishga tushdi");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}