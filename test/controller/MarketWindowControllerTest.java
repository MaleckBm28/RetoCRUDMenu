package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import java.util.List;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MarketWindowControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Carga de la vista principal
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MarketWindow.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    public void test01_AyudaYReporte() {
        // 1. Probar Ayuda y cerrar diálogo
        clickOn("Help");
        clickOn("Ayuda");
        WaitForAsyncUtils.waitForFxEvents();
        try { clickOn("Aceptar"); } catch (Exception e) { clickOn("OK"); }

        // 2. Probar Reporte
        clickOn("Actions");
        clickOn("Report");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void test02_FiltrosAvanzados() {
        // Selección múltiple reabriendo el menú
        clickOn("#menuGameType");
        clickOn("MAGIC"); 
        clickOn("#menuGameType");
        clickOn("POKEMON");

        clickOn("#menuRarity");
        clickOn("Legendary");
        clickOn("#menuRarity");
        clickOn("Common");

        clickOn("BORRAR FILTROS");
    }

    @Test
    public void test03_ClickDerechoTodasLasOpciones() {
        // Lista exacta de opciones definidas en el controlador
        // Nota: Se incluye el espacio inicial que tiene cada String en el código fuente
        String[] opciones = {
            " Precio: Menor a Mayor",
            " Precio: Mayor a Menor",
            " Ordenar por Nombre (A-Z)",
            " Ordenar por Nombre (Z-A)",
            " Actualizar Stock (BD)"
        };

        for (String opcion : opciones) {
            rightClickOn("#grid"); // Abre el menú contextual
            clickOn(opcion);       // Selecciona la opción
            WaitForAsyncUtils.waitForFxEvents(); // Espera a que se procese la ordenación/recarga
        }
    }

    @Test
    public void test04_CompraYAbrirCarrito() {
        // 1. Añadir producto (ItemController)
        verifyThat("Add to Cart", isVisible());
        clickOn("Add to Cart");

        // 2. Cerrar Alert de confirmación
        WaitForAsyncUtils.waitForFxEvents();
        try { clickOn("Aceptar"); } catch (Exception e) { clickOn("OK"); }

        // 3. Abrir carrito con el ID específico
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#openCarrito"); 

        // 4. Verificación de ventana abierta
        WaitForAsyncUtils.waitForFxEvents();
        boolean encontrada = listWindows().stream()
            .filter(w -> w instanceof Stage)
            .map(w -> (Stage) w)
            .anyMatch(s -> s.getTitle() != null && s.getTitle().equalsIgnoreCase("Carrito"));

        assertTrue("La ventana del carrito no se abrió correctamente", encontrada);
    }
}