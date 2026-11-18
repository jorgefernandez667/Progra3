
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
              AND u.activo = 1
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
            u.activo,
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
                    rs.getBoolean("activo"),
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

    /**
     * Activa o desactiva un usuario.
     *
     * Reglas: el administrador no puede desactivarse a sí mismo, no se
     * puede dejar el sistema sin administradores activos y no se puede
     * desactivar a un operador con la sesión abierta (ocupando una
     * ventanilla): primero debe cerrar sesión.
     */
    public void cambiarEstado(
            int idUsuario,
            boolean activo,
            int idUsuarioSolicitante) throws SQLException {

        if (!activo && idUsuario == idUsuarioSolicitante) {
            throw new IllegalStateException(
                "No puede desactivar su propio usuario."
            );
        }

        try (Connection conexion = DatabaseConfig.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                // Se bloquean todos los usuarios para contar
                // administradores activos sin carreras.
                int administradoresActivos = 0;
                boolean existe = false;
                boolean eraActivo = false;
                boolean esAdministrador = false;

                try (PreparedStatement ps = conexion.prepareStatement(
                        "SELECT id_usuario, rol, activo FROM usuario "
                        + "FOR UPDATE");
                     ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        boolean admin = "ADMINISTRADOR"
                                .equals(rs.getString("rol"));
                        boolean act = rs.getBoolean("activo");

                        if (admin && act) {
                            administradoresActivos++;
                        }

                        if (rs.getInt("id_usuario") == idUsuario) {
                            existe = true;
                            eraActivo = act;
                            esAdministrador = admin;
                        }
                    }
                }

                if (!existe) {
                    throw new IllegalArgumentException(
                        "El usuario indicado no existe."
                    );
                }

                if (eraActivo && !activo) {

                    if (esAdministrador && administradoresActivos <= 1) {
                        throw new IllegalStateException(
                            "Debe quedar al menos un administrador activo."
                        );
                    }

                    String sqlSesion = """
                        SELECT v.nombre
                        FROM asignacion_ventanilla a
                        INNER JOIN funcionario f
                            ON a.id_funcionario = f.id_funcionario
                        INNER JOIN ventanilla v
                            ON a.id_ventanilla = v.id_ventanilla
                        WHERE f.id_usuario = ?
                          AND a.hora_fin IS NULL
                          AND v.estado IN ('OCUPADA', 'NO_DISPONIBLE')
                        LIMIT 1
                        """;

                    try (PreparedStatement ps =
                                 conexion.prepareStatement(sqlSesion)) {

                        ps.setInt(1, idUsuario);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                throw new IllegalStateException(
                                    "El usuario tiene la sesión abierta en "
                                    + rs.getString("nombre")
                                    + ". Debe cerrar sesión primero."
                                );
                            }
                        }
                    }
                }

                try (PreparedStatement ps = conexion.prepareStatement(
                        "UPDATE usuario SET activo = ? "
                        + "WHERE id_usuario = ?")) {

                    ps.setBoolean(1, activo);
                    ps.setInt(2, idUsuario);
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

    /** Indica si el usuario existe y está activo. */
    public boolean estaActivo(int idUsuario) throws SQLException {

        try (Connection conexion = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(
                "SELECT activo FROM usuario WHERE id_usuario = ?")) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("activo");
            }
        }
    }

    /** Asigna una contraseña nueva (mínimo 8 caracteres). */
    public void restablecerContrasena(
            int idUsuario,
            String contrasenaNueva) throws SQLException {

        if (contrasenaNueva == null || contrasenaNueva.length() < 8) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos 8 caracteres."
            );
        }

        try (Connection conexion = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(
                "UPDATE usuario SET contrasena = ? "
                + "WHERE id_usuario = ?")) {

            ps.setString(1, PasswordUtil.hash(contrasenaNueva));
            ps.setInt(2, idUsuario);

            if (ps.executeUpdate() != 1) {
                throw new IllegalArgumentException(
                    "El usuario indicado no existe."
                );
            }
        }
    }

    /** Cambia el nombre completo del funcionario asociado al usuario. */
    public void actualizarNombreCompleto(
            int idUsuario,
            String nombreCompleto) throws SQLException {

        String limpio = nombreCompleto == null
                ? "" : nombreCompleto.trim();

        if (limpio.length() < 3 || limpio.length() > 100) {
            throw new IllegalArgumentException(
                "El nombre completo debe tener entre 3 y 100 caracteres."
            );
        }

        try (Connection conexion = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(
                "UPDATE funcionario SET nombre_completo = ? "
                + "WHERE id_usuario = ?")) {

            ps.setString(1, limpio);
            ps.setInt(2, idUsuario);

            if (ps.executeUpdate() != 1) {
                throw new IllegalArgumentException(
                    "Este usuario no tiene un funcionario asociado."
                );
            }
        }
    }
}
