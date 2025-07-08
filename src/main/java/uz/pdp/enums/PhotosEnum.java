package uz.pdp.enums;

import lombok.Getter;

@Getter
public enum PhotosEnum {
    ELECTRONICS("Electronics", "C:/projectPhotos/Electronics.jpg"),


    SMARTPHONES("Smartphones", "C:/projectPhotos/Smartphones.jpg"),

    SAMSUNG("Samsung phones", "C:/projectPhotos/Samsung.jpg"),
    APPLE("Apple phones", "C:/projectPhotos/Apple.jpg"),

    LAPTOPS("Laptops", "C:/projectPhotos/Laptops.jpg"),

    HP("HP laptops", "C:/projectPhotos/Hp.jpg"),
    DELL("Dell laptops", "C:/projectPhotos/Dells.jpg"),


    TOYS("Toys", "C:/projectPhotos/Toys.jpg"),

    DOLLS("Dolls", "C:/projectPhotos/Dolls.jpg"),

    BABY("Baby dolls", "C:/projectPhotos/Baby_Dolls.jpg"),
    FASHION("Fashion dolls", "C:/projectPhotos/Fashion_Dolls.jpg"),

    TEDDIES("Teddies", "C:/projectPhotos/Teddies.jpg"),

    CLASSIC("Classic teddies", "C:/projectPhotos/Classic_Teddies.jpg"),
    COLLECTIBLE("Collectible teddies", "C:/projectPhotos/Collectible_Teddies.jpg"),


    SPORT("Sport tools", "C:/projectPhotos/Sports.jpg"),

    RACKETS("Rackets", "C:/projectPhotos/Rackets.jpg"),

    TENNIS("Tennis Rackets", "C:/projectPhotos/Tennis_Rackets.jpg"),
    BADMINTON("Badminton Rackets", "C:/projectPhotos/Badminton_Rackets.jpg"),

    BALLS("Balls", "C:/projectPhotos/Balls.jpg"),

    FOOTBALL("Football Balls", "C:/projectPhotos/Football_Balls.jpg"),
    BASKETBALL("Basketball Balls", "C:/projectPhotos/Basketball_Balls.jpg");

    private final String name;
    private final String url;

    PhotosEnum(String name, String url) {
        this.name = name;
        this.url = url;
    }
}