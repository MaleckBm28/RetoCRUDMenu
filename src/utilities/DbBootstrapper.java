package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbBootstrapper {

    private DbBootstrapper() {}

    // Usa los mismos datos que tu pool (NO tocamos ConnectionPool)
    private static final String USER = "root";
    private static final String PASS = "abcd*1234";

    // OJO: SIN /crud al final. Esto conecta al servidor, no a una BD concreta.
    private static final String SERVER_URL =
            "jdbc:mysql://localhost:3306/?serverTimezone=UTC&useSSL=false&allowMultiQueries=true";

    public static void ensureDatabaseExists(String dbName) {
        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASS);
             Statement st = conn.createStatement()) {

            st.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
            System.out.println("✅ BD asegurada: " + dbName);

        } catch (Exception e) {
            throw new RuntimeException("❌ No se pudo asegurar la BD " + dbName, e);
        }
    }
}
