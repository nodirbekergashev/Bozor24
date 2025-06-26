package uz.pdp.baseAbs;

import lombok.*;

import java.util.Date;
import java.util.UUID;
@Data
public abstract class BaseModel {
    private final UUID id;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    public BaseModel() {
        id = UUID.randomUUID();
        isActive = true;
        createdAt = new Date();
        updatedAt = createdAt;
    }
}
