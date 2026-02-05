package model;

import javax.persistence.*;

/**
 * Clase base abstracta que representa un producto dentro del sistema.
 * Utiliza una estrategia de herencia de tabla unida (JOINED) para mapear
 * las diferentes categorías de productos (Cartas, Sobres, Cajas) en la base de datos.
 * * @author Alex
 * @version 1.0
 */
@Entity
@Table(name = "PRODUCT")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Product {

    /** Identificador único del producto. */
    @Id
    @Column(name = "PRODUCT_ID")
    private int productId;

    /** Nombre comercial del producto. */
    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    /** Precio unitario del producto. */
    @Column(name = "PRICE", nullable = false)
    private double price;

    /** Tipo de juego de cartas coleccionables al que pertenece el producto. */
    @Enumerated(EnumType.STRING)
    @Column(name = "GAME_TYPE", length = 30)
    private GameType gameType;

    /** Cantidad de unidades disponibles en el inventario. */
    @Column(name = "STOCK")
    private int stock;

    /** Ruta relativa al recurso de imagen del producto. */
    @Column(name = "IMAGE_PATH", length = 255)
    private String imagePath;

    /**
     * Constructor por defecto.
     * Asigna una imagen predeterminada por si el producto no tiene una ruta específica.
     */
    public Product() {
        this.imagePath = "/images/default_card.png";
    }

    /**
     * Constructor con parámetros esenciales para crear un producto.
     * * @param productId Identificador único del producto.
     * @param name      Nombre del producto.
     * @param price     Precio de venta.
     * @param gameType  Categoría de juego asociada.
     * @param stock     Existencias iniciales en inventario.
     */
    public Product(int productId, String name, double price, GameType gameType, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.gameType = gameType;
        this.stock = stock;
    }

    // Getters y Setters

    /** @return El identificador del producto. */
    public int getProductId() { return productId; }
    /** @param productId El identificador a establecer. */
    public void setProductId(int productId) { this.productId = productId; }

    /** @return El nombre del producto. */
    public String getName() { return name; }
    /** @param name El nombre a establecer. */
    public void setName(String name) { this.name = name; }

    /** @return El precio del producto. */
    public double getPrice() { return price; }
    /** @param price El precio a establecer. */
    public void setPrice(double price) { this.price = price; }

    /** @return El tipo de juego asociado. */
    public GameType getGameType() { return gameType; }
    /** @param gameType El tipo de juego a establecer. */
    public void setGameType(GameType gameType) { this.gameType = gameType; }

    /** @return El stock disponible. */
    public int getStock() { return stock; }
    /** @param stock La cantidad de stock a establecer. */
    public void setStock(int stock) { this.stock = stock; }

    /** @return La ruta de la imagen. */
    public String getImagePath() { return imagePath; }
    /** @param imagePath La nueva ruta de imagen a establecer. */
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}