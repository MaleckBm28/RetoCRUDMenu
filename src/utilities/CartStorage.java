package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import model.CartItem;

public class CartStorage {

    // Fichero en el home del usuario
    private static final Path FILE =
            Paths.get(System.getProperty("user.home"), "RetoCRUD_cart.csv");

    // ================== ADD (OBJETO COMPLEJO) ==================
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

    // ================== READ ==================
    public static List<CartItem> readAll() throws IOException {
        if (!Files.exists(FILE)) return new ArrayList<>();
        return readAllInternal();
    }

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

    // ================== WRITE ==================
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

    // ================== CLEAR ==================
    public static void clear() {
        try {
            Files.deleteIfExists(FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
