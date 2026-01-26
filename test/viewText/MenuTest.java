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
public class MenuTest extends ApplicationTest {
    
     
    public MenuTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Main.class);
    }
    

    @Before
    public void setUp() {
        // El login se hace una sola vez en el primer test
    }
    
    @After
    public void tearDown() {
    }

    @Override
    public void start(Stage stage) throws Exception {
        FxToolkit.showStage();
    }

    // Método para realizar el login automático
    private void performLogin(String username, String password) {
         // Esperar a que los campos del login estén disponibles
        clickOn("#TextField_Username");
        
        write(username);
        
        clickOn("#PasswordField_Password");
        write(password);
        
        clickOn("#Button_LogIn");
        
        // Esperar a que se cargue la ventana del menú y se cierre la de login
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Test that performs complete flow: Login -> Delete Cancel -> Modify Cancel -> LogOut
    // Este es el test principal solicitado que simula el flujo completo del usuario
    @Test
    public void test_CompleteFlowLoginDeleteModifyLogout(){
        // Realizar el login con credenciales del archivo (rluna / zxcvbn)
        performLogin("rluna", "zxcvbn");
        
        // Verificar que se está en el menú principal
        verifyThat("#label_Username", hasText("rluna"));
        
        // Pulsar botón DELETE
        clickOn("#Button_Delete");
        
        // Esperar a que se abra la ventana de eliminar
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Hacer click en CANCEL dentro de la ventana DELETE
        clickOn("#Button_Cancel");
        
        // Esperar a volver al menú
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Verificar que estamos de vuelta en el menú
        verifyThat("#Button_Delete", hasText("Delete User"));
        
        // Pulsar botón MODIFY
        clickOn("#Button_Modify");
        
        // Esperar a que se abra la ventana de modificar
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Hacer click en CANCEL dentro de la ventana MODIFY
        clickOn("#Button_Cancel");
        
        // Esperar a volver al menú
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Verificar que estamos de vuelta en el menú
        verifyThat("#Button_Modify", hasText("Modify User"));
        
        // Pulsar botón LOG OUT
        clickOn("#Button_LogOut");
        
        // Esperar a que se cierre la ventana
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

