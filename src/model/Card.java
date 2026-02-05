package model;

import javax.persistence.*;

/**
 * Modelo que representa una carta individual en el sistema.
 * Esta entidad extiende de {@link Product} e incluye información específica
 * sobre el grado de rareza de la carta (Común, Rara, Legendaria, etc.).
 * * @author Alex
 * @version 1.0
 */
@Entity
@Table(name = "CARD")
@PrimaryKeyJoinColumn(name = "PRODUCT_ID")
public class Card extends Product {

    /** Grado de rareza de la carta. */
    @Column(name = "RARITY", length = 20)
    private String rarity;

    /**
     * Constructor por defecto.
     * Inicializa la rareza como una cadena vacía y llama al constructor de la superclase.
     */
    public Card() {
        super();
        this.rarity = "";
    }

    /**
     * Constructor con parámetros para inicializar una carta.
     * * @param rarity    Grado de rareza de la carta.
     * @param productId Identificador único del producto.
     * @param name      Nombre comercial de la carta.
     * @param price     Precio de venta unitario.
     * @param gameType  Tipo de juego al que pertenece (Pokémon, Magic, etc.).
     * @param stock     Unidades disponibles en el inventario.
     */
    public Card(String rarity, int productId, String name, double price, GameType gameType, int stock) {
        super(productId, name, price, gameType, stock);
        this.rarity = rarity;
    }

    /**
     * Obtiene la rareza de la carta.
     * @return El texto que describe la rareza.
     */
    public String getRarity() { 
        return rarity; 
    }

    /**
     * Establece la rareza de la carta.
     * @param rarity La nueva rareza a asignar.
     */
    public void setRarity(String rarity) { 
        this.rarity = rarity; 
    }
}