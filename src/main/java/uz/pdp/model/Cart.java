package uz.pdp.model;

import lombok.*;
import uz.pdp.baseAbs.BaseModel;
import uz.pdp.itemClasses.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cart extends BaseModel {
    private UUID userId;
    private List<CartItem> products;

    public Cart(UUID userId) {
        this.userId = userId;
        this.products = new ArrayList<>();
    }


}
