package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import model.CartItem;

/**
 * Clase de utilidad para la gestión del almacenamiento persistente del carrito.
 * Utiliza un archivo CSV ubicado en el directorio personal del usuario para
 * guardar, leer y modificar los artículos seleccionados durante la sesión.
 * * @author Alex
 * @version 1.0
 */
public class CartStorage {

    /** Ruta del fichero CSV en el home del usuario. */
    private static final Path FILE =
            Paths.get(System.getProperty("user.home"), "RetoCRUD_cart.csv");

    /**
     * Añade un artículo al carrito. Si el producto ya existe en el archivo,
     * incrementa su cantidad. En caso contrario, lo añade como una nueva línea.
     * * @param newItem El objeto {@link CartItem} que se desea añadir.
     * @throws IOException Si ocurre un error al leer o escribir en el archivo.
     */
    public static void add(CartItem newItem) throws IOException {

        List<CartItem> items = readAllInternal();
        boolean found = false;

        for (CartItem it : items) {
            if (it.getProductId() == newItem.getProductId()) {
                it.addQuantity(newItem.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) {
            items.add(newItem);
        }

        writeAll(items);
    }

    /**
     * Recupera todos los artículos almacenados en el carrito.
     * * @return Una lista de objetos {@link CartItem}. Si el archivo no existe, devuelve una lista vacía.
     * @throws IOException Si ocurre un error de lectura.
     */
    public static List<CartItem> readAll() throws IOException {
        if (!Files.exists(FILE)) return new ArrayList<>();
        return readAllInternal();
    }

    /**
     * Proceso interno de lectura del archivo CSV.
     * Parsea cada línea separada por puntos y comas para reconstruir los objetos.
     * * @return Lista de items procesados.
     * @throws IOException Si el archivo no es accesible.
     */
    private static List<CartItem> readAllInternal() throws IOException {

        List<CartItem> out = new ArrayList<>();
        if (!Files.exists(FILE)) return out;

        try (BufferedReader br = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] p = line.split(";", -1);

                int productId = Integer.parseInt(p[0]);
                String name = p[1];
                double price = Double.parseDouble(p[2]);
                int quantity = Integer.parseInt(p[3]);
                String productType = p[4];
                String gameType = p[5];
                String rarity = p[6];
                String imagePath = p[7];

                out.add(new CartItem(
                    productId,
                    name,
                    price,
                    quantity,
                    productType,
                    gameType,
                    rarity,
                    imagePath
                ));
            }
        }
        return out;
    }

    /**
     * Sobrescribe el archivo CSV con la lista actualizada de artículos.
     * * @param items Lista de elementos a persistir.
     * @throws IOException Si hay errores en la escritura.
     */
    private static void writeAll(List<CartItem> items) throws IOException {

        try (BufferedWriter bw = Files.newBufferedWriter(
                FILE,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (CartItem it : items) {
                bw.write(
                    it.getProductId() + ";" +
                    it.getName() + ";" +
                    it.getPrice() + ";" +
                    it.getQuantity() + ";" +
                    it.getProductType() + ";" +
                    it.getGameType() + ";" +
                    it.getRarity() + ";" +
                    it.getImagePath()
                );
                bw.newLine();
            }
        }
    }

    /**
     * Elimina físicamente el archivo del carrito, vaciando todos sus elementos.
     */
    public static void clear() {
        try {
            Files.deleteIfExists(FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}