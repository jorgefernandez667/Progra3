package cr.ac.una.turnos.server;

import cr.ac.una.turnos.server.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexionBD {

    public static void main(String[] args) {
        System.out.println("Probando conexión con MySQL...");

        try (Connection conexion =
                     DatabaseConfig.obtenerConexion()) {

            if (conexion.isValid(5)) {
                System.out.println(
                        "CONEXIÓN EXITOSA: MySQL está disponible."
                );
                System.out.println(
                        "Base de datos: "
                        + conexion.getCatalog()
                );
            } else {
                System.out.println(
                        "La conexión se abrió, "
                        + "pero no respondió a la validación."
                );
            }

        } catch (SQLException e) {
            System.err.println(
                    "No fue posible conectar con MySQL."
            );
            System.err.println(
                    "Motivo: " + e.getMessage()
            );
        }
    }
}
