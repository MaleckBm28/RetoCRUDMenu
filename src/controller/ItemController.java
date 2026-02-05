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

public class ItemController {

    @FXML private ImageView imgProduct;
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private Spinner<Integer> spinnerAmount;
    @FXML private Button btnAdd;
    @FXML private Label lblStock;

    private Product product;

    // ================== SET DATA ==================
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

    // ================== ADD TO CART ==================
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
            // 🔥 PASO 2: Product → CartItem (OBJETO COMPLEJO)
            CartItem item = toCartItem(product, qtySelected);

            CartStorage.add(item);

            mostrarAlertaInfo("Carrito",
                    "Añadido: " + product.getName() + " x" + qtySelected);

            setData(this.product);

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    // ================== PASO 2 ==================
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
            p.getClass().getSimpleName(),   // Card / BoosterPack / BoosterBox
            p.getGameType().name(),
            rarity,
            p.getImagePath()
        );
    }

    // ================== ALERTAS ==================
    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarAlertaInfo(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    // ================== CANTIDAD EN CARRITO ==================
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
