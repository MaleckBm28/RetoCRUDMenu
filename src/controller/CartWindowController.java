package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Stage;

import model.CartItem;
import model.User;
import utilities.CartStorage;
import utilities.Session;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import model.Product;
import model.Purchase;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import utilities.JPAUtil;

/**
 * Controlador para la ventana del Carrito de Compras.
 * Esta clase gestiona la visualización de los productos seleccionados, 
 * el cálculo de totales, la persistencia de compras en la base de datos 
 * y la generación de reportes de preventa.
 * * @author Alex
 * @version 1.0
 */
public class CartWindowController implements Initializable {

    @FXML
    private TableView<CartItem> tableCart;

    @FXML
    private TableColumn<CartItem, String> colImage;
    @FXML
    private TableColumn<CartItem, String> colName;
    @FXML
    private TableColumn<CartItem, String> colType;
    @FXML
    private TableColumn<CartItem, String> colGame;
    @FXML
    private TableColumn<CartItem, String> colRarity;
    @FXML
    private TableColumn<CartItem, Integer> colQty;
    @FXML
    private TableColumn<CartItem, Double> colPrice;
    @FXML
    private TableColumn<CartItem, Double> colSubtotal;

    @FXML
    private Label lblTotal;

    @FXML
    private Button btnBuy;

    /** Menú contextual para interactuar con la tabla de productos. */
    private ContextMenu contextMenu;

