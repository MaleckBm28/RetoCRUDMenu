package main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.persistence.EntityManager;

import model.Admin;
import utilities.JPAUtil;
import utilities.SqlSeedRunner;

public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger("MarketApp");
    
    @Override
    public void start(Stage stage) throws Exception {
        
        try {
            // 1. Decimos dónde guardar el archivo (true = no borrar lo anterior, añadir al final)
            FileHandler fileHandler = new FileHandler("BitacoraMarket.log", true);
            
            // 2. Le ponemos formato simple (texto) en vez de XML para que sea legible
            fileHandler.setFormatter(new SimpleFormatter());
            
            // 3. Asignamos este manejador al Logger raíz (para que pille todo)
            Logger.getLogger("").addHandler(fileHandler);
            
            LOGGER.info(">>> INICIO DE LA APLICACIÓN <<<");
            
        } catch (IOException e) {
            System.err.println("No se pudo crear el archivo de log");
        }
        
        //Inicializar JPA -> Hibernate crea tablas
        EntityManager emInit = JPAUtil.em();
        emInit.close();

        //Cargar catálogo de productos (seed.sql)
        SqlSeedRunner.run("/seed.sql");

        ensureAdmin();

        Parent root = FXMLLoader.load(getClass().getResource("/view/MarketWindow.fxml"));
        Scene scene = new Scene(root);

        stage.setTitle("RetoCRUD");
        stage.setScene(scene);
        stage.show();
    }

    // NO TOCADO
    private void ensureAdmin() {
        EntityManager em = JPAUtil.em();
        try {
            Long count = em.createQuery("SELECT COUNT(a) FROM Admin a", Long.class)
                           .getSingleResult();

            if (count == 0) {
                em.getTransaction().begin();

                Admin admin = new Admin(
                        "CTA-000",
                        "admin",
                        "admin123",
                        "admin@crud.local",
                        0,
                        "Admin",
                        "000000000",
                        "Root"
                );

                em.persist(admin);
                em.getTransaction().commit();

                System.out.println("✅ Admin creado: admin / admin123");
            } else {
                System.out.println("ℹ️ Admin ya existe (count=" + count + ")");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void stop() {
        JPAUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
