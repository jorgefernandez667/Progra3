
package cr.ac.una.turnos.server.dao;

import cr.ac.una.turnos.common.domain.Usuario;
import cr.ac.una.turnos.common.domain.Funcionario;
import cr.ac.una.turnos.common.enums.TipoUsuario;
import cr.ac.una.turnos.server.config.DatabaseConfig;
import cr.ac.una.turnos.server.security.PasswordUtil;

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
}


