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
public class ModifyTest extends ApplicationTest {
    
    public ModifyTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Main.class);
    }
    
    @AfterClass
    public static void tearDownClass() {
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
    public void test_ModifyUserData(){
        // login
        performLogin("jlopez", "pass123");
        
        verifyThat("#label_Username", hasText("jlopez"));
        
        // ir a ventana modify
        clickOn("#Button_Modify");
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // modificar nombre
        clickOn("#TextField_Name");
        write("Juan Carlos");
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // modificar apellido
        clickOn("#TextField_Surname");
        write("Lopez Garcia");
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // modificar telefono
        clickOn("#TextField_Telephone");
        write("666777888");
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // boton save
        clickOn("#Button_SaveChanges");
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // cerrar dialogo de exito
        type(javafx.scene.input.KeyCode.ENTER);
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
