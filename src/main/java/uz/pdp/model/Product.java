package uz.pdp.model;

import lombok.*;
import uz.pdp.baseAbs.BaseModel;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Product extends BaseModel {
    private String name;
    private String description;
    private int quantity;
    private double price;
    private UUID categoryId;
    private UUID sellerId;
}
