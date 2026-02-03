package controller;

import dao.DBImplementation;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.BoosterBox;
import model.Product;

/**
 * Controlador de Reposición.
 * CORREGIDO: Ahora al convertir cajas en sobres, SE RESTAN las cajas del inventario.
 */
public class RestockController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger("RestockWindow");
    
    @FXML private ComboBox<String> cmbGameType;
    @FXML private ComboBox<String> cmbAction;
    @FXML private TextField txtQuantity;
    
    private List<Product> allProducts; 
    private DBImplementation db; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new DBImplementation();
        
        try {
            allProducts = db.getAllProducts(); 
            LOGGER.info("Productos cargados. Cantidad: " + allProducts.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar productos", e);
        }
        
        cmbGameType.getItems().addAll("Pokemon", "Magic", "Yu-Gi-Oh", "Digimon", "Shadowverse");
        
        cmbAction.getItems().addAll(
            "Reponer Stock Cajas", 
            "Reponer Stock Sobres (Abriendo Cajas)", 
            "Abrir Sobres (Gacha Cartas)"
        );
    }

    @FXML
    private void handleExecute() {
        String game = cmbGameType.getValue();
        String action = cmbAction.getValue();
        String qtyStr = txtQuantity.getText();

        if (game == null || action == null || qtyStr.isEmpty()) {
            showError("Por favor, rellena todos los campos.");
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) throw new NumberFormatException();

            if (action.startsWith("Reponer Stock Cajas")) {
                restockBoxes(game, quantity);
            } else if (action.contains("Sobres (Abriendo Cajas)")) {
                restockPacksFromBoxes(game, quantity); // <--- MÉTODO CORREGIDO
            } else {
                openPacksGacha(game, quantity);
            }

        } catch (NumberFormatException e) {
            showError("La cantidad debe ser un número entero positivo.");
        }
    }

    // --- 1. AÑADIR CAJAS (COMPRA A PROVEEDOR) ---
    private void restockBoxes(String game, int qty) {
        boolean found = false;
        String gameClean = cleanGameName(game);

        for (Product p : allProducts) {
            if (isGame(p, gameClean) && isBox(p)) {
                
                int nuevoStock = p.getStock() + qty;
                p.setStock(nuevoStock);
                db.updateProduct(p);
                
                found = true;
                LOGGER.info("ADMIN: Compradas " + qty + " cajas de " + game);
                showInfo("Stock Actualizado", "Has añadido " + qty + " cajas de " + game + ".\nTotal actual: " + nuevoStock);
            }
        }
        if (!found) showError("No se encontró el producto 'Caja' para " + game);
    }

    // --- 2. CONVERTIR CAJAS EN SOBRES (CORREGIDO) ---
    private void restockPacksFromBoxes(String game, int boxQty) {
        String gameClean = cleanGameName(game);
        
        // A. BUSCAR LA CAJA Y RESTAR STOCK
        Product productoCaja = null;
        int sobresPorCaja = 36; // Valor por defecto

        for (Product p : allProducts) {
            if (isGame(p, gameClean) && isBox(p)) {
                productoCaja = p;
                if (p instanceof BoosterBox) {
                    sobresPorCaja = ((BoosterBox) p).getPackAmount();
                }
                break;
            }
        }

        if (productoCaja == null) {
            showError("No encuentro la Caja de " + game + " en la base de datos.");
            return;
        }

        // --- VALIDACIÓN Y RESTA (IMPORTANTE) ---
        if (productoCaja.getStock() < boxQty) {
            showError("¡Error de Stock! Quieres abrir " + boxQty + " cajas, pero solo tienes " + productoCaja.getStock() + " en el almacén.");
            return;
        }

        // 1. RESTAMOS LAS CAJAS
        productoCaja.setStock(productoCaja.getStock() - boxQty);
        db.updateProduct(productoCaja); // Guardamos la resta en BD
        LOGGER.info("ADMIN: Restadas " + boxQty + " cajas del inventario para abrir sobres.");


        // B. BUSCAR EL SOBRE Y SUMAR STOCK
        boolean foundPack = false;
        int totalSobresToAdd = boxQty * sobresPorCaja;

        for (Product p : allProducts) {
            if (isGame(p, gameClean) && isPack(p)) {
                
                // 2. SUMAMOS LOS SOBRES
                p.setStock(p.getStock() + totalSobresToAdd);
                db.updateProduct(p); // Guardamos la suma en BD
                
                foundPack = true;
                LOGGER.info("ADMIN: Añadidos " + totalSobresToAdd + " sobres al inventario.");
                
                showInfo("Conversión Realizada", 
                    "Se han abierto " + boxQty + " cajas del almacén.\n" +
                    "(Stock cajas restante: " + productoCaja.getStock() + ")\n\n" +
                    "Se han generado " + totalSobresToAdd + " sobres nuevos.");
            }
        }
        
        if (!foundPack) {
            // Si falla al encontrar el sobre, deberíamos devolver las cajas (Rollback manual básico)
            productoCaja.setStock(productoCaja.getStock() + boxQty);
            db.updateProduct(productoCaja);
            showError("Error crítico: Se restaron las cajas pero no encuentro el producto 'Sobre' para sumar el stock. Se ha deshecho la operación.");
        }
    }

    // --- 3. ABRIR SOBRES (GACHA) ---
    private void openPacksGacha(String game, int numPacks) {
        // Validación previa: ¿Tenemos sobres para abrir?
        // (Opcional: Si quieres que esto gaste stock de sobres físicos, añade la lógica aquí)
        
        Random rand = new Random();
        Map<String, Integer> cartasObtenidas = new HashMap<>(); 
        String gameClean = cleanGameName(game);
        
        List<Product> cartasPosibles = allProducts.stream()
                .filter(p -> isGame(p, gameClean) && !isBox(p) && !isPack(p))
                .collect(Collectors.toList());

        if (cartasPosibles.isEmpty()) {
            showError("No hay cartas registradas para " + game);
            return;
        }

        for (int i = 0; i < numPacks; i++) {
            for (int c = 0; c < 5; c++) {
                Product carta = cartasPosibles.get(rand.nextInt(cartasPosibles.size()));
                
                carta.setStock(carta.getStock() + 1);
                db.updateProduct(carta);
                
                cartasObtenidas.put(carta.getName(), cartasObtenidas.getOrDefault(carta.getName(), 0) + 1);
            }
        }

        StringBuilder resumen = new StringBuilder("Has abierto " + numPacks + " sobres.\n\nCartas obtenidas:\n");
        cartasObtenidas.forEach((k, v) -> resumen.append("- ").append(v).append("x ").append(k).append("\n"));
        
        showInfo("¡Apertura Completada!", resumen.toString());
    }

    // --- MÉTODOS DE AYUDA ---
    
    private String cleanGameName(String game) {
        return game.replace("-", "").toUpperCase();
    }

    private boolean isGame(Product p, String gameClean) {
        return p.getGameType().name().equalsIgnoreCase(gameClean);
    }

    private boolean isBox(Product p) {
        return p instanceof BoosterBox || p.getName().toLowerCase().contains("caja") || p.getName().toLowerCase().contains("box");
    }

    private boolean isPack(Product p) {
        return (p.getName().toLowerCase().contains("sobre") || p.getName().toLowerCase().contains("pack")) 
               && !p.getName().toLowerCase().contains("box");
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        TextArea area = new TextArea(content);
        area.setEditable(false);
        area.setWrapText(true);
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.show();
    }
}