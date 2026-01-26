/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewText;

import java.util.concurrent.TimeoutException;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

/**
 * Integration test for LogInWindowController using TestFX. Covers initial
 * state, button logic, login flow, and SignUp window.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogInTest extends ApplicationTest {

    @Override
    public void stop() {
    }

    /**
     * Inicializa la aplicación JavaFX antes de los tests.
     */
    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(main.Main.class);
        // Ejemplo: FxToolkit.setupApplication(MyApplication.class);
    }

    /**
     * Comprueba el estado inicial de la ventana de login.
     */
    @Test
    public void test1_InitialState() {
        verifyThat("#TextField_Username", hasText(""));
        verifyThat("#PasswordField_Password", hasText(""));
        verifyThat("#Button_LogIn", isEnabled());
    }

    /**
     * Comprueba el flujo completo de login: 1. Introduce credenciales
     * incorrectas → muestra error. 2. Corrige la contraseña → inicia sesión
     * correctamente.
     */
    @Test
    public void test4_LoginFlow_IncorrectThenCorrect() {
        // 1️⃣ Usuario y contraseña incorrectos
        clickOn("#TextField_Username");
        write("mramirez");
        clickOn("#PasswordField_Password");
        write("wrongpass");
        clickOn("#Button_LogIn");

        // Verifica que aparece mensaje de error
        verifyThat("#labelIncorrecto", isVisible());

        // 2️⃣ Borrar contraseña y escribir la correcta
        clickOn("#PasswordField_Password");
        eraseText(9); // borra "wrongpass"
        write("pass456");

        // Reintentar login
        clickOn("#Button_LogIn");

        // Verifica que se abre el menú principal
        verifyThat("#MenuRoot", isVisible());
    }

    @Test
    public void test_SignUpWindow_OpensIndependently() throws TimeoutException {
        // Reinicia la aplicación para asegurar ventana limpia
        FxToolkit.cleanupStages();
        FxToolkit.setupApplication(main.Main.class);

        clickOn("#Button_SignUp");
        verifyThat("#SignUpRoot", isVisible());
    }
}
