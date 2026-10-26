package cr.ac.una.turnos.server.dao;

import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.server.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;

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

}
