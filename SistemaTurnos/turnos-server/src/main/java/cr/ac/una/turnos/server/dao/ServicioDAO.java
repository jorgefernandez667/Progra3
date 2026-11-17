
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
            SELECT id_servicio, nombre, prefijo, activo
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
                    resultado.getBoolean("activo")
                );

                servicios.add(servicio);
            }
        }

        return servicios;
    }

    public Servicio obtenerPorId(int id) throws SQLException {
        String sql = """
            SELECT id_servicio, nombre, prefijo, activo
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
                        resultado.getBoolean("activo")
                    );
                }
            }
        }

        return null;
    }

    /** Crea un servicio. El prefijo (una letra) no se puede repetir. */
    public int crear(String prefijo, String nombre) throws SQLException {

        String sql = """
            INSERT INTO servicio (nombre, prefijo, activo)
            VALUES (?, ?, 1)
            """;

        try (Connection conexion = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, prefijo);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException(
                "Ya existe un servicio con el prefijo " + prefijo + "."
            );
        }
    }

    /**
     * Cambia el nombre y/o el estado de un servicio. El prefijo no se
     * modifica porque ya forma parte de los códigos de turno emitidos.
     * No permite dejar el sistema sin ningún servicio activo.
     */
    public void actualizar(int idServicio, String nombre, boolean activo)
            throws SQLException {

        try (Connection conexion = DatabaseConfig.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                // Se bloquean todos los servicios para contar los activos
                // sin que otro administrador desactive el último a la vez.
                int activos = 0;
                boolean existe = false;
                boolean estabaActivo = false;

                try (PreparedStatement ps = conexion.prepareStatement(
                        "SELECT id_servicio, activo FROM servicio FOR UPDATE");
                     ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        boolean a = rs.getBoolean("activo");
                        if (a) {
                            activos++;
                        }
                        if (rs.getInt("id_servicio") == idServicio) {
                            existe = true;
                            estabaActivo = a;
                        }
                    }
                }

                if (!existe) {
                    throw new IllegalArgumentException(
                        "El servicio indicado no existe."
                    );
                }

                if (estabaActivo && !activo && activos <= 1) {
                    throw new IllegalStateException(
                        "Debe quedar al menos un servicio activo."
                    );
                }

                try (PreparedStatement ps = conexion.prepareStatement(
                        "UPDATE servicio SET nombre = ?, activo = ? "
                        + "WHERE id_servicio = ?")) {

                    ps.setString(1, nombre);
                    ps.setBoolean(2, activo);
                    ps.setInt(3, idServicio);
                    ps.executeUpdate();
                }

                conexion.commit();

            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }
}
