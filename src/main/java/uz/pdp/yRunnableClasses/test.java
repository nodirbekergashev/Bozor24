package uz.pdp.yRunnableClasses;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.Queue;

import io.github.cdimascio.dotenv.Dotenv;

public class test {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
        System.out.println("TOKEN: " + botToken);
    }
}