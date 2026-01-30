package dao;

import dao.ClassDAO;
import pull.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import model.Admin;
import model.Product;
import model.Profile;
import model.User;
import threads.HiloConnection;

/**
 * Implementation of ClassDAO using database operations. Handles all database interactions for users and admins. Provides login, signup, deletion, modification, and retrieval of usernames.
 *
 * Author: acer
 */
public class DBImplementation implements ClassDAO {
    
    
    private static final Logger LOGGER = Logger.getLogger(DBImplementation.class.getName());

    private PreparedStatement stmt;

    // Configuration for database connection
    private ResourceBundle configFile;
    private String driverDB;
    private String urlDB;
    private String userDB;
    private String passwordDB;
    private Connection con;

    // SQL statements
    private final String SQLSINGUPPROFILE = "INSERT INTO PROFILE_ (USERNAME, PASSWORD_, EMAIL, NAME_, TELEPHONE, SURNAME) VALUES (?,?,?,?,?,?);";
    private final String SQLSIGNUPUSER = "INSERT INTO USER_ (USERNAME, GENDER, CARD_NUMBER) VALUES (?,?,?);";

    private final String SLQDELETEPROFILE = "DELETE FROM PROFILE_ WHERE USERNAME = ? AND PASSWORD_ = ?;";
    private final String SLQDELETEPROFILEADMIN = "DELETE p FROM PROFILE_ p JOIN USER_ u ON p.USERNAME = u.USERNAME JOIN ADMIN_ a ON p.USERNAME = a.USERNAME WHERE p.PASSWORD_ = ? AND u.username = ?;";

    private final String SLQLOGINUSER = "SELECT p.*, u.GENDER, u.CARD_NUMBER FROM PROFILE_ p JOIN USER_ u ON p.USERNAME= u.USERNAME WHERE u.USERNAME = ? AND p.PASSWORD_ = ?;";
    private final String SLQLOGINADMIN = "SELECT p.*, a.CURRENT_ACCOUNT FROM PROFILE_ p JOIN ADMIN_ a ON p.USERNAME= a.USERNAME WHERE a.USERNAME = ? AND p.PASSWORD_ = ?;";

    final String SQLMODIFYPROFILE = "UPDATE PROFILE_ P SET P.PASSWORD_ = ?, P.EMAIL = ?, P.NAME_ = ?, P.TELEPHONE = ?, P.SURNAME = ? WHERE USERNAME = ?;";
    final String SQLMODIFYUSER = "UPDATE USER_ U SET U.GENDER = ? WHERE USERNAME = ?";

    private final String SLQSELECTNUSER = "SELECT u.USERNAME FROM USER_ u;";

