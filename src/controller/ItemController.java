package controller;

import java.io.InputStream;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import model.CartItem;
import model.Product;
import model.Card;
import utilities.CartStorage;

/**
 * Controlador para los elementos individuales (items) que se muestran en el mercado.
 * Gestiona la visualización de la información de un producto, el control de stock 
 * disponible en tiempo real y la lógica para añadir unidades al carrito.
 * * @author Maleck
 * @version 1.0
 */
public class ItemController {

    @FXML private ImageView imgProduct;
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private Spinner<Integer> spinnerAmount;
    @FXML private Button btnAdd;
    @FXML private Label lblStock;

    /** Producto asociado a este controlador de item. */
    private Product product;

    /**
     * Configura los datos del producto en la vista del item.
     * Calcula el stock real disponible restando lo que ya se encuentra en el carrito
     * y configura el Spinner y el botón de añadir según la disponibilidad.
     * * @param product El objeto Product con la información a mostrar.
     */
    public void setData(Product product) {
        this.product = product;

        lblName.setText(product.getName());
        lblPrice.setText("€" + String.format("%.2f", product.getPrice()));

        int stockTotal = product.getStock();
        int enCarrito = getQuantityAlreadyInCart(product.getProductId());
        int disponiblesReal = stockTotal - enCarrito;

        if (lblStock != null) {
            lblStock.setText("Stock: " + disponiblesReal);
        }

        if (disponiblesReal > 0) {
            spinnerAmount.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, disponiblesReal, 1)
            );
            spinnerAmount.setDisable(false);
            btnAdd.setDisable(false);
            btnAdd.setText("Add to Cart");
        } else {
            spinnerAmount.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0)
            );
            spinnerAmount.setDisable(true);
            btnAdd.setDisable(true);
            btnAdd.setText(stockTotal > 0 ? "En tu carrito" : "Agotado");
        }

        try {
            String path = product.getImagePath();
            if (path != null && !path.isEmpty()) {
                InputStream stream = getClass().getResourceAsStream(path);
                if (stream != null) {
                    imgProduct.setImage(new Image(stream));
                }
            }
        } catch (Exception e) {
            System.err.println("Error imagen: " + e.getMessage());
        }
    }

    /**
     * Gestiona el evento de añadir el producto al carrito de compras.
     * Valida el stock actual y persiste el cambio en el almacenamiento local 
     * a través de {@link CartStorage}.
     * * @param event El evento de acción disparado por el botón.
     */
    @FXML
    private void addToCart(ActionEvent event) {
        if (product == null) return;

        Integer qtySelectedObj = spinnerAmount.getValue();
        if (qtySelectedObj == null || qtySelectedObj == 0) return;

        int qtySelected = qtySelectedObj;

        int stockTotal = product.getStock();
        int alreadyInCart = getQuantityAlreadyInCart(product.getProductId());
        int available = stockTotal - alreadyInCart;

        if (available <= 0) {
            mostrarAlerta("Stock insuficiente", "No quedan unidades disponibles (revisa tu carrito).");
            setData(this.product);
            return;
        }

        if (qtySelected > available) {
            mostrarAlerta("Stock insuficiente", "Solo puedes añadir " + available + " más.");
            return;
        }

        try {
            CartItem item = toCartItem(product, qtySelected);
            CartStorage.add(item);

            mostrarAlertaInfo("Carrito",
                    "Añadido: " + product.getName() + " x" + qtySelected);

            setData(this.product);

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    /**
     * Convierte un objeto de tipo Product a un objeto CartItem para su almacenamiento.
     * * @param p El producto original.
     * @param quantity La cantidad seleccionada por el usuario.
     * @return Un nuevo objeto CartItem con la información mapeada.
     */
    private CartItem toCartItem(Product p, int quantity) {
        String rarity = "-";
        if (p instanceof Card) {
            rarity = ((Card) p).getRarity();
        }

        return new CartItem(
            p.getProductId(),
            p.getName(),
            p.getPrice(),
            quantity,
            p.getClass().getSimpleName(),
            p.getGameType().name(),
            rarity,
            p.getImagePath()
        );
    }

    /**
     * Muestra una alerta de advertencia al usuario.
     * * @param titulo El título de la ventana.
     * @param contenido El mensaje informativo.
     */
    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de información al usuario.
     * * @param titulo El título de la ventana.
     * @param contenido El mensaje informativo.
     */
    private void mostrarAlertaInfo(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Consulta el almacenamiento para saber cuántas unidades de un producto 
     * específico ya han sido añadidas al carrito.
     * * @param productId Identificador único del producto.
     * @return Cantidad de unidades encontradas en el carrito.
     */
    private int getQuantityAlreadyInCart(int productId) {
        try {
            for (CartItem it : CartStorage.readAll()) {
                if (it.getProductId() == productId) {
                    return it.getQuantity();
                }
            }
        } catch (IOException e) {
            return 0;
        }
        return 0;
    }
}