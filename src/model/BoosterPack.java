package model;

import javax.persistence.*;

/**
 * Modelo que representa un sobre de cartas (Booster Pack) en el sistema.
 * Esta entidad extiende de {@link Product} e incluye información específica
 * sobre el número de cartas contenidas en el sobre.
 * * @author Alex
 * @version 1.0
 */
@Entity
@Table(name = "BOOSTER_PACK")
@PrimaryKeyJoinColumn(name = "PRODUCT_ID")
public class BoosterPack extends Product {

    /** Cantidad de cartas individuales que contiene el sobre. */
    @Column(name = "CARD_AMOUNT", nullable = false)
    private int cardAmount;

    /**
     * Constructor por defecto.
     * Inicializa la cantidad de cartas a cero y llama al constructor de la superclase.
     */
    public BoosterPack() {
        super();
        this.cardAmount = 0;
    }

    /**
     * Constructor con parámetros para inicializar un sobre de cartas.
     * * @param cardAmount Cantidad de cartas en el sobre.
     * @param productId  Identificador único del producto.
     * @param name       Nombre comercial del sobre.
     * @param price      Precio de venta.
     * @param gameType   Tipo de juego asociado (Pokémon, Magic, Yu-Gi-Oh, etc.).
     * @param stock      Unidades disponibles en el inventario.
     */
    public BoosterPack(int cardAmount, int productId, String name, double price, GameType gameType, int stock) {
        super(productId, name, price, gameType, stock);
        this.cardAmount = cardAmount;
    }

    /**
     * Obtiene la cantidad de cartas del sobre.
     * @return El número de cartas.
     */
    public int getCardAmount() { 
        return cardAmount; 
    }

    /**
     * Establece la cantidad de cartas del sobre.
     * @param cardAmount El nuevo número de cartas.
     */
    public void setCardAmount(int cardAmount) { 
        this.cardAmount = cardAmount; 
    }
}