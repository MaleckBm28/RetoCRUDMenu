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
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

/**
 * Integration test for SignUp window starting from Login window. Usa la base de
 * datos real MySQL.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SignUpTest extends ApplicationTest {

    private static int uniqueUserId = 0;

    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(main.Main.class); // abre Login
    }

    @Override
    public void stop() throws Exception {
        FxToolkit.cleanupStages();
    }

    @Test
    public void test_SignUpFlow_RealMySQL_FromLogin() {
        clickOn("#Button_SignUp");
        FxAssert.verifyThat("#SignUpRoot", isVisible());

        String uniqueId = String.valueOf(System.currentTimeMillis() % 100000);

        clickOn("#textFieldEmail").write("pepegrillo" + uniqueId + "@example.com");
        clickOn("#textFieldName").write("TestName");
        clickOn("#textFieldSurname").write("TestSurname");
        clickOn("#textFieldTelephone").write("123456789");
        clickOn("#textFieldCardN").write("ZA9081726354891027364512");
        clickOn("#textFieldUsername").write("pepe" + uniqueId);
        clickOn("#textFieldPassword").write("pass123");
        clickOn("#textFieldCPassword").write("pass123");
        clickOn("#rButtonM");

        clickOn("#buttonSignUp");

        // Esperar a que se cargue el menú
        sleep(1500);

        FxAssert.verifyThat("#MenuRoot", isVisible());
    }
}