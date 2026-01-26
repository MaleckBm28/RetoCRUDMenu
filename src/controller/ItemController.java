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
    
    // 1. AÑADE ESTA LÍNEA (Asegúrate que coincide con el fx:id del FXML)
    @FXML private Label lblStock; 

    private Product product;

    public void setData(Product product) {
        this.product = product;

        // Datos básicos
        lblName.setText(product.getName());
        lblPrice.setText("€" + String.format("%.2f", product.getPrice()));

        // --- 2. LÓGICA NUEVA DE STOCK VISUAL ---
        
        int stockTotal = product.getStock();
        // Consultamos cuánto hay ya en el carrito para restarlo
        int enCarrito = getQuantityAlreadyInCart(product.getProductId());
        int disponiblesReal = stockTotal - enCarrito;

        // Actualizamos la etiqueta visual
        if (lblStock != null) {
            lblStock.setText("Stock: " + disponiblesReal);
        }

        // Configuramos el Spinner y el Botón según lo que REALMENTE queda
        if (disponiblesReal > 0) {
            // El máximo del spinner es lo que queda disponible, no el total absoluto
            spinnerAmount.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, disponiblesReal, 1)
            );
            spinnerAmount.setDisable(false);

            btnAdd.setDisable(false);
            btnAdd.setText("Add to Cart");
        } else {
            // Si disponiblesReal es 0 (aunque haya en BD, si está todo en el carrito) bloqueamos
            spinnerAmount.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0)
            );
            spinnerAmount.setDisable(true);
            btnAdd.setDisable(true);
            
            // Mensaje diferenciado si es por falta de stock o porque ya lo tienes todo
            if (stockTotal > 0) {
                 btnAdd.setText("En tu carrito");
            } else {
                 btnAdd.setText("Agotado");
            }
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
        if (qtySelectedObj == null || qtySelectedObj == 0) return; // Protección extra

        int qtySelected = qtySelectedObj;
        
        // Volvemos a calcular por seguridad (por si el usuario tardó mucho en clicar)
        int stockTotal = product.getStock();
        int alreadyInCart = getQuantityAlreadyInCart(product.getProductId());
        int available = stockTotal - alreadyInCart;

        if (available <= 0) {
            mostrarAlerta("Stock insuficiente", "No quedan unidades disponibles (revisa tu carrito).");
            // Refrescamos la vista visualmente por si acaso
            setData(this.product); 
            return;
        }

        if (qtySelected > available) {
            mostrarAlerta("Stock insuficiente", "Solo puedes añadir " + available + " más.");
            return;
        }

        try {
            CartStorage.add(product, qtySelected);
            
            mostrarAlertaInfo("Carrito", "Añadido: " + product.getName() + " x" + qtySelected);
            
            // --- 3. IMPORTANTE: REFRESCAR LA VISTA AL INSTANTE ---
            // Al llamar a setData de nuevo, se recalcula el stock restante y se actualiza el spinner/etiqueta
            setData(this.product);

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    // Métodos auxiliares para no repetir código de alertas
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