package utilities;

import model.User;

/**
 * Clase de utilidad para gestionar la sesión activa del usuario en la aplicación.
 * Proporciona métodos estáticos para almacenar, recuperar y validar el usuario 
 * autenticado, permitiendo el acceso global a sus datos durante la ejecución.
 * * @author Maleck
 * @version 1.0
 */
public class Session {

    /** El usuario que tiene la sesión iniciada actualmente. */
    private static User currentUser;

    /**
     * Establece el usuario actual de la sesión tras un inicio de sesión exitoso.
     * * @param user El objeto {@link User} que representa al usuario autenticado.
     */
    public static void setUser(User user) {
        currentUser = user;
    }

    /**
     * Obtiene el usuario almacenado en la sesión actual.
     * * @return El objeto {@link User} de la sesión activa, o null si no hay nadie conectado.
     */
    public static User getUser() {
        return currentUser;
    }

    /**
     * Comprueba si existe una sesión activa de usuario.
     * * @return true si hay un usuario autenticado, false en caso contrario.
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Finaliza la sesión actual eliminando la referencia al usuario.
     * Se utiliza para el cierre de sesión o logout.
     */
    public static void logout() {
        currentUser = null;
    }

}