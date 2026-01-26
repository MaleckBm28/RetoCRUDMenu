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
import utilities.CartStorage;

public class ItemController {

    @FXML private ImageView imgProduct;
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private Spinner<Integer> spinnerAmount;
    @FXML private Button btnAdd;

    private Product product;

    public void setData(Product product) {
        this.product = product;

        // Datos básicos
        lblName.setText(product.getName());
        lblPrice.setText("€" + String.format("%.2f", product.getPrice()));

        // Spinner según stock TOTAL (el control fino lo hacemos al añadir)
        int stockActual = product.getStock();

        if (stockActual > 0) {
            spinnerAmount.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, stockActual, 1)
            );
            spinnerAmount.setDisable(false);

            btnAdd.setDisable(false);
            btnAdd.setText("Add to Cart");
        } else {
            spinnerAmount.setDisable(true);
            btnAdd.setDisable(true);
            btnAdd.setText("Sin Stock");
        }

        // Cargar imagen
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

    @FXML
    private void addToCart(ActionEvent event) {

        if (product == null) return;

        Integer qtySelectedObj = spinnerAmount.getValue();
        if (qtySelectedObj == null) return;

        int qtySelected = qtySelectedObj;

        int stockTotal = product.getStock();
        int alreadyInCart = getQuantityAlreadyInCart(product.getProductId());
        int available = stockTotal - alreadyInCart;

        // Si ya no queda stock disponible por lo que hay en el carrito
        if (available <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Stock insuficiente");
            alert.setHeaderText(null);
            alert.setContentText("No quedan unidades disponibles de:\n" + product.getName());
            alert.showAndWait();
            return;
        }

        // Si intenta añadir más de lo que queda disponible
        if (qtySelected > available) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Stock insuficiente");
            alert.setHeaderText(null);
            alert.setContentText(
                "Solo quedan " + available + " unidades disponibles de:\n" + product.getName()
            );
            alert.showAndWait();
            return;
        }

        // Guardar en fichero
        try {
            CartStorage.add(product, qtySelected);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Carrito");
            alert.setHeaderText(null);
            alert.setContentText("Añadido al carrito:\n" + product.getName() + " x" + qtySelected);
            alert.showAndWait();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo añadir al carrito");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private int getQuantityAlreadyInCart(int productId) {
        try {
            for (CartItem it : CartStorage.readAll()) {
                if (it.getProductId() == productId) {
                    return it.getQuantity();
                }
            }
        } catch (IOException e) {
            // Si falla leer el fichero, asumimos 0 para no bloquear la app
        }
        return 0;
    }
}
