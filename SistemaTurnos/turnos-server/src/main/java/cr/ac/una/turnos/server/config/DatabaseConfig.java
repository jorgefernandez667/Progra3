package cr.ac.una.turnos.server.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {

    private static final String URL =
            "jdbc:mysql://mysql.us.cloudlogin.co:3306/"
            + "gamabasis_p3g3?serverTimezone=America/Costa_Rica";

    private static final String USER =
            System.getenv("TURNOS_DB_USER");

    private static final String PASSWORD =
            System.getenv("TURNOS_DB_PASSWORD");

    private DatabaseConfig() {
    }

    public static Connection obtenerConexion()
            throws SQLException {

        if (USER == null || USER.isBlank()
                || PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "Faltan las variables de entorno "
                    + "TURNOS_DB_USER y TURNOS_DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}