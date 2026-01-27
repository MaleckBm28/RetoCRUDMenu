package controller;

import dao.DBImplementation;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import model.GameType;
import model.Product;
import model.Card;

public class MarketWindowController implements Initializable {

    @FXML private GridPane grid;
    @FXML private MenuButton menuGameType;
    @FXML private MenuButton menuRarity;

    private List<Product> allProducts = new ArrayList<>();
    private DBImplementation db = new DBImplementation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDataFromDatabase();
        setupFilters();
    }

    private void loadDataFromDatabase() {
        try {
            allProducts = db.getAllProducts();
            populateGrid(allProducts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupFilters() {
        // --- A. FILTRO JUEGOS  ---
        if (menuGameType != null) {
            menuGameType.getItems().clear();
            for (GameType type : GameType.values()) {
                CheckMenuItem item = new CheckMenuItem(type.name());
                item.selectedProperty().addListener((obs, wasSelected, isSelected) -> applyFilters());
                menuGameType.getItems().add(item);
            }
        }

        // --- B. FILTRO RAREZAS  ---
        if (menuRarity != null) {
            menuRarity.getItems().clear();
            String[] rarezas = {"Common", "Rare", "Epic", "Legendary", "Special"};
            for (String r : rarezas) {
                CheckMenuItem item = new CheckMenuItem(r);
                item.selectedProperty().addListener((obs, wasSelected, isSelected) -> applyFilters());
                menuRarity.getItems().add(item);
            }
        }
    }

    // --- NUEVO MÉTODO: BORRAR FILTROS ---
    @FXML
    private void clearFilters() {
        // 1. Desmarcar todos los Juegos
        if (menuGameType != null) {
            for (MenuItem item : menuGameType.getItems()) {
                if (item instanceof CheckMenuItem) {
                    ((CheckMenuItem) item).setSelected(false);
                }
            }
        }

        // 2. Desmarcar todas las Rarezas
        if (menuRarity != null) {
            for (MenuItem item : menuRarity.getItems()) {
                if (item instanceof CheckMenuItem) {
                    ((CheckMenuItem) item).setSelected(false);
                }
            }
        }
        
        // 3. Forzar actualización (mostrar todo)
        applyFilters();
    }

    @FXML
    private void applyFilters() {
        List<GameType> selectedGames = new ArrayList<>();
        if (menuGameType != null) {
            for (MenuItem item : menuGameType.getItems()) {
                if (item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected()) {
                    selectedGames.add(GameType.valueOf(item.getText()));
                }
            }
        }

        List<String> selectedRarities = new ArrayList<>();
        if (menuRarity != null) {
            for (MenuItem item : menuRarity.getItems()) {
                if (item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected()) {
                    selectedRarities.add(item.getText());
                }
            }
        }

        List<Product> filteredList = allProducts.stream().filter(p -> {
            boolean matchGame = selectedGames.isEmpty() || selectedGames.contains(p.getGameType());
            boolean matchRarity;
            if (selectedRarities.isEmpty()) {
                matchRarity = true;
            } else {
                if (p instanceof Card) {
                    matchRarity = selectedRarities.contains(((Card) p).getRarity());
                } else {
                    matchRarity = false; 
                }
            }
            return matchGame && matchRarity;
        }).collect(Collectors.toList());

        populateGrid(filteredList);
    }

    private void populateGrid(List<Product> products) {
        grid.getChildren().clear();
        int column = 0;
        int row = 1;
        try {
            for (Product p : products) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/item.fxml")); // Revisa mayúsculas
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
    }
    
    
    @FXML
    private void handleHelp(ActionEvent event) {
        System.out.println("ℹ Abriendo ventana de Ayuda...");
        // AQUÍ pondremos luego el código para abrir el PDF o el Popup
        // showHelpPopup();
    }

    @FXML
    private void handleClose(ActionEvent event) {
        System.out.println("Cerrando aplicación...");
        Platform.exit(); // Cierra la app de forma elegante
        System.exit(0);
    }
}