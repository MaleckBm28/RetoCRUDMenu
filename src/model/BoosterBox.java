package model;

import javax.persistence.*;

/**
 * Modelo que representa una caja de sobres (Booster Box) en el sistema.
 * Esta entidad extiende de {@link Product} y añade información específica 
 * sobre la cantidad de sobres contenidos en la caja.
 * * @author Alex
 * @version 1.0
 */
@Entity
@Table(name = "BOOSTER_BOX")
@PrimaryKeyJoinColumn(name = "PRODUCT_ID")
public class BoosterBox extends Product {

    /** Cantidad de sobres incluidos en la caja. */
    @Column(name = "PACK_AMOUNT", nullable = false)
    private int packAmount;

    /**
     * Constructor por defecto.
     * Inicializa la cantidad de sobres a cero y llama al constructor de la superclase.
     */
    public BoosterBox() {
        super();
        this.packAmount = 0;
    }

    /**
     * Constructor con parámetros para inicializar una caja de sobres.
     * * @param packAmount Cantidad de sobres en la caja.
     * @param productId  Identificador único del producto.
     * @param name       Nombre comercial de la caja.
     * @param price      Precio de venta.
     * @param gameType   Tipo de juego asociado (Pokémon, Magic, etc.).
     * @param stock      Unidades disponibles en el inventario.
     */
    public BoosterBox(int packAmount, int productId, String name, double price, GameType gameType, int stock) {
        super(productId, name, price, gameType, stock);
        this.packAmount = packAmount;
    }

    /**
     * Obtiene la cantidad de sobres de la caja.
     * @return El número de sobres.
     */
    public int getPackAmount() { 
        return packAmount; 
    }

    /**
     * Establece la cantidad de sobres de la caja.
     * @param packAmount El nuevo número de sobres.
     */
    public void setPackAmount(int packAmount) { 
        this.packAmount = packAmount; 
    }
}