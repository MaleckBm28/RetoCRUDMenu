package main;

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

    @Override
    public void start(Stage stage) throws Exception {

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
