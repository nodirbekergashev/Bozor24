package uz.pdp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileUtil {
    private static final String PATH = "src/main/java/uz/pdp/recurse/";

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static <T> void writeToJson(String fileName, T t) {
        try {
            jsonMapper.writeValue(new File(PATH + fileName), t);
        } catch (Exception e) {
            System.out.println("Problem writing JSON file: " + e.getMessage());
        }
    }

    public static <T> List<T> readFromJson(String fileName, Class<T> clazz) {
        try {
            return jsonMapper.readValue(new File(PATH + fileName),
                    jsonMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("No content to map due to end-of-input")) {
                return new ArrayList<>();
            }
            System.out.println("Problem reading JSON file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static <T> void writeToXml(String fileName, T t) {
        try {
            xmlMapper.writeValue(new File(PATH + fileName), t);
        } catch (Exception e) {
            System.out.println("Problem writing XML file: " + e.getMessage());
        }
    }

    public static <T> List<T> readFromXml(String fileName, Class<T> clazz) {
        try {
            return xmlMapper.readValue(new File(PATH + fileName),
                    xmlMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("No content to map due to end-of-input")) {
                return new ArrayList<>();
            }
            System.out.println("Problem reading XML file: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
