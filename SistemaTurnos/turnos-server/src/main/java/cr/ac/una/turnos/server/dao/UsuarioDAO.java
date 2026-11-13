
package cr.ac.una.turnos.server.dao;

import cr.ac.una.turnos.common.domain.Usuario;
import cr.ac.una.turnos.common.domain.Funcionario;
import cr.ac.una.turnos.common.enums.TipoUsuario;
import cr.ac.una.turnos.server.config.DatabaseConfig;
import cr.ac.una.turnos.server.security.PasswordUtil;
import cr.ac.una.turnos.common.dto.UsuarioDTO;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Funcionario autenticar(
            String nombreUsuario,
            String contrasena) throws SQLException {

        String sql = """
            SELECT u.id_usuario, u.nombre_usuario,
                   u.contrasena, u.rol,
                   f.id_funcionario, f.nombre_completo
            FROM usuario u
            LEFT JOIN funcionario f
                ON u.id_usuario = f.id_usuario
            WHERE u.nombre_usuario = ?
            """;

        try (Connection conexion = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                if (!PasswordUtil.verificar(
                        contrasena,
                        rs.getString("contrasena"))) {
                    return null;
                }

                TipoUsuario tipo = TipoUsuario.valueOf(
                        rs.getString("rol")
                );

                Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        tipo,
                        true
                );

                int idFuncionario = rs.getInt("id_funcionario");

                if (rs.wasNull()) {
                    if (tipo == TipoUsuario.OPERADOR) {
                        return null;
                    }

                    return new Funcionario(
                            0,
                            usuario,
                            rs.getString("nombre_usuario")
                    );
                }

                return new Funcionario(
                        idFuncionario,
                        usuario,
                        rs.getString("nombre_completo")
                );
            }
        }
    }
    public List<UsuarioDTO> obtenerTodos() throws SQLException {

    String sql = """
        SELECT
            u.id_usuario,
            u.nombre_usuario,
            u.rol,
            f.id_funcionario,
            f.nombre_completo
        FROM usuario u
        LEFT JOIN funcionario f
            ON u.id_usuario = f.id_usuario
        ORDER BY u.id_usuario
        """;

    List<UsuarioDTO> usuarios = new ArrayList<>();

    try (Connection conexion = DatabaseConfig.obtenerConexion();
         PreparedStatement ps = conexion.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            int idFuncionario = rs.getInt("id_funcionario");

            Integer funcionario = rs.wasNull()
                    ? null
                    : idFuncionario;

            usuarios.add(new UsuarioDTO(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre_usuario"),
                    TipoUsuario.valueOf(rs.getString("rol")),
                    true,
                    funcionario,
                    rs.getString("nombre_completo")
            ));
        }
    }

    return usuarios;
}
    public void crearOperador(
        String nombreUsuario,
        String contrasena,
        String nombreCompleto) throws SQLException {

    if (nombreUsuario == null || nombreUsuario.isBlank()
            || contrasena == null || contrasena.isBlank()
            || nombreCompleto == null || nombreCompleto.isBlank()) {
        throw new IllegalArgumentException(
                "Todos los campos son obligatorios."
        );
    }

    nombreUsuario = nombreUsuario.trim();
    nombreCompleto = nombreCompleto.trim();

    if (nombreUsuario.length() > 50) {
        throw new IllegalArgumentException(
                "El nombre de usuario no puede superar 50 caracteres."
        );
    }

    if (nombreCompleto.length() > 100) {
        throw new IllegalArgumentException(
                "El nombre completo no puede superar 100 caracteres."
        );
    }

    if (contrasena.length() < 8) {
        throw new IllegalArgumentException(
                "La contraseña debe tener al menos 8 caracteres."
        );
    }

    String hash = PasswordUtil.hash(contrasena);

    String sqlUsuario = """
        INSERT INTO usuario (nombre_usuario, contrasena, rol)
        VALUES (?, ?, 'OPERADOR')
        """;

    String sqlFuncionario = """
        INSERT INTO funcionario (id_usuario, nombre_completo)
        VALUES (?, ?)
        """;

    try (Connection conexion = DatabaseConfig.obtenerConexion()) {

        conexion.setAutoCommit(false);

        try {
            int idUsuario;

            try (PreparedStatement ps = conexion.prepareStatement(
                    sqlUsuario,
                    java.sql.Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, nombreUsuario);
                ps.setString(2, hash);

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "No se pudo obtener el ID del usuario."
                        );
                    }

                    idUsuario = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = conexion.prepareStatement(
                    sqlFuncionario)) {

                ps.setInt(1, idUsuario);
                ps.setString(2, nombreCompleto);
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


