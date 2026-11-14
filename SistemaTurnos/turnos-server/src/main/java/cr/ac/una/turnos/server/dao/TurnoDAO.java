package cr.ac.una.turnos.server.dao;

import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.server.config.DatabaseConfig;
import java.time.LocalDateTime;
import cr.ac.una.turnos.common.domain.Servicio;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;
import cr.ac.una.turnos.common.domain.Servicio;
import cr.ac.una.turnos.common.enums.EstadoTurno;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import cr.ac.una.turnos.common.enums.EstadoTurno;

public class TurnoDAO {

    public int insertar(Turno turno) throws SQLException {

        if (turno == null) {
            throw new IllegalArgumentException(
                    "El turno no puede ser nulo."
            );
        }

        if (turno.getServicio() == null) {
            throw new IllegalArgumentException(
                    "El turno debe tener un servicio."
            );
        }

        if (turno.getPrioridad() == null) {
            throw new IllegalArgumentException(
                    "El turno debe tener una prioridad."
            );
        }

        String sql = """
            INSERT INTO turno (
                codigo,
                id_servicio,
                id_prioridad,
                estado,
                fecha_generacion,
                hora_generacion,
                hora_llamado,
                hora_inicio_atencion,
                hora_fin_atencion,
                id_ventanilla,
                id_funcionario
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexion =
                     DatabaseConfig.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             sql, Statement.RETURN_GENERATED_KEYS)) {

            turno.setFechaGeneracion(
                    turno.getFechaGeneracion() == null
                            ? java.time.LocalDateTime.now()
                            : turno.getFechaGeneracion()
            );

            sentencia.setString(1, turno.getCodigo());
            sentencia.setInt(2, turno.getServicio().getId());
            sentencia.setInt(3, obtenerIdPrioridad(
                    turno.getPrioridad().name()));
            sentencia.setString(4, turno.getEstado().name());

            sentencia.setDate(5, Date.valueOf(
                    turno.getFechaGeneracion().toLocalDate()));
            sentencia.setTimestamp(6, Timestamp.valueOf(
                    turno.getFechaGeneracion()));

            establecerFecha(sentencia, 7, turno.getFechaLlamado());
            establecerFecha(
                    sentencia, 8, turno.getFechaInicioAtencion());
            establecerFecha(
                    sentencia, 9, turno.getFechaFinAtencion());

            if (turno.getIdVentanilla() == null) {
                sentencia.setNull(10, java.sql.Types.INTEGER);
            } else {
                sentencia.setInt(10, turno.getIdVentanilla());
            }

            if (turno.getIdFuncionario() == null) {
                sentencia.setNull(11, java.sql.Types.INTEGER);
            } else {
                sentencia.setInt(11, turno.getIdFuncionario());
            }

            sentencia.executeUpdate();

            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    int idGenerado = claves.getInt(1);
                    turno.setId(idGenerado);
                    return idGenerado;
                }
            }

            throw new SQLException(
                    "No se pudo recuperar el ID del turno insertado."
            );
        }
    }

    private int obtenerIdPrioridad(String nombre)
            throws SQLException {

        String sql = """
            SELECT id_prioridad
            FROM prioridad
            WHERE nombre = ?
            """;

        try (Connection conexion =
                     DatabaseConfig.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setString(1, nombre);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("id_prioridad");
                }
            }
        }

        throw new SQLException(
                "No se encontró la prioridad: " + nombre
        );
    }

    private void establecerFecha(
            PreparedStatement sentencia,
            int posicion,
            java.time.LocalDateTime fecha) throws SQLException {

        if (fecha == null) {
            sentencia.setNull(posicion, java.sql.Types.TIMESTAMP);
        } else {
            sentencia.setTimestamp(
                    posicion, Timestamp.valueOf(fecha));
        }
    }

 public void actualizar(Turno turno) throws java.sql.SQLException {
    if (turno == null) {
        throw new IllegalArgumentException(
                "El turno no puede ser nulo."
        );
    }

    if (turno.getId() <= 0) {
        throw new IllegalArgumentException(
                "El turno debe tener un ID válido."
        );
    }

    String sql = """
        UPDATE turno
        SET estado = ?,
            hora_llamado = ?,
            hora_inicio_atencion = ?,
            hora_fin_atencion = ?,
            id_ventanilla = ?,
            id_funcionario = ?
        WHERE id_turno = ?
        """;

    try (java.sql.Connection conexion =
                 cr.ac.una.turnos.server.config.DatabaseConfig.obtenerConexion();
         java.sql.PreparedStatement sentencia =
                 conexion.prepareStatement(sql)) {

        sentencia.setString(1, turno.getEstado().name());

        establecerFecha(sentencia, 2, turno.getFechaLlamado());
        establecerFecha(sentencia, 3, turno.getFechaInicioAtencion());
        establecerFecha(sentencia, 4, turno.getFechaFinAtencion());

        if (turno.getIdVentanilla() == null) {
            sentencia.setNull(5, java.sql.Types.INTEGER);
        } else {
            sentencia.setInt(5, turno.getIdVentanilla());
        }

        if (turno.getIdFuncionario() == null) {
            sentencia.setNull(6, java.sql.Types.INTEGER);
        } else {
            sentencia.setInt(6, turno.getIdFuncionario());
        }

        sentencia.setInt(7, turno.getId());

        int filasActualizadas = sentencia.executeUpdate();

        if (filasActualizadas == 0) {
            throw new java.sql.SQLException(
                    "No se encontró el turno con ID "
                    + turno.getId()
            );
        }
    }
 }

 public int contarPorPrefijo(String prefijo)
        throws SQLException {

    String sql = """
        SELECT COUNT(*)
        FROM turno t
        JOIN servicio s ON t.id_servicio = s.id_servicio
        WHERE s.prefijo = ?
        """;

    try (Connection conexion = DatabaseConfig.obtenerConexion();
         PreparedStatement sentencia = conexion.prepareStatement(sql)) {

        sentencia.setString(1, prefijo);

        try (ResultSet resultado = sentencia.executeQuery()) {
            if (resultado.next()) {
                return resultado.getInt(1);
            }
        }
    }

    return 0;
 }

public List<Turno> obtenerTurnosPendientes() throws SQLException {

    String sql = """
        SELECT t.id_turno, t.codigo, t.estado,
               t.fecha_generacion, t.hora_generacion,
               t.hora_llamado, t.hora_inicio_atencion,
               t.hora_fin_atencion, t.id_ventanilla,
               t.id_funcionario,
               s.id_servicio, s.prefijo, s.nombre,
               p.nombre AS prioridad
        FROM turno t
        INNER JOIN servicio s
            ON t.id_servicio = s.id_servicio
        INNER JOIN prioridad p
            ON t.id_prioridad = p.id_prioridad
        WHERE t.estado = 'EN_ESPERA'
        ORDER BY t.hora_generacion ASC
        """;

    List<Turno> resultado = new ArrayList<>();

    try (Connection conexion = DatabaseConfig.obtenerConexion();
         PreparedStatement sentencia = conexion.prepareStatement(sql);
         ResultSet rs = sentencia.executeQuery()) {

        while (rs.next()) {
            Servicio servicio = new Servicio(
        rs.getInt("id_servicio"),
        rs.getString("prefijo"),
        rs.getString("nombre"),
        null,
        true
);

            Turno turno = new Turno();
            turno.setId(rs.getInt("id_turno"));
            turno.setCodigo(rs.getString("codigo"));
            turno.setServicio(servicio);
            turno.setPrioridad(
                    PrioridadTurno.valueOf(rs.getString("prioridad"))
            );
            turno.setEstado(
                    EstadoTurno.valueOf(rs.getString("estado"))
            );

            Timestamp generacion = rs.getTimestamp("hora_generacion");
            if (generacion != null) {
                turno.setFechaGeneracion(
                        generacion.toLocalDateTime()
                );
            }

            Timestamp llamado = rs.getTimestamp("hora_llamado");
            if (llamado != null) {
                turno.setFechaLlamado(llamado.toLocalDateTime());
            }

            Timestamp inicio = rs.getTimestamp("hora_inicio_atencion");
            if (inicio != null) {
                turno.setFechaInicioAtencion(
                        inicio.toLocalDateTime()
                );
            }

            Timestamp fin = rs.getTimestamp("hora_fin_atencion");
            if (fin != null) {
                turno.setFechaFinAtencion(fin.toLocalDateTime());
            }

            int idVentanilla = rs.getInt("id_ventanilla");
            if (!rs.wasNull()) {
                turno.setIdVentanilla(idVentanilla);
            }

            int idFuncionario = rs.getInt("id_funcionario");
            if (!rs.wasNull()) {
                turno.setIdFuncionario(idFuncionario);
            }

            resultado.add(turno);
        }
    }

    return resultado;
}
public List<Turno> obtenerUltimosTurnosLlamados()
        throws SQLException {

    String sql = """
        SELECT t.codigo, t.estado, t.id_ventanilla,
               t.hora_llamado, t.hora_inicio_atencion,
               t.hora_fin_atencion,
               s.id_servicio, s.prefijo, s.nombre
        FROM turno t
        INNER JOIN servicio s
            ON t.id_servicio = s.id_servicio
        WHERE t.hora_llamado IS NOT NULL
        ORDER BY t.hora_llamado DESC
        LIMIT 10
        """;

    List<Turno> turnos = new ArrayList<>();

    try (Connection conexion = DatabaseConfig.obtenerConexion();
         PreparedStatement ps = conexion.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            Servicio servicio = new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getString("prefijo"),
                    rs.getString("nombre"),
                    null,
                    true
            );

            Turno turno = new Turno();
            turno.setCodigo(rs.getString("codigo"));
            turno.setServicio(servicio);
            turno.setEstado(
                    EstadoTurno.valueOf(rs.getString("estado"))
            );

            int idVentanilla = rs.getInt("id_ventanilla");
            turno.setIdVentanilla(
                    rs.wasNull() ? null : idVentanilla
            );

            Timestamp llamado = rs.getTimestamp("hora_llamado");
            if (llamado != null) {
                turno.setFechaLlamado(llamado.toLocalDateTime());
            }

            Timestamp inicio =
                    rs.getTimestamp("hora_inicio_atencion");
            if (inicio != null) {
                turno.setFechaInicioAtencion(
                        inicio.toLocalDateTime()
                );
            }

            Timestamp fin = rs.getTimestamp("hora_fin_atencion");
            if (fin != null) {
                turno.setFechaFinAtencion(fin.toLocalDateTime());
            }

            turnos.add(turno);
        }
    }

    return turnos;
}

public Turno llamarSiguienteTurno(
        int idFuncionario,
        int idVentanilla) throws SQLException {

    String sqlBuscar = """
    SELECT t.id_turno, t.codigo, t.id_servicio,
           t.id_prioridad, t.fecha_generacion,
           t.hora_generacion, s.prefijo, s.nombre,
           p.nombre AS prioridad
    FROM turno t
    INNER JOIN servicio s
        ON t.id_servicio = s.id_servicio
    INNER JOIN prioridad p
        ON t.id_prioridad = p.id_prioridad
    WHERE t.estado = 'EN_ESPERA'
    ORDER BY
        CASE t.id_prioridad
            WHEN 3 THEN 1
            WHEN 2 THEN 2
            WHEN 1 THEN 3
            ELSE 4
        END ASC,
        t.hora_generacion ASC
    LIMIT 1
    FOR UPDATE
    """;

    String sqlBloquearVentanilla = """
        SELECT id_ventanilla
        FROM ventanilla
        WHERE id_ventanilla = ?
        FOR UPDATE
        """;

    String sqlTurnoActivo = """
        SELECT codigo, estado
        FROM turno
        WHERE id_ventanilla = ?
          AND estado IN ('LLAMADO', 'EN_ATENCION')
        LIMIT 1
        """;

    String sqlActualizar = """
        UPDATE turno
        SET estado = 'LLAMADO',
            hora_llamado = ?,
            id_ventanilla = ?,
            id_funcionario = ?
        WHERE id_turno = ?
        """;

    String sqlHistorial = """
        INSERT INTO historial_turno
            (id_turno, estado, fecha_hora,
             id_funcionario, id_ventanilla, comentario)
        VALUES (?, 'LLAMADO', ?, ?, ?, ?)
        """;

    try (Connection conexion = DatabaseConfig.obtenerConexion()) {
        conexion.setAutoCommit(false);

        try {
            Turno turno;

            // Regla: una ventanilla no puede atender dos turnos a la vez.
            // Se bloquea la fila de la ventanilla para que dos solicitudes
            // simultáneas de la misma ventanilla se ejecuten en serie.
            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlBloquearVentanilla)) {

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
                         conexion.prepareStatement(sqlTurnoActivo)) {

                ps.setInt(1, idVentanilla);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new IllegalStateException(
                            "Ya tiene el turno " + rs.getString("codigo")
                            + " en estado " + rs.getString("estado")
                            + ". Finalícelo antes de llamar otro."
                        );
                    }
                }
            }

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlBuscar);
                 ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    conexion.commit();
                    return null;
                }

                Servicio servicio = new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getString("prefijo"),
                    rs.getString("nombre"),
                    null,
                    true
                );

                turno = new Turno();
                turno.setId(rs.getInt("id_turno"));
                turno.setCodigo(rs.getString("codigo"));
                turno.setServicio(servicio);
                turno.setPrioridad(
                    PrioridadTurno.valueOf(rs.getString("prioridad"))
                );
                turno.setEstado(EstadoTurno.LLAMADO);
            }

            LocalDateTime ahora = LocalDateTime.now();

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlActualizar)) {

                ps.setTimestamp(1, Timestamp.valueOf(ahora));
                ps.setInt(2, idVentanilla);
                ps.setInt(3, idFuncionario);
                ps.setInt(4, turno.getId());

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                        "No se pudo asignar el turno."
                    );
                }
            }

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlHistorial)) {

                ps.setInt(1, turno.getId());
                ps.setTimestamp(2, Timestamp.valueOf(ahora));
                ps.setInt(3, idFuncionario);
                ps.setInt(4, idVentanilla);
                ps.setString(5, "Turno llamado");

                ps.executeUpdate();
            }

            turno.setFechaLlamado(ahora);
            turno.setIdFuncionario(idFuncionario);
            turno.setIdVentanilla(idVentanilla);

            conexion.commit();
            return turno;

        } catch (SQLException | RuntimeException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
    }
}

private record TurnoActivo(
        int id, String estado, Integer idFuncionario) {
}

/**
 * Libera los turnos que quedaron activos en una ventanilla cuando
 * termina la sesión de su operador (o al iniciar una sesión nueva,
 * para recuperar turnos huérfanos de una caída anterior).
 *
 * LLAMADO     -> EN_ESPERA  (vuelve a la cola para otro operador)
 * EN_ATENCION -> ABANDONADO (la atención no pudo terminarse)
 *
 * Todo ocurre en una sola transacción, con historial.
 *
 * @return cantidad de turnos liberados
 */
public int liberarTurnosActivosDeVentanilla(int idVentanilla)
        throws SQLException {

    String sqlBuscar = """
        SELECT id_turno, estado, id_funcionario
        FROM turno
        WHERE id_ventanilla = ?
          AND estado IN ('LLAMADO', 'EN_ATENCION')
        FOR UPDATE
        """;

    String sqlDevolver = """
        UPDATE turno
        SET estado = 'EN_ESPERA',
            hora_llamado = NULL,
            id_ventanilla = NULL,
            id_funcionario = NULL
        WHERE id_turno = ? AND estado = 'LLAMADO'
        """;

    String sqlAbandonar = """
        UPDATE turno
        SET estado = 'ABANDONADO'
        WHERE id_turno = ? AND estado = 'EN_ATENCION'
        """;

    String sqlHistorial = """
        INSERT INTO historial_turno
            (id_turno, estado, fecha_hora,
             id_funcionario, id_ventanilla, comentario)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    try (Connection conexion = DatabaseConfig.obtenerConexion()) {
        conexion.setAutoCommit(false);

        try {
            List<TurnoActivo> activos = new ArrayList<>();

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlBuscar)) {

                ps.setInt(1, idVentanilla);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int idFuncionario = rs.getInt("id_funcionario");

                        activos.add(new TurnoActivo(
                                rs.getInt("id_turno"),
                                rs.getString("estado"),
                                rs.wasNull() ? null : idFuncionario
                        ));
                    }
                }
            }

            Timestamp ahora = Timestamp.valueOf(LocalDateTime.now());

            for (TurnoActivo activo : activos) {

                boolean devolver = "LLAMADO".equals(activo.estado());

                String nuevoEstado = devolver
                        ? "EN_ESPERA"
                        : "ABANDONADO";

                String comentario = devolver
                        ? "Devuelto a la cola: el operador cerró sesión"
                        : "Abandonado: la sesión del operador terminó "
                          + "durante la atención";

                try (PreparedStatement ps = conexion.prepareStatement(
                        devolver ? sqlDevolver : sqlAbandonar)) {

                    ps.setInt(1, activo.id());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "El turno " + activo.id()
                                + " cambió de estado durante la liberación."
                        );
                    }
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlHistorial)) {

                    ps.setInt(1, activo.id());
                    ps.setString(2, nuevoEstado);
                    ps.setTimestamp(3, ahora);

                    if (activo.idFuncionario() == null) {
                        ps.setNull(4, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(4, activo.idFuncionario());
                    }

                    ps.setInt(5, idVentanilla);
                    ps.setString(6, comentario);

                    ps.executeUpdate();
                }
            }

            conexion.commit();
            return activos.size();

        } catch (SQLException | RuntimeException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
    }
}

