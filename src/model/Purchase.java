package model;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "PURCHASE")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PURCHASE_ID")
    private int purchaseId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "USERNAME")  // FK a USER_.USERNAME
    private User user;

    @ManyToOne(optional = true)
    @JoinColumn(name = "PRODUCT_ID") // FK a PRODUCT.PRODUCT_ID
    private Product product;

    @Column(name = "PURCHASE_DATE")
    private LocalDate purchaseDate;

    public Purchase() { }

    public Purchase(int purchaseId, User user, Product product, LocalDate purchaseDate) {
        this.purchaseId = purchaseId;
        this.user = user;
        this.product = product;
        this.purchaseDate = purchaseDate;
    }

    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
}
