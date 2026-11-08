
package cr.ac.una.turnos.server.dao;

import cr.ac.una.turnos.server.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class VentanillaDAO {

    public int asignarVentanilla(int idFuncionario)
            throws SQLException {

        String sqlBuscar = """
            SELECT id_ventanilla
            FROM ventanilla
            WHERE estado = 'DISPONIBLE'
            ORDER BY id_ventanilla
            LIMIT 1
            FOR UPDATE
            """;

        String sqlAsignar = """
            UPDATE ventanilla
            SET estado = 'OCUPADA'
            WHERE id_ventanilla = ?
              AND estado = 'DISPONIBLE'
            """;

        String sqlHistorial = """
            INSERT INTO asignacion_ventanilla
                (id_funcionario, id_ventanilla,
                 fecha, hora_inicio)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexion = DatabaseConfig.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                int idVentanilla;

                // Buscar una ventanilla disponible y bloquearla.
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlBuscar);
                     ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException(
                                "No hay ventanillas disponibles."
                        );
                    }

                    idVentanilla = rs.getInt("id_ventanilla");
                }

                // Marcar la ventanilla como ocupada.
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlAsignar)) {

                    ps.setInt(1, idVentanilla);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo asignar la ventanilla."
                        );
                    }
                }

                // Registrar quién ocupa la ventanilla.
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlHistorial)) {

                    ps.setInt(1, idFuncionario);
                    ps.setInt(2, idVentanilla);
                    ps.setDate(3,
                            java.sql.Date.valueOf(LocalDate.now()));
                    ps.setTime(4,
                            java.sql.Time.valueOf(LocalTime.now()));

                    ps.executeUpdate();
                }

                conexion.commit();
                return idVentanilla;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;

            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    public void liberarVentanilla(
            int idVentanilla,
            int idFuncionario) throws SQLException {

        String sqlHistorial = """
            UPDATE asignacion_ventanilla
            SET hora_fin = ?
            WHERE id_ventanilla = ?
              AND id_funcionario = ?
              AND fecha = ?
              AND hora_fin IS NULL
            ORDER BY id_asignacion DESC
            LIMIT 1
            """;

        String sqlVentanilla = """
            UPDATE ventanilla
            SET estado = 'DISPONIBLE'
            WHERE id_ventanilla = ?
              AND estado = 'OCUPADA'
            """;

        try (Connection conexion = DatabaseConfig.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                // Finalizar el registro de asignación.
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlHistorial)) {

                    ps.setTime(1,
                            java.sql.Time.valueOf(LocalTime.now()));
                    ps.setInt(2, idVentanilla);
                    ps.setInt(3, idFuncionario);
                    ps.setDate(4,
                            java.sql.Date.valueOf(LocalDate.now()));

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se encontró la asignación activa."
                        );
                    }
                }

                // Liberar la ventanilla.
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlVentanilla)) {

                    ps.setInt(1, idVentanilla);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo liberar la ventanilla."
                        );
                    }
                }

                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw e;

            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }
}


