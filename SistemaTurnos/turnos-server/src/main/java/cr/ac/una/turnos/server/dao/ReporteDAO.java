
package cr.ac.una.turnos.server.dao;

import cr.ac.una.turnos.server.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteDAO {

    public List<Map<String, Object>> obtenerTurnosAtendidos(
            Date desde, Date hasta) throws SQLException {

        String sql = """
            SELECT t.codigo,
                   s.nombre AS servicio,
                   t.fecha_generacion,
                   t.hora_generacion,
                   t.hora_llamado,
                   t.hora_inicio_atencion,
                   t.hora_fin_atencion,
                   v.nombre AS ventanilla,
                   f.nombre_completo AS funcionario,
                   t.estado
            FROM turno t
            INNER JOIN servicio s
                ON t.id_servicio = s.id_servicio
            LEFT JOIN ventanilla v
                ON t.id_ventanilla = v.id_ventanilla
            LEFT JOIN funcionario f
                ON t.id_funcionario = f.id_funcionario
            WHERE t.hora_inicio_atencion IS NOT NULL
              AND t.fecha_generacion BETWEEN ? AND ?
            ORDER BY t.fecha_generacion, t.hora_generacion
            """;

        List<Map<String, Object>> filas = new ArrayList<>();

        try (Connection cn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, desde);
            ps.setDate(2, hasta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("codigo", rs.getString("codigo"));
                    fila.put("servicio", rs.getString("servicio"));
                    fila.put("fechaGeneracion", rs.getDate("fecha_generacion"));
                    fila.put("horaGeneracion", rs.getTimestamp("hora_generacion"));
                    fila.put("horaLlamado", rs.getTimestamp("hora_llamado"));
                    fila.put("horaInicio", rs.getTimestamp("hora_inicio_atencion"));
                    fila.put("horaFin", rs.getTimestamp("hora_fin_atencion"));
                    fila.put("ventanilla", rs.getString("ventanilla"));
                    fila.put("funcionario", rs.getString("funcionario"));
                    fila.put("estado", rs.getString("estado"));
                    filas.add(fila);
                }
            }
        }

        return filas;
    }

    public List<Map<String, Object>> obtenerAtencionPorFuncionario(
            Date desde, Date hasta) throws SQLException {

        String sql = """
            SELECT f.id_funcionario,
                   f.nombre_completo,
                   COUNT(t.id_turno) AS cantidad
            FROM funcionario f
            LEFT JOIN turno t
                ON t.id_funcionario = f.id_funcionario
               AND t.hora_inicio_atencion IS NOT NULL
               AND t.fecha_generacion BETWEEN ? AND ?
            GROUP BY f.id_funcionario, f.nombre_completo
            ORDER BY cantidad DESC, f.nombre_completo
            """;

        return ejecutarConsultaAgrupada(sql, desde, hasta,
                "idFuncionario", "nombreCompleto", "cantidad");
    }

    public List<Map<String, Object>> obtenerAtencionPorServicio(
            Date desde, Date hasta) throws SQLException {

        String sql = """
            SELECT s.id_servicio,
                   s.nombre AS servicio,
                   COUNT(t.id_turno) AS cantidad
            FROM servicio s
            LEFT JOIN turno t
                ON t.id_servicio = s.id_servicio
               AND t.hora_inicio_atencion IS NOT NULL
               AND t.fecha_generacion BETWEEN ? AND ?
            GROUP BY s.id_servicio, s.nombre
            ORDER BY cantidad DESC, s.nombre
            """;

        return ejecutarConsultaAgrupada(sql, desde, hasta,
                "idServicio", "servicio", "cantidad");
    }

    public List<Map<String, Object>> obtenerTiemposEspera(
            Date desde, Date hasta) throws SQLException {

        String sql = """
            SELECT t.codigo,
                   f.nombre_completo AS funcionario,
                   s.nombre AS servicio,
                   TIMESTAMPDIFF(
                       SECOND,
                       t.hora_generacion,
                       t.hora_inicio_atencion
                   ) AS segundos
            FROM turno t
            INNER JOIN servicio s
                ON t.id_servicio = s.id_servicio
            LEFT JOIN funcionario f
                ON t.id_funcionario = f.id_funcionario
            WHERE t.hora_inicio_atencion IS NOT NULL
              AND t.fecha_generacion BETWEEN ? AND ?
            ORDER BY t.fecha_generacion, t.hora_generacion
            """;

        return ejecutarConsultaTiempo(sql, desde, hasta);
    }

    public List<Map<String, Object>> obtenerTiemposAtencion(
            Date desde, Date hasta) throws SQLException {

        String sql = """
            SELECT t.codigo,
                   f.nombre_completo AS funcionario,
                   s.nombre AS servicio,
                   TIMESTAMPDIFF(
                       SECOND,
                       t.hora_inicio_atencion,
                       t.hora_fin_atencion
                   ) AS segundos
            FROM turno t
            INNER JOIN servicio s
                ON t.id_servicio = s.id_servicio
            LEFT JOIN funcionario f
                ON t.id_funcionario = f.id_funcionario
            WHERE t.hora_inicio_atencion IS NOT NULL
              AND t.hora_fin_atencion IS NOT NULL
              AND t.fecha_generacion BETWEEN ? AND ?
            ORDER BY t.fecha_generacion, t.hora_generacion
            """;

        return ejecutarConsultaTiempo(sql, desde, hasta);
    }

    private List<Map<String, Object>> ejecutarConsultaAgrupada(
            String sql, Date desde, Date hasta,
            String campoId, String campoNombre,
            String campoCantidad) throws SQLException {

        List<Map<String, Object>> filas = new ArrayList<>();

        try (Connection cn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, desde);
            ps.setDate(2, hasta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put(campoId, rs.getInt(1));
                    fila.put(campoNombre, rs.getString(2));
                    fila.put(campoCantidad, rs.getInt(3));
                    filas.add(fila);
                }
            }
        }

        return filas;
    }

    private List<Map<String, Object>> ejecutarConsultaTiempo(
            String sql, Date desde, Date hasta) throws SQLException {

        List<Map<String, Object>> filas = new ArrayList<>();

        try (Connection cn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, desde);
            ps.setDate(2, hasta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("codigo", rs.getString("codigo"));
                    fila.put("funcionario", rs.getString("funcionario"));
                    fila.put("servicio", rs.getString("servicio"));
                    fila.put("segundos", rs.getLong("segundos"));
                    filas.add(fila);
                }
            }
        }

        return filas;
    }
}

