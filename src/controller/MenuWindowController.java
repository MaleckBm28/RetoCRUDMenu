/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.Controller;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Admin;
import model.Profile;
import model.User;
import static org.hibernate.bytecode.BytecodeLogging.LOGGER;
import utilities.Session;

/**
 * Controller for the main Menu window. Handles navigation to modify, delete, and logout actions.
 */
public class MenuWindowController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger("LogInWindow");

    @FXML
    private Button Button_Delete;

    @FXML
    private Button Button_Modify;

    @FXML
    private Button Button_LogOut;

    @FXML
    private Label label_Username;

    @FXML
    private Button btnRestock;

    @FXML
    private Button btnBackToMarket;

    private model.Profile profile;
    private Controller cont;

    public void setUsuario(Profile profile) {
        this.profile = profile;
        label_Username.setText(profile.getUsername());
    }

    public void setProfile(model.Profile profile) {
        this.profile = profile;
        LOGGER.info("Sesión vinculada en el Market para el usuario: " + profile.getUsername());
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public Controller getCont() {
        return cont;
    }

    /**
     * Opens the Modify window.
     */
    @FXML
    private void modifyVentana(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ModifyWindow.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            controller.ModifyWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setProfile(profile);
            controllerWindow.setCont(this.cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) Button_Modify.getScene().getWindow();
            currentStage.close();

        } catch (IOException ex) {
            Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Opens the Delete Account window depending on profile type. Users open DeleteAccount; Admins open DeleteAccountAdmin.
     */
    @FXML
    private void delete() {
        try {
            FXMLLoader fxmlLoader;
            if (profile instanceof User) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/view/DeleteAccount.fxml"));
                javafx.scene.Parent root = fxmlLoader.load();
                controller.DeleteAccountController controllerWindow = fxmlLoader.getController();
                controllerWindow.setProfile(profile);
                controllerWindow.setCont(cont);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                Stage currentStage = (Stage) Button_Delete.getScene().getWindow();
                currentStage.close();

            } else if (profile instanceof Admin) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/view/DeleteAccountAdmin.fxml"));
                javafx.scene.Parent root = fxmlLoader.load();
                controller.DeleteAccountAdminController controllerWindow = fxmlLoader.getController();
                controllerWindow.setProfile(profile);
                controllerWindow.setCont(cont);
                controllerWindow.setComboBoxUser();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                Stage currentStage = (Stage) Button_Delete.getScene().getWindow();
                currentStage.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Closes the current window (used for logout).
     */
    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic if needed
    }

    // coger datos y revisar si es admin
    public void initData(model.Profile user) {
        this.profile = user; // Guardamos el perfil para usarlo después

        if (user instanceof model.Admin) {
            btnRestock.setVisible(true);
            if (btnBackToMarket != null) {
                btnBackToMarket.setVisible(false);
            }
        } else {
            btnRestock.setVisible(false);
            // El botón de volver al market solo es para usuarios normales
            if (btnBackToMarket != null) {
                btnBackToMarket.setVisible(true);
            }
        }
    }

    @FXML
    private void handleOpenRestock(javafx.event.ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/RestockWindow.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogOut(javafx.event.ActionEvent event) {
        try {
            LOGGER.info("Cerrando sesión del usuario...");

            // 1. Cargar la Tienda de nuevo (MarketWindow)
            // IMPORTANTE: Ahora apunta a MarketWindow, no a LogInWindow
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MarketWindow.fxml"));
            Parent root = loader.load();

            // 2. Mostrar la Tienda
            Stage stage = new Stage();
            stage.setTitle("Tienda de Cartas");
            stage.setScene(new Scene(root));
            stage.show();

            // 3. Cerrar el Menú de Usuario
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            LOGGER.info("Sesión cerrada. Usuario devuelto a la Tienda como invitado.");

        } catch (IOException e) {
            LOGGER.severe("Error al intentar volver a la tienda tras logout: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToMarket(ActionEvent event) {
        try {
            // IMPORTANTE: Convertimos Profile a User para que Session.setUser lo acepte
            if (this.profile instanceof model.User) {
                utilities.Session.setUser((model.User) this.profile);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MarketWindow.fxml"));
            Parent root = loader.load();

            // Ahora ya no dará error porque añadimos el método en el paso anterior
            MarketWindowController marketController = loader.getController();
            marketController.setProfile(this.profile);

            Stage stage = new Stage();
            stage.setTitle("Tienda de Cartas");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.severe("Error al volver al market: " + e.getMessage());
        }
    }

}
