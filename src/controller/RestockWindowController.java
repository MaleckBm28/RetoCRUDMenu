package controller;

import dao.DBImplementation; // O tu interfaz
import java.net.URL;
import java.util.ArrayList;
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
import model.Product;

public class RestockController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger("RestockLog");
    
    @FXML private ComboBox<String> cmbGameType;
    @FXML private ComboBox<String> cmbAction; // "Añadir Stock Cajas", "Abrir Sobres"
    @FXML private TextField txtQuantity;
    
    // Necesitamos cargar los productos para saber qué cartas pueden tocar
    private List<Product> allProducts; 
    private DBImplementation db; // Tu clase de base de datos

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new DBImplementation();
        allProducts = db.getAllProducts(); // Cargar todo al inicio
        
        // Llenar combos
        cmbGameType.getItems().addAll("Pokemon", "Magic", "Yu-Gi-Oh");
        cmbAction.getItems().addAll("Reponer Cajas", "Abrir Sobres");
        
        LOGGER.info("Ventana de Reposición abierta.");
    }

    @FXML
    private void handleExecute() {
        String game = cmbGameType.getValue();
        String action = cmbAction.getValue();
        String qtyStr = txtQuantity.getText();

        if (game == null || action == null || qtyStr.isEmpty()) {
            showError("Por favor rellena todos los campos");
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) throw new NumberFormatException();

            if (action.equals("Reponer Cajas")) {
                restockBoxes(game, quantity);
            } else {
                openPacks(game, quantity);
            }

        } catch (NumberFormatException e) {
            showError("La cantidad debe ser un número entero positivo.");
        }
    }

    // --- OPCIÓN 1: REPONER CAJAS (Sencillo) ---
    private void restockBoxes(String game, int qty) {
        // Buscamos el producto "Caja" de ese juego
        // OJO: Asumo que tienes un producto que se llama "Caja Pokemon" o similar
        // Si no, tendrás que buscar por ID o lógica específica.
        boolean found = false;
        for (Product p : allProducts) {
            if (p.getGameType().equalsIgnoreCase(game) && p.getName().contains("Caja")) {
                // Actualizar BD
                // db.updateStock(p.getId(), p.getStock() + qty); 
                // Actualizar local para no recargar
                p.setStock(p.getStock() + qty);
                found = true;
                LOGGER.info("Añadidas " + qty + " cajas de " + game);
            }
        }
        
        if (found) showInfo("Stock Actualizado", "Se han añadido " + qty + " cajas al inventario.");
        else showError("No se encontró el producto 'Caja' para " + game);
    }

    // --- OPCIÓN 2: ABRIR SOBRES (La lógica compleja) ---
    private void openPacks(String game, int numPacks) {
        Random rand = new Random();
        Map<String, Integer> cartasObtenidas = new HashMap<>(); // Para contar repetidas: "Squirtle" -> 2
        
        // 1. Filtrar todas las cartas posibles de ese juego
        List<Product> cartasDelJuego = allProducts.stream()
                .filter(p -> p.getGameType().equalsIgnoreCase(game) && !p.getName().contains("Caja") && !p.getName().contains("Sobre"))
                .collect(Collectors.toList());

        if (cartasDelJuego.isEmpty()) {
            showError("No hay cartas registradas para " + game);
            return;
        }

        // 2. Bucle por cada sobre
        for (int i = 0; i < numPacks; i++) {
            
            // Restar 1 al stock de Sobres (Si lo tienes controlado)
            // db.reduceStockSobre(game, 1); 

            // 3. Bucle por las 5 cartas del sobre
            for (int c = 0; c < 5; c++) {
                // DETERMINAR RAREZA (Probabilidad)
                String targetRarity = getRarityByChance(rand.nextInt(100));
                
                // Buscar cartas que coincidan con esa rareza
                // NOTA: Asumo que en tu BD tienes un campo 'rarity' o lo sacas del nombre
                // Si no tienes campo rareza, elige una carta totalmente al azar.
                List<Product> candidates = cartasDelJuego; // .filter(p -> p.getRarity().equals(targetRarity))
                
                if (!candidates.isEmpty()) {
                    Product cartaGanadora = candidates.get(rand.nextInt(candidates.size()));
                    
                    // Aumentar stock de la carta (Lógica de negocio)
                    // db.updateStock(cartaGanadora.getId(), cartaGanadora.getStock() + 1);
                    cartaGanadora.setStock(cartaGanadora.getStock() + 1);

                    // Guardar para el resumen visual
                    String key = cartaGanadora.getName() + " (" + targetRarity + ")";
                    cartasObtenidas.put(key, cartasObtenidas.getOrDefault(key, 0) + 1);
                }
            }
        }

        // 4. Generar el reporte (Pop-up)
        StringBuilder resumen = new StringBuilder("Has abierto " + numPacks + " sobres:\n\n");
        cartasObtenidas.forEach((nombre, cantidad) -> {
            resumen.append(cantidad).append("x ").append(nombre).append("\n");
        });

        LOGGER.info("Sobres abiertos. Cartas generadas: " + cartasObtenidas.size());
        showInfo("¡Apertura Completada!", resumen.toString());
    }

    // --- LÓGICA DE PROBABILIDAD ---
    private String getRarityByChance(int roll) {
        // 0-59 (60%) -> Común
        // 60-89 (30%) -> Infrecuente
        // 90-99 (10%) -> Rara
        if (roll < 60) return "Comun";
        if (roll < 90) return "Infrecuente";
        return "Rara";
    }

    // --- ALERTAS ---
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Permitir scroll si el texto es muy largo (muchas cartas)
        TextArea area = new TextArea(content);
        area.setEditable(false);
        area.setWrapText(true);
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}