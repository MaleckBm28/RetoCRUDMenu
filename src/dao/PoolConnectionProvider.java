package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;

import pull.ConnectionPool;

public class PoolConnectionProvider
        implements ConnectionProvider, Configurable, Stoppable {

    @Override
    public Connection getConnection() throws SQLException {
        // Pedimos una conexion a tu pool
        return ConnectionPool.getConnection();
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        if (conn != null) {
            // En DBCP2 cerrar la conexion la devuelve al pool
            conn.close();
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public void configure(Map configurationValues) {
        // No necesitamos configuracion extra
        // El ConnectionPool ya esta inicializado
    }

    @Override
    public void stop() {
        // Opcional: no hacemos nada
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}
