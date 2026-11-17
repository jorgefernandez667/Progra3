
package cr.ac.una.turnos.server.dao;

import cr.ac.una.turnos.server.config.DatabaseConfig;
import cr.ac.una.turnos.common.domain.Ventanilla;
import cr.ac.una.turnos.common.enums.EstadoVentanilla;
import java.util.ArrayList;
import java.util.List;
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
              AND estado IN ('OCUPADA', 'NO_DISPONIBLE')
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
    /**
     * Servicios que atiende cada ventanilla (ventanilla_servicio).
     * Una ventanilla sin filas atiende todos los servicios.
     */
    public java.util.Map<Integer, List<Integer>> obtenerServiciosPorVentanilla()
            throws SQLException {

        String sql = """
            SELECT id_ventanilla, id_servicio
            FROM ventanilla_servicio
            ORDER BY id_ventanilla, id_servicio
            """;

        java.util.Map<Integer, List<Integer>> resultado =
                new java.util.HashMap<>();

        try (Connection conexion = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado
                    .computeIfAbsent(
                        rs.getInt("id_ventanilla"),
                        k -> new ArrayList<>()
                    )
                    .add(rs.getInt("id_servicio"));
            }
        }

        return resultado;
    }

    /**
     * Define qué servicios atiende una ventanilla. Reemplaza la
     * configuración anterior. Una lista vacía significa "todos".
     *
     * Se bloquea la fila de la ventanilla, igual que al llamar un turno
     * (TurnoDAO.llamarSiguienteTurno), así que cambiar la configuración y
     * llamar un turno al mismo tiempo se ejecutan en serie y la llamada
     * siempre ve una configuración completa.
     */
    public void asignarServicios(
            int idVentanilla,
            List<Integer> idServicios) throws SQLException {

        if (idServicios == null) {
            throw new IllegalArgumentException(
                "Debe indicar la lista de servicios."
            );
        }

        java.util.Set<Integer> servicios =
                new java.util.LinkedHashSet<>(idServicios);

        String sqlBloquear = """
            SELECT id_ventanilla
            FROM ventanilla
            WHERE id_ventanilla = ?
            FOR UPDATE
            """;

        String sqlExiste = """
            SELECT 1
            FROM servicio
            WHERE id_servicio = ?
            """;

        String sqlBorrar = """
            DELETE FROM ventanilla_servicio
            WHERE id_ventanilla = ?
            """;

        String sqlInsertar = """
            INSERT INTO ventanilla_servicio (id_ventanilla, id_servicio)
            VALUES (?, ?)
            """;

        try (Connection conexion = DatabaseConfig.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlBloquear)) {

                    ps.setInt(1, idVentanilla);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                "La ventanilla indicada no existe."
                            );
                        }
                    }
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlExiste)) {

                    for (int idServicio : servicios) {
                        ps.setInt(1, idServicio);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new IllegalArgumentException(
                                    "El servicio " + idServicio
                                    + " no existe."
                                );
                            }
                        }
                    }
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlBorrar)) {

                    ps.setInt(1, idVentanilla);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlInsertar)) {

                    for (int idServicio : servicios) {
                        ps.setInt(1, idVentanilla);
                        ps.setInt(2, idServicio);
                        ps.addBatch();
                    }

                    ps.executeBatch();
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

    public List<Ventanilla> obtenerTodas() throws SQLException {
    String sql = """
        SELECT id_ventanilla, nombre, estado
        FROM ventanilla
        ORDER BY id_ventanilla
        """;

    List<Ventanilla> resultado = new ArrayList<>();

    try (Connection conexion = DatabaseConfig.obtenerConexion();
         PreparedStatement ps = conexion.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            resultado.add(new Ventanilla(
                rs.getInt("id_ventanilla"),
                rs.getString("nombre"),
                EstadoVentanilla.valueOf(rs.getString("estado"))
            ));
        }
    }

    return resultado;
}

