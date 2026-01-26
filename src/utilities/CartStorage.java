package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import model.CartItem;
import model.Product;

public class CartStorage {

    // Fichero en el home del usuario (Windows: C:\Users\TUUSUARIO\...)
    private static final Path FILE =
            Paths.get(System.getProperty("user.home"), "RetoCRUD_cart.csv");

    public static void add(Product p, int qty) throws IOException {
        List<CartItem> items = readAllInternal(); // no peta si no existe
        boolean found = false;

        for (CartItem it : items) {
            if (it.getProductId() == p.getProductId()) {
                it.addQuantity(qty);     // <-- usamos TU método
                found = true;
                break;
            }
        }

        if (!found) {
            items.add(new CartItem(
                    p.getProductId(),
                    p.getName(),
                    p.getPrice(),
                    qty
            ));
        }

        writeAll(items);
    }

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

                String[] parts = line.split(";", -1);
                // 0:id 1:name 2:price 3:qty
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                double price = Double.parseDouble(parts[2]);
                int q = Integer.parseInt(parts[3]);

                out.add(new CartItem(id, name, price, q));
            }
        }
        return out;
    }

    private static void writeAll(List<CartItem> items) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(
                FILE,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (CartItem it : items) {
                bw.write(it.getProductId() + ";" +
                         it.getName() + ";" +
                         it.getPrice() + ";" +
                         it.getQuantity());
                bw.newLine();
            }
        }
    }
}