    /**
     * Inicializa el controlador. Configura las columnas de la tabla, 
     * el menú contextual y carga los elementos almacenados en el carrito.
     * * @param location Ubicación relativa para el objeto raíz.
     * @param resources Recursos para localizar el objeto raíz.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTableColumns();
        initContextMenu();
        loadCart();

        // 🔁 cuando la ventana se muestre o recupere foco → actualizar botón
        tableCart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsW, oldW, newW) -> {
                    if (newW != null) {
                        newW.setOnShown(e -> updateBuyButton());
                        newW.focusedProperty().addListener((o, was, is) -> {
                            if (is) {
                                updateBuyButton();
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Configura las fábricas de valores para las columnas de la TableView.
     */
    private void initTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("productType"));
        colGame.setCellValueFactory(new PropertyValueFactory<>("gameType"));
        colRarity.setCellValueFactory(new PropertyValueFactory<>("rarity"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        setupImageColumn();
    }

    /**
     * Configura la celda personalizada para mostrar la imagen del producto en la tabla.
     */
    private void setupImageColumn() {
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        colImage.setCellFactory(col -> new TableCell<CartItem, String>() {
            private final ImageView iv = new ImageView();
            {
                iv.setFitWidth(40);
                iv.setFitHeight(40);
                iv.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null || path.trim().isEmpty()) {
                    setGraphic(null);
                    return;
                }
                try {
                    iv.setImage(new Image(getClass().getResourceAsStream(path)));
                    setGraphic(iv);
                } catch (Exception ex) {
                    setGraphic(null);
                }
            }
        });
    }

    /**
     * Lee los artículos del almacenamiento temporal, los carga en la tabla 
     * y actualiza el Label del costo total.
     */
    private void loadCart() {
        try {
            List<CartItem> items = CartStorage.readAll();
            tableCart.getItems().setAll(items);

            double total = 0.0;
            for (CartItem it : items) {
                total += it.getSubtotal();
            }

            lblTotal.setText("Total: €" + String.format("%.2f", total));
        } catch (Exception e) {
            lblTotal.setText("Total: €0.00");
        }
        updateBuyButton();
    }

    /**
     * Habilita o deshabilita el botón de compra según el estado de la sesión 
     * y el contenido del carrito.
     */
    private void updateBuyButton() {
        boolean enabled = Session.isLoggedIn() && !tableCart.getItems().isEmpty();
        btnBuy.setDisable(!enabled);
    }

    /**
     * Procesa la compra de los artículos. Valida la sesión, la tarjeta de crédito 
     * asociada al usuario y actualiza el stock en la base de datos mediante JPA.
     */
    @FXML
    private void buy() {
        if (!Session.isLoggedIn()) {
            showAlert("Debes iniciar sesión para realizar una compra");
            updateBuyButton();
            return;
        }

        if (tableCart.getItems().isEmpty()) {
            showAlert("El carrito está vacío");
            updateBuyButton();
            return;
        }

        User user = (User) Session.getUser();
        String storedCard = user.getCardNumber();

        if (storedCard == null || storedCard.trim().isEmpty()) {
            showAlert("El usuario no tiene una tarjeta asociada");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Confirmar compra");
        dialog.setHeaderText("Introduce tu número de tarjeta");
        dialog.setContentText("Tarjeta:");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return;
        }

        if (!result.get().trim().equals(storedCard)) {
            showAlert("El número de tarjeta no es correcto");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar compra");
        confirm.setHeaderText("¿Deseas confirmar la compra?");
        confirm.setContentText("Usuario: " + user.getUsername());

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            EntityManager em = JPAUtil.em();
            try {
                em.getTransaction().begin();
                for (CartItem item : tableCart.getItems()) {
                    Product product = em.find(Product.class, item.getProductId());
                    if (product == null) {
                        throw new RuntimeException("Producto no encontrado");
                    }

                    int newStock = product.getStock() - item.getQuantity();
                    if (newStock < 0) {
                        throw new RuntimeException("Stock insuficiente para " + product.getName());
                    }

                    product.setStock(newStock);
                    em.merge(product);

                    Purchase purchase = new Purchase();
                    purchase.setProduct(product);
                    purchase.setUser(user);
                    purchase.setPurchaseDate(LocalDate.now());
                    em.persist(purchase);
                }
                em.getTransaction().commit();
                CartStorage.clear();
                loadCart();
                showAlert("Compra realizada correctamente");
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                showAlert("Error al realizar la compra");
                ex.printStackTrace();
            } finally {
                em.close();
            }
        }
    }

    /**
     * Inicializa las opciones del menú contextual de la tabla.
     */
    private void initContextMenu() {
        contextMenu = new ContextMenu();
        MenuItem reload = new MenuItem("🔄 Recargar");
        reload.setOnAction(e -> loadCart());

        MenuItem clear = new MenuItem("🧹 Vaciar carrito");
        clear.setOnAction(e -> {
            CartStorage.clear();
            loadCart();
        });
        contextMenu.getItems().addAll(reload, clear);
    }

    /**
     * Muestra el menú contextual en la posición del cursor sobre la tabla.
     * @param e Evento de menú contextual.
     */
    @FXML
    private void showContextMenu(ContextMenuEvent e) {
        contextMenu.hide();
        contextMenu.show((Node) e.getSource(), e.getScreenX(), e.getScreenY());
    }

    /**
     * Cierra la ventana actual y regresa a la ventana anterior.
     */
    @FXML
    private void goBack() {
        ((Stage) tableCart.getScene().getWindow()).close();
    }

    /**
     * Finaliza la aplicación.
     */
    @FXML
    private void handleClose() {
        Platform.exit();
    }

    /**
     * Muestra un mensaje informativo de ayuda en consola.
     */
    @FXML
    private void handleHelp() {
        System.out.println("Ayuda pulsada desde carrito");
    }

    /**
     * Genera y muestra un reporte en formato Jasper con el contenido actual del carrito.
     */
    @FXML
    private void handleReport() {
        if (tableCart.getItems().isEmpty()) {
            showAlert("El carrito está vacío");
            return;
        }

        try {
            JasperReport report = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("/report/CartReport.jrxml")
            );

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(tableCart.getItems());
            JasperPrint print = JasperFillManager.fillReport(report, null, dataSource);
            JasperViewer.viewReport(print, false);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error al generar el reporte");
        }
    }

    /**
     * Muestra una alerta informativa al usuario.
     * @param msg Mensaje a mostrar.
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}