package model;

import javax.persistence.*;

@Entity
@Table(name = "BOOSTER_BOX")
@PrimaryKeyJoinColumn(name = "PRODUCT_ID")
public class BoosterBox extends Product {

    @Column(name = "PACK_AMOUNT", nullable = false)
    private int packAmount;

    public BoosterBox() {
        super();
        this.packAmount = 0;
    }

    public BoosterBox(int packAmount, int productId, String name, double price, GameType gameType, int stock) {
        super(productId, name, price, gameType, stock);
        this.packAmount = packAmount;
    }

    public int getPackAmount() { return packAmount; }
    public void setPackAmount(int packAmount) { this.packAmount = packAmount; }
}
