/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewText;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import java.util.concurrent.TimeoutException;

import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;
import main.Main;
import org.testfx.api.FxToolkit;

/**
 *
 * @author acer
 */
public class DeleteAccountAdminTest extends ApplicationTest {

    public DeleteAccountAdminTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Main.class);
    }
    

    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Override
    public void start(Stage stage) throws Exception {
        FxToolkit.showStage();
    }

    private void performLogin(String username, String password) {
        clickOn("#TextField_Username");
        
        write(username);
        
        clickOn("#PasswordField_Password");
        write(password);
        
        clickOn("#Button_LogIn");
        
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_DeleteUserAsAdmin(){
        // login como admin
        performLogin("rluna", "zxcvbn");
        
        verifyThat("#label_Username", hasText("rluna"));
        
        // ir a ventana delete
        clickOn("#Button_Delete");
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // abrir combobox
        clickOn("#ComboBoxUser");
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // seleccionar segundo usuario
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // escribir password del admin
        clickOn("#TextFieldPassword");
        write("zxcvbn");
        
        // boton delete
        clickOn("#Button_Delete");
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // confirmar dialogo
        type(javafx.scene.input.KeyCode.ENTER);
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // cerrar mensaje de exito
        type(javafx.scene.input.KeyCode.ENTER);
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
