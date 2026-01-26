package controller;

import dao.DBImplementation;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.GameType;
import model.Product;

public class MarketWindowController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private ComboBox<GameType> comboGameType;

    private List<Product> allProducts = new ArrayList<>();
    private DBImplementation db = new DBImplementation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboGameType.getItems().addAll(GameType.values());
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            allProducts = db.getAllProducts();
            populateGrid(allProducts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void reloadMarket() {
        GameType selected = comboGameType.getValue();
        if (selected == null) {
            populateGrid(allProducts);
            return;
        }

        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getGameType() == selected) {
                filtered.add(p);
            }
        }
        populateGrid(filtered);
    }

    private void populateGrid(List<Product> products) {
        grid.getChildren().clear();
        int column = 0;
        int row = 1;

        try {
            for (Product p : products) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Item.fxml"));
                AnchorPane pane = loader.load();

                ItemController controller = loader.getController();
                controller.setData(p);

                if (column == 4) {
                    column = 0;
                    row++;
                }

                grid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ================= ICONOS ================= */
    @FXML
    private void openCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CartWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Carrito");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openUser() {
        System.out.println("👤 Usuario pulsado");
        // más adelante
    }
}
