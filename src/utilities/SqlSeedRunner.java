package utilities;

import pull.ConnectionPool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

public class SqlSeedRunner {

    private SqlSeedRunner() {}

    /**
     * Ejecuta un archivo SQL desde recursos (classpath) separando por ';'
     * Recomendado: poner seed.sql en src/ para que acabe en build/classes
     *
     * @param resourcePath ejemplo: "/seed.sql"
     */
    public static void run(String resourcePath) {

        InputStream in = SqlSeedRunner.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new RuntimeException("❌ No se encuentra el recurso: " + resourcePath
                    + " (pon seed.sql en src/)");
        }

        try (Connection conn = ConnectionPool.getConnection();
             Statement st = conn.createStatement();
             BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                String t = line.trim();

                // Saltar comentarios SQL y líneas vacías
                if (t.isEmpty() || t.startsWith("--") || t.startsWith("/*") || t.startsWith("*") || t.startsWith("*/")) {
                    continue;
                }

                sb.append(line).append("\n");

                // Ejecutar cuando detecte final de sentencia
                if (t.endsWith(";")) {
                    st.execute(sb.toString());
                    sb.setLength(0);
                }
            }

            // Si quedara algo sin ';' al final (por si acaso)
            if (sb.toString().trim().length() > 0) {
                st.execute(sb.toString());
            }

            System.out.println("✅ Seed ejecutado correctamente desde " + resourcePath);

        } catch (Exception e) {
            throw new RuntimeException("❌ Error ejecutando seed: " + resourcePath, e);
        }
    }
}
