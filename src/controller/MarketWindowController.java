package controller;

import java.util.Comparator;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;

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

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import java.util.HashMap;
import java.sql.Connection;
import java.util.logging.Logger;
import pull.ConnectionPool;

public class MarketWindowController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(MarketWindowController.class.getName());
    
    @FXML
    private GridPane grid;
    @FXML
    private MenuButton menuGameType;
    @FXML
    private MenuButton menuRarity;

    private List<Product> allProducts = new ArrayList<>();
    private DBImplementation db = new DBImplementation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDataFromDatabase();
        setupFilters();
        setupContextMenu();
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
            String[] rarezas = {"Common", "Special" , "Rare", "Epic", "Legendary"};
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
        System.out.println(" Usuario pulsado");
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

    @FXML
    private void handleReport(ActionEvent event) {
        Connection con = null;
        
        LOGGER.info("El usuario ha solicitado generar el informe PDF.");
        
        try {
            System.out.println(" Generando informe...");

            // 1. Obtener conexión de TU Pool (importante para que tenga datos)
            con = ConnectionPool.getConnection();

            // 2. Definir parámetros (si tu informe los necesita, si no, mapa vacío)
            HashMap<String, Object> parameters = new HashMap<>();
            // Ejemplo: parameters.put("TipoJuego", "Magic"); 

            // 3. Cargar el diseño del informe (Compilamos el .jrxml al vuelo)
            // NOTA: Asegúrate de que el archivo 'MarketReport.jrxml' está en 'src/report/'
            JasperReport report = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("/report/MarketReport.jrxml")
            );

            // 4. Llenar el informe con datos de la BD
            JasperPrint print = JasperFillManager.fillReport(report, parameters, con);

            // 5. Mostrar el visor
            // El 'false' final es CRÍTICO: evita que al cerrar el informe se cierre toda la App
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Informe de Mercado");
            viewer.setVisible(true);

            LOGGER.info("Informe generado con éxito.");
        } catch (Exception e) {
            System.err.println(" Error al generar el informe:");
            e.printStackTrace();
            // Sería buena idea mostrar un Alert de error aquí
            LOGGER.severe("Error al compilar el informe: " + e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close(); // <--- Al ser un Pool, esto la "suelta" y la devuelve disponible
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    
    private void setupContextMenu() {
        // 1. Crear el Menú
        ContextMenu contextMenu = new ContextMenu();

        // 2. Opciones de ORDENACIÓN (Lo que le falta a tu barra)
        MenuItem sortNameAsc = new MenuItem(" Ordenar por Nombre (A-Z)");
        MenuItem sortNameDesc = new MenuItem(" Ordenar por Nombre (Z-A)");
        MenuItem sortPriceAsc = new MenuItem(" Precio: Menor a Mayor");
        MenuItem sortPriceDesc = new MenuItem(" Precio: Mayor a Menor");
        
        // 3. Opción de REFRESCO REAL (Conectar con BD de nuevo)
        MenuItem refreshDB = new MenuItem(" Actualizar Stock (BD)");

        // --- LÓGICA DE ORDENACIÓN ---
        sortNameAsc.setOnAction(e -> {
            allProducts.sort(Comparator.comparing(Product::getName));
            populateGrid(allProducts); // Repintamos ordenado
        });

        sortNameDesc.setOnAction(e -> {
            allProducts.sort(Comparator.comparing(Product::getName).reversed());
            populateGrid(allProducts);
        });

        sortPriceAsc.setOnAction(e -> {
            allProducts.sort(Comparator.comparing(Product::getPrice));
            populateGrid(allProducts);
        });

        sortPriceDesc.setOnAction(e -> {
            allProducts.sort(Comparator.comparing(Product::getPrice).reversed());
            populateGrid(allProducts);
        });

        // --- LÓGICA DE REFRESCO ---
        // Esto es útil si otro usuario compra algo y quieres ver el stock real YA
        refreshDB.setOnAction(e -> {
            System.out.println("Recargando datos desde la Base de Datos...");
            loadDataFromDatabase(); // Vuelve a hacer el SELECT a la BD
            // Opcional: Si quieres mantener los filtros visuales o borrarlos:
            // clearFilters(); 
        });

        // 4. Añadimos todo al menú con separadores bonitos
        contextMenu.getItems().addAll(
            sortPriceAsc, sortPriceDesc, 
            new SeparatorMenuItem(),
            sortNameAsc, sortNameDesc,
            new SeparatorMenuItem(), 
            refreshDB
        );

        // 5. Pegar el menú al Grid (Click Derecho)
        grid.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(grid, event.getScreenX(), event.getScreenY());
            } else {
                contextMenu.hide();
            }
        });

        // Truco para que funcione también si pinchas en el fondo blanco (fuera de las cartas)
        if (grid.getParent() != null) {
            grid.getParent().setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(grid, event.getScreenX(), event.getScreenY());
                    event.consume(); 
                } else {
                    contextMenu.hide();
                }
            });
        }
    }
}
