
package cr.ac.una.turnos.server.dao;

import cr.ac.una.turnos.common.domain.Servicio;
import cr.ac.una.turnos.server.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    public List<Servicio> obtenerTodos() throws SQLException {
        String sql = """
            SELECT id_servicio, nombre, prefijo
            FROM servicio
            ORDER BY id_servicio
            """;

        List<Servicio> servicios = new ArrayList<>();

        try (Connection conexion = DatabaseConfig.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                Servicio servicio = new Servicio(
                    resultado.getInt("id_servicio"),
                    resultado.getString("prefijo"),
                    resultado.getString("nombre"),
                    null,
                    true
                );

                servicios.add(servicio);
            }
        }

        return servicios;
    }

    public Servicio obtenerPorId(int id) throws SQLException {
        String sql = """
            SELECT id_servicio, nombre, prefijo
            FROM servicio
            WHERE id_servicio = ?
            """;

        try (Connection conexion = DatabaseConfig.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return new Servicio(
                        resultado.getInt("id_servicio"),
                        resultado.getString("prefijo"),
                        resultado.getString("nombre"),
                        null,
                        true
                    );
                }
            }
        }

        return null;
    }
}

