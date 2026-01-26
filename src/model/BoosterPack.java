package model;

import javax.persistence.*;

@Entity
@Table(name = "BOOSTER_PACK")
@PrimaryKeyJoinColumn(name = "PRODUCT_ID")
public class BoosterPack extends Product {

    @Column(name = "CARD_AMOUNT", nullable = false)
    private int cardAmount;

    public BoosterPack() {
        super();
        this.cardAmount = 0;
    }

    public BoosterPack(int cardAmount, int productId, String name, double price, GameType gameType, int stock) {
        super(productId, name, price, gameType, stock);
        this.cardAmount = cardAmount;
    }

    public int getCardAmount() { return cardAmount; }
    public void setCardAmount(int cardAmount) { this.cardAmount = cardAmount; }
}
