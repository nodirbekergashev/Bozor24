package uz.pdp.itemClasses;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private UUID productId;
    private int quantity;

}
