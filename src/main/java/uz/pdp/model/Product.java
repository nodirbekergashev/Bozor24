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
    private int quantity;
    private double price;
    private UUID catId;
    private UUID sellerId;
}