public void cambiarEstadoConHistorial(
        Turno turno,
        EstadoTurno estadoEsperado,
        EstadoTurno nuevoEstado,
        LocalDateTime fecha,
        String comentario) throws SQLException {

    if (turno == null || turno.getId() <= 0) {
        throw new IllegalArgumentException("Turno inválido.");
    }

    if (turno.getIdFuncionario() == null
            || turno.getIdVentanilla() == null) {
        throw new IllegalArgumentException(
                "El turno no tiene funcionario o ventanilla asignados."
        );
    }

    String columnaFecha;

    switch (nuevoEstado) {
        case EN_ATENCION ->
            columnaFecha = "hora_inicio_atencion";
        case FINALIZADO ->
            columnaFecha = "hora_fin_atencion";
        default ->
            throw new IllegalArgumentException(
                    "Transición de estado no permitida."
            );
    }

    String sqlActualizar = """
        UPDATE turno
        SET estado = ?, %s = ?
        WHERE id_turno = ? AND estado = ?
        """.formatted(columnaFecha);

    String sqlHistorial = """
        INSERT INTO historial_turno
            (id_turno, estado, fecha_hora,
             id_funcionario, id_ventanilla, comentario)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    try (Connection conexion = DatabaseConfig.obtenerConexion()) {
        conexion.setAutoCommit(false);

        try {
            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlActualizar)) {

                ps.setString(1, nuevoEstado.name());
                ps.setTimestamp(2, Timestamp.valueOf(fecha));
                ps.setInt(3, turno.getId());
                ps.setString(4, estadoEsperado.name());

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                            "El turno ya cambió de estado o no existe."
                    );
                }
            }

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlHistorial)) {

                ps.setInt(1, turno.getId());
                ps.setString(2, nuevoEstado.name());
                ps.setTimestamp(3, Timestamp.valueOf(fecha));
                ps.setInt(4, turno.getIdFuncionario());
                ps.setInt(5, turno.getIdVentanilla());
                ps.setString(6, comentario);

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

public Turno obtenerUltimoTurnoLlamado() throws SQLException {

    String sql = """
        SELECT t.codigo, t.estado, t.id_ventanilla,
               t.hora_llamado,
               s.id_servicio, s.prefijo, s.nombre
        FROM turno t
        INNER JOIN servicio s
            ON t.id_servicio = s.id_servicio
        WHERE t.hora_llamado IS NOT NULL
        ORDER BY t.hora_llamado DESC
        LIMIT 1
        """;

    try (Connection conexion = DatabaseConfig.obtenerConexion();
         PreparedStatement ps = conexion.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (!rs.next()) {
            return null;
        }

        Servicio servicio = new Servicio(
                rs.getInt("id_servicio"),
                rs.getString("prefijo"),
                rs.getString("nombre"),
                null,
                true
        );

        Turno turno = new Turno();
        turno.setCodigo(rs.getString("codigo"));
        turno.setServicio(servicio);
        turno.setEstado(
                EstadoTurno.valueOf(rs.getString("estado"))
        );

        int idVentanilla = rs.getInt("id_ventanilla");
        turno.setIdVentanilla(
                rs.wasNull() ? null : idVentanilla
        );

        Timestamp llamado = rs.getTimestamp("hora_llamado");

        if (llamado != null) {
            turno.setFechaLlamado(llamado.toLocalDateTime());
        }

        return turno;
    }
}

public List<Turno> obtenerTurnosEnAtencion()
        throws SQLException {

    String sql = """
        SELECT t.codigo, t.estado, t.id_ventanilla,
               t.hora_llamado, t.hora_inicio_atencion,
               s.id_servicio, s.prefijo, s.nombre
        FROM turno t
        INNER JOIN servicio s
            ON t.id_servicio = s.id_servicio
        WHERE t.estado = 'EN_ATENCION'
        ORDER BY t.hora_inicio_atencion DESC
        """;

    List<Turno> turnos = new ArrayList<>();

    try (Connection conexion = DatabaseConfig.obtenerConexion();
         PreparedStatement ps = conexion.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            Servicio servicio = new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getString("prefijo"),
                    rs.getString("nombre"),
                    null,
                    true
            );

            Turno turno = new Turno();
            turno.setCodigo(rs.getString("codigo"));
            turno.setServicio(servicio);
            turno.setEstado(
                    EstadoTurno.valueOf(rs.getString("estado"))
            );

            int idVentanilla = rs.getInt("id_ventanilla");
            turno.setIdVentanilla(
                    rs.wasNull() ? null : idVentanilla
            );

            Timestamp llamado = rs.getTimestamp("hora_llamado");
            if (llamado != null) {
                turno.setFechaLlamado(llamado.toLocalDateTime());
            }

            Timestamp inicio =
                    rs.getTimestamp("hora_inicio_atencion");

            if (inicio != null) {
                turno.setFechaInicioAtencion(
                        inicio.toLocalDateTime()
                );
            }

            turnos.add(turno);
        }
    }

    return turnos;
}


public Turno obtenerPorId(int idTurno) throws SQLException {

    String sql = """
        SELECT t.id_turno, t.codigo, t.estado,
               t.fecha_generacion, t.hora_generacion,
               t.hora_llamado, t.hora_inicio_atencion,
               t.hora_fin_atencion, t.id_ventanilla,
               t.id_funcionario,
               s.id_servicio, s.prefijo, s.nombre,
               p.nombre AS prioridad
        FROM turno t
        INNER JOIN servicio s
            ON t.id_servicio = s.id_servicio
        INNER JOIN prioridad p
            ON t.id_prioridad = p.id_prioridad
        WHERE t.id_turno = ?
        """;

    try (Connection conexion = DatabaseConfig.obtenerConexion();
         PreparedStatement ps = conexion.prepareStatement(sql)) {

        ps.setInt(1, idTurno);

        try (ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                return null;
            }

            Servicio servicio = new Servicio(
                rs.getInt("id_servicio"),
                rs.getString("prefijo"),
                rs.getString("nombre"),
                null,
                true
            );

            Turno turno = new Turno();

            turno.setId(rs.getInt("id_turno"));
            turno.setCodigo(rs.getString("codigo"));
            turno.setServicio(servicio);
            turno.setPrioridad(
                PrioridadTurno.valueOf(rs.getString("prioridad"))
            );
            turno.setEstado(
                EstadoTurno.valueOf(rs.getString("estado"))
            );

            Timestamp generacion = rs.getTimestamp("hora_generacion");
            if (generacion != null) {
                turno.setFechaGeneracion(generacion.toLocalDateTime());
            }

            Timestamp llamado = rs.getTimestamp("hora_llamado");
            if (llamado != null) {
                turno.setFechaLlamado(llamado.toLocalDateTime());
            }

            Timestamp inicio = rs.getTimestamp("hora_inicio_atencion");
            if (inicio != null) {
                turno.setFechaInicioAtencion(inicio.toLocalDateTime());
            }

            Timestamp fin = rs.getTimestamp("hora_fin_atencion");
            if (fin != null) {
                turno.setFechaFinAtencion(fin.toLocalDateTime());
            }

            int idVentanilla = rs.getInt("id_ventanilla");
            if (!rs.wasNull()) {
                turno.setIdVentanilla(idVentanilla);
            }

            int idFuncionario = rs.getInt("id_funcionario");
            if (!rs.wasNull()) {
                turno.setIdFuncionario(idFuncionario);
            }

            return turno;
        }
    }
}

public void reclamarTurno(
        int idTurno,
        int idFuncionario,
        int idVentanilla) throws SQLException {

    String sqlActualizar = """
        UPDATE turno
        SET hora_llamado = ?
        WHERE id_turno = ?
          AND estado = 'LLAMADO'
          AND id_funcionario = ?
          AND id_ventanilla = ?
        """;

    String sqlHistorial = """
        INSERT INTO historial_turno
            (id_turno, estado, fecha_hora,
             id_funcionario, id_ventanilla, comentario)
        VALUES (?, 'LLAMADO', ?, ?, ?, ?)
        """;

    LocalDateTime ahora = LocalDateTime.now();

    try (Connection conexion = DatabaseConfig.obtenerConexion()) {

        conexion.setAutoCommit(false);

        try {
            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlActualizar)) {

                ps.setTimestamp(1, Timestamp.valueOf(ahora));
                ps.setInt(2, idTurno);
                ps.setInt(3, idFuncionario);
                ps.setInt(4, idVentanilla);

                if (ps.executeUpdate() != 1) {
                    throw new IllegalStateException(
                        "El turno no está llamado o no pertenece "
                        + "a esta ventanilla."
                    );
                }
            }

            try (PreparedStatement ps =
                    conexion.prepareStatement(sqlHistorial)) {

                ps.setInt(1, idTurno);
                ps.setTimestamp(2, Timestamp.valueOf(ahora));
                ps.setInt(3, idFuncionario);
                ps.setInt(4, idVentanilla);
                ps.setString(5, "Turno vuelto a llamar");

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
