package model;

import java.io.Serializable;

/**
 * Clase que representa un elemento individual dentro del carrito de compras.
 * Almacena la información del producto, la cantidad seleccionada y permite
 * calcular subtotales para la gestión de la preventa.
 * * @author Alex
 * @version 1.0
 */
public class CartItem implements Serializable {

    private int productId;
    private String name;
    private double price;
    private int quantity;

    private String productType;
    private String gameType;
    private String rarity;
    private String imagePath;

    /**
     * Constructor completo para crear un nuevo item del carrito.
     * * @param productId   Identificador único del producto.
     * @param name        Nombre del producto.
     * @param price       Precio unitario del producto.
     * @param quantity    Cantidad de unidades seleccionadas.
     * @param productType Categoría o clase del producto (Card, Pack, etc.).
     * @param gameType    Tipo de juego asociado (Pokémon, Magic, etc.).
     * @param rarity      Grado de rareza del artículo.
     * @param imagePath   Ruta relativa al recurso de la imagen.
     */
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

    /** @return El identificador del producto. */
    public int getProductId() {
        return productId;
    }

    /** @return El nombre del producto. */
    public String getName() {
        return name;
    }

    /** @return El precio unitario. */
    public double getPrice() {
        return price;
    }

    /** @return La cantidad de unidades en este item. */
    public int getQuantity() {
        return quantity;
    }

    /** @return El tipo de producto. */
    public String getProductType() {
        return productType;
    }

    /** @return El tipo de juego. */
    public String getGameType() {
        return gameType;
    }

    /** @return La rareza asignada. */
    public String getRarity() {
        return rarity;
    }

    /** @return La ruta de la imagen del producto. */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Incrementa la cantidad actual del item.
     * @param q Cantidad a sumar.
     */
    public void addQuantity(int q) {
        this.quantity += q;
    }

    /**
     * Calcula el subtotal del item multiplicando precio por cantidad.
     * @return El costo total acumulado de este item.
     */
    public double getSubtotal() {
        return price * quantity;
    }
}