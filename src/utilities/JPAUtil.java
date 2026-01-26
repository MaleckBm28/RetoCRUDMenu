package utilities;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("CRUD_PU");

    private JPAUtil() { }

    public static EntityManager em() {
        return EMF.createEntityManager();
    }

    public static void shutdown() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }
}
