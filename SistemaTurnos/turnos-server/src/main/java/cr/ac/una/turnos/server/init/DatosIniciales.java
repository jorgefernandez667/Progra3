
package cr.ac.una.turnos.server.init;

import cr.ac.una.turnos.server.config.DatabaseConfig;
import cr.ac.una.turnos.server.security.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatosIniciales {

    public static void main(String[] args) {

        try (Connection conexion = DatabaseConfig.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                crearOperador(conexion, "operador1", "Operador 1");
                crearOperador(conexion, "operador2", "Operador 2");

                crearVentanilla(conexion, "Ventanilla 1");
                crearVentanilla(conexion, "Ventanilla 2");

                conexion.commit();

                System.out.println("Datos iniciales creados correctamente.");

            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            }

        } catch (Exception e) {
            System.err.println("Error al crear los datos iniciales:");
            e.printStackTrace();
        }
    }

    private static void crearOperador(
            Connection conexion,
            String nombreUsuario,
            String nombreCompleto) throws SQLException {

        int idUsuario = obtenerIdUsuario(conexion, nombreUsuario);

        if (idUsuario == 0) {
            String sql = """
                INSERT INTO usuario (nombre_usuario, contrasena, rol)
                VALUES (?, ?, 'OPERADOR')
                """;

            try (PreparedStatement ps =
                    conexion.prepareStatement(sql,
                            PreparedStatement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, nombreUsuario);
                ps.setString(2, PasswordUtil.hash("Turnos123*"));
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idUsuario = rs.getInt(1);
                    }
                }
            }
        }

        if (idUsuario == 0) {
            throw new SQLException("No se pudo obtener el ID del usuario.");
        }

        String consulta = """
            SELECT id_funcionario
            FROM funcionario
            WHERE id_usuario = ?
            """;

        try (PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
        }

        String insertar = """
            INSERT INTO funcionario (id_usuario, nombre_completo)
            VALUES (?, ?)
            """;

        try (PreparedStatement ps = conexion.prepareStatement(insertar)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, nombreCompleto);
            ps.executeUpdate();
        }
    }

    private static int obtenerIdUsuario(
            Connection conexion,
            String nombreUsuario) throws SQLException {

        String sql = """
            SELECT id_usuario
            FROM usuario
            WHERE nombre_usuario = ?
            """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        }

        return 0;
    }

    private static void crearVentanilla(
            Connection conexion,
            String nombre) throws SQLException {

        String consulta = """
            SELECT id_ventanilla
            FROM ventanilla
            WHERE nombre = ?
            """;

        try (PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
        }

        String insertar = """
            INSERT INTO ventanilla (nombre, estado)
            VALUES (?, 'DISPONIBLE')
            """;

        try (PreparedStatement ps = conexion.prepareStatement(insertar)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        }
    }
}

