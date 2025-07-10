package uz.pdp.enums;

import lombok.Getter;

@Getter
public enum CallBackQueryStarting {

    CATEGORY("CATEGORY",8),
    PRODUCT("PRODUCT",7),
    CART("CART",4),
    USER_ROLE("USER_ROLE",9),
    ORDER("ORDER",5),
    ;

    private final String value;
    private final int length;

    CallBackQueryStarting(String value, int length) {
        this.value = value;
        this.length = length;
    }
}
