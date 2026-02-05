package model;

import java.io.Serializable;

public class CartItem implements Serializable {

    private int productId;
    private String name;
    private double price;
    private int quantity;

    private String productType;
    private String gameType;
    private String rarity;
    private String imagePath;

    public CartItem(int productId, String name, double price, int quantity, String productType, String gameType, String rarity, String imagePath) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.productType = productType;
        this.gameType = gameType;
        this.rarity = rarity;
        this.imagePath = imagePath;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductType() {
        return productType;
    }

    public String getGameType() {
        return gameType;
    }

    public String getRarity() {
        return rarity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void addQuantity(int q) {
        this.quantity += q;
    }

    public double getSubtotal() {
        return price * quantity;
    }
}
