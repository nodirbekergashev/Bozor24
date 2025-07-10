package uz.pdp.itemClasses;

import lombok.*;
import uz.pdp.baseAbs.BaseModel;
import uz.pdp.enums.OrderStatus;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseModel {
    private UUID productId;
    private int productCount;
}
