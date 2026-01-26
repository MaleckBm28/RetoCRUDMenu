package model;

import javax.persistence.*;

@Entity
@Table(name = "CARD")
@PrimaryKeyJoinColumn(name = "PRODUCT_ID")
public class Card extends Product {

    @Column(name = "RARITY", length = 20)
    private String rarity;

    public Card() {
        super();
        this.rarity = "";
    }

    public Card(String rarity, int productId, String name, double price, GameType gameType, int stock) {
        super(productId, name, price, gameType, stock);
        this.rarity = rarity;
    }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
}
