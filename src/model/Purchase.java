package model;

import java.time.LocalDate;
import javax.persistence.*;

/**
 * Entidad que representa una transacción de compra en el sistema.
 * Registra la relación entre un usuario, un producto y la fecha en la que 
 * se realizó la adquisición para el histórico de ventas.
 * * @author Alex
 * @version 1.0
 */
@Entity
@Table(name = "PURCHASE")
public class Purchase {

    /** Identificador único de la compra (autogenerado). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PURCHASE_ID")
    private int purchaseId;

    /** Usuario que realizó la compra. */
    @ManyToOne(optional = true)
    @JoinColumn(name = "USERNAME")  // FK a USER_.USERNAME
    private User user;

    /** Producto adquirido en la transacción. */
    @ManyToOne(optional = true)
    @JoinColumn(name = "PRODUCT_ID") // FK a PRODUCT.PRODUCT_ID
    private Product product;

    /** Fecha en la que se efectuó la compra. */
    @Column(name = "PURCHASE_DATE")
    private LocalDate purchaseDate;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Purchase() { }

    /**
     * Constructor con parámetros para registrar una nueva compra.
     * * @param purchaseId   Identificador de la transacción.
     * @param user         Objeto usuario comprador.
     * @param product      Objeto producto comprado.
     * @param purchaseDate Fecha de la operación.
     */
    public Purchase(int purchaseId, User user, Product product, LocalDate purchaseDate) {
        this.purchaseId = purchaseId;
        this.user = user;
        this.product = product;
        this.purchaseDate = purchaseDate;
    }

    /** @return El ID de la compra. */
    public int getPurchaseId() { return purchaseId; }
    /** @param purchaseId El ID a establecer. */
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }

    /** @return El usuario asociado. */
    public User getUser() { return user; }
    /** @param user El usuario a establecer. */
    public void setUser(User user) { this.user = user; }

    /** @return El producto asociado. */
    public Product getProduct() { return product; }
    /** @param product El producto a establecer. */
    public void setProduct(Product product) { this.product = product; }

    /** @return La fecha de compra. */
    public LocalDate getPurchaseDate() { return purchaseDate; }
    /** @param purchaseDate La fecha a establecer. */
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
}