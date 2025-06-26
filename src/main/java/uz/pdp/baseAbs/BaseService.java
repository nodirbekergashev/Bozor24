package uz.pdp.baseAbs;

import java.util.List;
import java.util.UUID;

public interface BaseService<T> {
    boolean add(T t);

    void update(UUID id, T t);

    boolean delete(UUID id);

    T getById(UUID id);

    List<T> getAll();

    void saveToFile();

    default String getCreatedTimeById() {
        return "";
    }

    default String getUpdatedTimeById() {
        return "";
    }

}
