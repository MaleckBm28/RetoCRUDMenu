package controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.CartItem;
import utilities.CartStorage;

public class CartWindowController implements Initializable {

    @FXML private VBox cartBox;
    @FXML private Label lblTotal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshCart();
    }

    private void refreshCart() {
        cartBox.getChildren().clear();

        double total = 0.0;

        try {
            List<CartItem> items = CartStorage.readAll();

            if (items.isEmpty()) {
                cartBox.getChildren().add(new Label("El carrito está vacío."));
                lblTotal.setText("Total: €0.00");
                return;
            }

            for (CartItem it : items) {
                HBox row = new HBox(15);
                Label name = new Label(it.getName());
                Label qty  = new Label("x" + it.getQuantity());
                Label price = new Label("€" + String.format("%.2f", it.getPrice()));

                row.getChildren().addAll(name, qty, price);
                cartBox.getChildren().add(row);

                total += it.getPrice() * it.getQuantity();
            }

            lblTotal.setText("Total: €" + String.format("%.2f", total));

        } catch (Exception e) {
            cartBox.getChildren().add(new Label("Error leyendo el carrito: " + e.getMessage()));
            e.printStackTrace();
            lblTotal.setText("Total: €0.00");
        }
    }

    @FXML
    private void goBack() {
        try {
            // Cerrar ventana actual (carrito)
            Stage currentStage = (Stage) cartBox.getScene().getWindow();
            currentStage.close();

            // Volver a abrir MarketWindow
            Parent root = FXMLLoader.load(getClass().getResource("/view/MarketWindow.fxml"));
            Stage stage = new Stage();
            stage.setTitle("RetoCRUD");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
