package uz.pdp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class FileUtil {
    private static final String PATH = "src/main/java/uz/pdp/database";

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static <T> void writeToJson(String fileName, T t) throws IOException {
        jsonMapper.writeValue(new File(PATH + fileName), t);
    }

    public static <T> List<T> readFromJson(String fileName, Class<T> clazz) {
        try {
            File file = new File(PATH + fileName);
            if (file.length() == 0) return new ArrayList<>();
            return jsonMapper.readValue(file,
                    jsonMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static <T> void writeToXml(String fileName, T t) throws IOException {
        xmlMapper.writeValue(new File(PATH + fileName), t);
    }

    public static <T> List<T> readFromXml(String fileName, Class<T> clazz) {
        try {
            File file = new File(PATH + fileName);
            if (file.length() == 0) return  new ArrayList<>();
            return xmlMapper.readValue(file,
                    xmlMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

}