    /**
     * Default constructor that loads DB configuration.
     */
    public DBImplementation() {
        try {
            // 1. Intentamos cargar del archivo de configuración
            this.configFile = ResourceBundle.getBundle("model.configClass");
            this.driverDB = this.configFile.getString("Driver");
            this.urlDB = this.configFile.getString("Conn");
            this.userDB = this.configFile.getString("DBUser");
            this.passwordDB = this.configFile.getString("DBPass");

            // 2. ¡IMPORTANTE! Abrimos la conexión aquí
            this.con = java.sql.DriverManager.getConnection(this.urlDB, this.userDB, this.passwordDB);
            System.out.println("✅ Conexión establecida desde Config File.");

            //log
            LOGGER.info("Conexión a BD establecida correctamente.");
            
        } catch (Exception e) {
            System.err.println("⚠ Fallo al leer config, intentando conexión directa...");

            // 3. PLAN B: Si falla el config, usamos tus datos directos
            try {
                String url = "jdbc:mysql://localhost:3306/CRUD?serverTimezone=UTC";
                String user = "root";
                String pass = "abcd*1234"; // Tu contraseña

                this.con = java.sql.DriverManager.getConnection(url, user, pass);
                System.out.println("✅ Conexión establecida Directamente (Hardcoded).");

            } catch (SQLException ex) {
                System.err.println("❌ ERROR CRÍTICO: No se pudo conectar a la BD.");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Logs in a user or admin from the database.
     *
     * @param username The username to log in
     * @param password The password to validate
     * @return Profile object (User or Admin) if found, null otherwise
     */
    @Override
    public Profile logIn(String username, String password) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            stmt = con.prepareStatement(SLQLOGINUSER);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet result = stmt.executeQuery();
            if (!(result.next())) {
                stmt = con.prepareStatement(SLQLOGINADMIN);
                stmt.setString(1, username);
                stmt.setString(2, password);
                result = stmt.executeQuery();
                if (result.next()) {
                    Admin profile_admin = new Admin();
                    profile_admin.setUsername(result.getString("USERNAME"));
                    profile_admin.setPassword(result.getString("PASSWORD_"));
                    profile_admin.setEmail(result.getString("EMAIL"));
                    profile_admin.setUserCode(result.getInt("USER_CODE"));
                    profile_admin.setName(result.getString("NAME_"));
                    profile_admin.setTelephone(result.getString("TELEPHONE"));
                    profile_admin.setSurname(result.getString("SURNAME"));
                    profile_admin.setCurrentAccount(result.getString("CURRENT_ACCOUNT"));
                    return profile_admin;
                } else {
                    System.out.println("Usuario encontrado en la base de datos");
                }
            } else {
                User profile_user = new User();
                profile_user.setUsername(result.getString("USERNAME"));
                profile_user.setPassword(result.getString("PASSWORD_"));
                profile_user.setEmail(result.getString("EMAIL"));
                profile_user.setUserCode(result.getInt("USER_CODE"));
                profile_user.setName(result.getString("NAME_"));
                profile_user.setTelephone(result.getString("TELEPHONE"));
                profile_user.setSurname(result.getString("SURNAME"));
                profile_user.setGender(result.getString("GENDER"));
                profile_user.setCardNumber(result.getString("CARD_NUMBER"));
                return profile_user;
            }
        } catch (SQLException e) {
            System.out.println("Database query error");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing database connection");
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Signs up a new user in the database.
     *
     * @return true if signup was successful, false otherwise
     */
    @Override
    public Boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname) {
        HiloConnection connectionThread = new HiloConnection(30);
        connectionThread.start();
        boolean success = false;
        try {
            Connection con = waitForConnection(connectionThread);
            stmt = con.prepareStatement(SQLSINGUPPROFILE);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, name);
            stmt.setString(5, telephone);
            stmt.setString(6, surname);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                stmt = con.prepareStatement(SQLSIGNUPUSER);
                stmt.setString(1, username);
                stmt.setString(2, gender);
                stmt.setString(3, cardNumber);
                rowsUpdated = stmt.executeUpdate();
                success = rowsUpdated > 0;
            }
        } catch (SQLException | InterruptedException e) {
            System.out.println("Database error on signup");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                connectionThread.releaseConnection();
            } catch (SQLException e) {
                System.out.println("Error closing DB connection after signup");
                e.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Deletes a standard user from the database.
     */
    @Override
    public Boolean dropOutUser(String username, String password) {
        HiloConnection connectionThread = new HiloConnection(30);
        connectionThread.start();
        boolean success = false;
        PreparedStatement stmtUser = null;
        try {
            Connection con = waitForConnection(connectionThread);

            // verificar password
            String checkPassword = "SELECT PASSWORD_ FROM PROFILE_ WHERE USERNAME = ?";
            stmt = con.prepareStatement(checkPassword);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("PASSWORD_");
                if (!dbPassword.equals(password)) {
                    return false;
                }
            } else {
                return false;
            }
            rs.close();
            stmt.close();

            // eliminar de USER_ primero
            String deleteUser = "DELETE FROM USER_ WHERE USERNAME = ?";
            stmtUser = con.prepareStatement(deleteUser);
            stmtUser.setString(1, username);
            stmtUser.executeUpdate();
            stmtUser.close();

            // eliminar de PROFILE_
            stmt = con.prepareStatement(SLQDELETEPROFILE);
            stmt.setString(1, username);
            stmt.setString(2, password);
            success = stmt.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            System.out.println("Database error on deleting user");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (stmtUser != null) {
                    stmtUser.close();
                }
                connectionThread.releaseConnection();
            } catch (SQLException e) {
                System.out.println("Error closing DB connection after deleting user");
                e.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Deletes a user selected by admin from the database.
     */
    @Override
    public Boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) {
        HiloConnection connectionThread = new HiloConnection(30);
        connectionThread.start();
        boolean success = false;
        PreparedStatement stmtDeleteUser = null;
        PreparedStatement stmtDeleteAdmin = null;
        try {
            Connection con = waitForConnection(connectionThread);

            // verificar password del admin logueado
            String checkAdminPassword = "SELECT PASSWORD_ FROM PROFILE_ WHERE USERNAME = ?";
            stmt = con.prepareStatement(checkAdminPassword);
            stmt.setString(1, adminUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("PASSWORD_");
                if (!dbPassword.equals(adminPassword)) {
                    return false;
                }
            } else {
                return false;
            }
            rs.close();
            stmt.close();

            // eliminar de USER_ si existe
            String deleteUser = "DELETE FROM USER_ WHERE USERNAME = ?";
            stmtDeleteUser = con.prepareStatement(deleteUser);
            stmtDeleteUser.setString(1, usernameToDelete);
            stmtDeleteUser.executeUpdate();
            stmtDeleteUser.close();

            // eliminar de ADMIN_ si existe
            String deleteAdmin = "DELETE FROM ADMIN_ WHERE USERNAME = ?";
            stmtDeleteAdmin = con.prepareStatement(deleteAdmin);
            stmtDeleteAdmin.setString(1, usernameToDelete);
            stmtDeleteAdmin.executeUpdate();
            stmtDeleteAdmin.close();

            // eliminar de PROFILE_
            String deleteProfile = "DELETE FROM PROFILE_ WHERE USERNAME = ?";
            stmt = con.prepareStatement(deleteProfile);
            stmt.setString(1, usernameToDelete);
            success = stmt.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            System.out.println("Database error on deleting admin");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (stmtDeleteUser != null) {
                    stmtDeleteUser.close();
                }
                if (stmtDeleteAdmin != null) {
                    stmtDeleteAdmin.close();
                }
                connectionThread.releaseConnection();
            } catch (SQLException e) {
                System.out.println("Error closing DB connection after deleting admin");
                e.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Modifies the information of a user in the database.
     */
    @Override
    public Boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender) {
        HiloConnection connectionThread = new HiloConnection(30);
        connectionThread.start();
        boolean success = false;
        PreparedStatement stmtUser = null;

        try {
            Connection con = waitForConnection(connectionThread);

            // actualizar PROFILE_
            stmt = con.prepareStatement(SQLMODIFYPROFILE);
            stmt.setString(1, password);
            stmt.setString(2, email);
            stmt.setString(3, name);
            stmt.setString(4, telephone);
            stmt.setString(5, surname);
            stmt.setString(6, username);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                // actualizar USER_ si existe
                stmtUser = con.prepareStatement(SQLMODIFYUSER);
                stmtUser.setString(1, gender);
                stmtUser.setString(2, username);
                stmtUser.executeUpdate();
                stmtUser.close();

                success = true;
            } else {
                System.out.println("Usuario no encontrado en la base de datos");
                success = false;
            }
        } catch (SQLException | InterruptedException e) {
            System.out.println("Database error on modifying user");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (stmtUser != null) {
                    stmtUser.close();
                }
                connectionThread.releaseConnection();

            } catch (SQLException e) {
                System.out.println("Error closing DB connection after modifying user");
                e.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Retrieves a list of usernames from the database.
     *
     * @return List of usernames
     */
    @Override
    public List comboBoxInsert() {
        List<String> listaUsuarios = new ArrayList<>();
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            stmt = con.prepareStatement(SLQSELECTNUSER);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                listaUsuarios.add(result.getString("USERNAME"));
            }
        } catch (SQLException e) {
            System.out.println("Database error on retrieving usernames");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing DB connection after retrieving usernames");
                e.printStackTrace();
            }
        }
        return listaUsuarios;
    }

    private Connection waitForConnection(HiloConnection thread) throws InterruptedException {
        int attempts = 0;
        while (!thread.isReady() && attempts < 50) {
            Thread.sleep(10);
            attempts++;
        }
        return thread.getConnection();
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        if (this.con == null) {
            System.err.println("La conexión es NULA. Revisa tu constructor.");
            return products;
        }

        String sql = "SELECT p.*, b.PACK_AMOUNT, k.CARD_AMOUNT, c.RARITY "
                + "FROM PRODUCT p "
                + "LEFT JOIN BOOSTER_BOX b ON p.PRODUCT_ID = b.PRODUCT_ID "
                + "LEFT JOIN BOOSTER_PACK k ON p.PRODUCT_ID = k.PRODUCT_ID "
                + "LEFT JOIN CARD c ON p.PRODUCT_ID = c.PRODUCT_ID";

        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product p = null;

                // --- 1. IDENTIFICAR TIPO ---
                if (rs.getObject("PACK_AMOUNT") != null) {
                    model.BoosterBox box = new model.BoosterBox();
                    box.setPackAmount(rs.getInt("PACK_AMOUNT"));
                    p = box;
                } else if (rs.getObject("CARD_AMOUNT") != null) {
                    model.BoosterPack pack = new model.BoosterPack();
                    pack.setCardAmount(rs.getInt("CARD_AMOUNT"));
                    p = pack;
                } else if (rs.getString("RARITY") != null) {
                    model.Card card = new model.Card();
                    card.setRarity(rs.getString("RARITY"));
                    p = card;
                } else {
                    p = new model.Card(); // Default
                }

                // --- 2. DATOS COMUNES ---
                p.setProductId(rs.getInt("PRODUCT_ID"));
                p.setName(rs.getString("NAME"));
                p.setPrice(rs.getDouble("PRICE"));
                p.setStock(rs.getInt("STOCK"));
                p.setImagePath(rs.getString("IMAGE_PATH"));

                try {
                    p.setGameType(model.GameType.valueOf(rs.getString("GAME_TYPE")));
                } catch (Exception e) {
                    p.setGameType(model.GameType.MAGIC);
                }

                products.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error SQL en getAllProducts:");
            e.printStackTrace();
        }
        return products;
    }

}
