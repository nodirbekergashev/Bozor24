package uz.pdp.bot.util;

import java.io.File;

import lombok.SneakyThrows;

public class PhotoUtil {

    @SneakyThrows
    public static File getPhoto(String url){
        return new File(url);
    }

}