/**
 * El operador marca su ventanilla como no disponible (por ejemplo, para
 * un descanso) o vuelve a ponerla disponible.
 *
 * OCUPADA <-> NO_DISPONIBLE. Mientras está NO_DISPONIBLE no puede llamar
 * turnos (lo valida TurnoDAO.llamarSiguienteTurno). No se permite
 * pausar con un turno LLAMADO o EN_ATENCION: primero se debe terminar.
 *
 * Se bloquea la fila de la ventanilla, igual que al llamar un turno, así
 * que pausar y llamar al mismo tiempo se ejecutan en serie.
 *
 * @return el estado en que quedó la ventanilla
 */
public EstadoVentanilla cambiarDisponibilidad(
        int idVentanilla,
        boolean disponible) throws SQLException {

    String sqlBloquear = """
        SELECT estado
        FROM ventanilla
        WHERE id_ventanilla = ?
        FOR UPDATE
        """;

    String sqlTurnoActivo = """
        SELECT codigo
        FROM turno
        WHERE id_ventanilla = ?
          AND estado IN ('LLAMADO', 'EN_ATENCION')
        LIMIT 1
        """;

    String sqlActualizar = """
        UPDATE ventanilla
        SET estado = ?
        WHERE id_ventanilla = ?
        """;

    EstadoVentanilla destino = disponible
            ? EstadoVentanilla.OCUPADA
            : EstadoVentanilla.NO_DISPONIBLE;

    try (Connection conexion = DatabaseConfig.obtenerConexion()) {

        conexion.setAutoCommit(false);

        try {
            EstadoVentanilla actual;

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlBloquear)) {

                ps.setInt(1, idVentanilla);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException(
                            "La ventanilla indicada no existe."
                        );
                    }

                    actual = EstadoVentanilla.valueOf(
                        rs.getString("estado")
                    );
                }
            }

            if (actual != EstadoVentanilla.OCUPADA
                    && actual != EstadoVentanilla.NO_DISPONIBLE) {
                throw new IllegalStateException(
                    "La ventanilla no está en uso por un operador."
                );
            }

            if (actual == destino) {
                conexion.commit();
                return actual;
            }

            if (!disponible) {
                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlTurnoActivo)) {

                    ps.setInt(1, idVentanilla);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new IllegalStateException(
                                "No puede marcarse como no disponible "
                                + "mientras tiene el turno "
                                + rs.getString("codigo")
                                + " en proceso. Finalícelo primero."
                            );
                        }
                    }
                }
            }

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlActualizar)) {

                ps.setString(1, destino.name());
                ps.setInt(2, idVentanilla);
                ps.executeUpdate();
            }

            conexion.commit();
            return destino;

        } catch (SQLException | RuntimeException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
    }
}

public void cambiarEstado(
        int idVentanilla,
        EstadoVentanilla nuevoEstado) throws SQLException {

    if (nuevoEstado == null) {
        throw new IllegalArgumentException("Debe indicar un estado.");
    }

    if (nuevoEstado == EstadoVentanilla.OCUPADA
            || nuevoEstado == EstadoVentanilla.NO_DISPONIBLE) {
        throw new IllegalArgumentException(
            "Los estados OCUPADA y NO_DISPONIBLE los controla el "
            + "operador que usa la ventanilla."
        );
    }

    String sql = """
        UPDATE ventanilla
        SET estado = ?
        WHERE id_ventanilla = ?
          AND estado NOT IN ('OCUPADA', 'NO_DISPONIBLE')
        """;

    try (Connection conexion = DatabaseConfig.obtenerConexion();
         PreparedStatement ps = conexion.prepareStatement(sql)) {

        ps.setString(1, nuevoEstado.name());
        ps.setInt(2, idVentanilla);

        if (ps.executeUpdate() != 1) {
            throw new IllegalStateException(
                "No se encontró la ventanilla o está en uso por un operador."
            );
        }
    }
}
}


