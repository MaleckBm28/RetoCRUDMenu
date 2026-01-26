package model;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCT")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Product {

    @Id
    @Column(name = "PRODUCT_ID")
    private int productId;

    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "PRICE", nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "GAME_TYPE", length = 30)
    private GameType gameType;

    @Column(name = "STOCK")
    private int stock;

    @Column(name = "IMAGE_PATH", length = 255)
    private String imagePath;

    public Product() {
        this.imagePath = "/images/default_card.png";
    }

    public Product(int productId, String name, double price, GameType gameType, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.gameType = gameType;
        this.stock = stock;
        // ojo: en tu constructor original estabas asignando imagePath mal
        // aquí lo dejamos tal cual y se setea con setter si hace falta
    }

    // getters/setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public GameType getGameType() { return gameType; }
    public void setGameType(GameType gameType) { this.gameType = gameType; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
