package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.baseAbs.BaseModel;
import uz.pdp.enums.OrderStatus;
import uz.pdp.itemClasses.OrderItem;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseModel {
    private UUID userId;
    private String orderNumber;
    private List<OrderItem> ordersByUser;
    private double totalPrice;
    private OrderStatus status;
}
