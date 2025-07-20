package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.baseAbs.BaseModel;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Category extends BaseModel {
    public static final UUID ROOT_CATEGORY_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private String name;
    private UUID parentId;
    private boolean hasProducts;
}
