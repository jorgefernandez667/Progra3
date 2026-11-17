package cr.ac.una.turnos.server.service;

import cr.ac.una.turnos.common.domain.Servicio;
import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.common.domain.Ventanilla;
import cr.ac.una.turnos.common.enums.EstadoTurno;
import cr.ac.una.turnos.common.enums.EstadoVentanilla;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import cr.ac.una.turnos.server.dao.TurnoDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TurnoService {

    private final List<Turno> turnos = new ArrayList<>();
    private final TurnoDAO turnoDAO = new TurnoDAO();
    
public Turno consultarUltimoTurnoLlamado()
        throws SQLException {

    return turnoDAO.obtenerUltimoTurnoLlamado();
}

public List<Turno> consultarTurnosEnAtencion()
        throws SQLException {

    return turnoDAO.obtenerTurnosEnAtencion();
}


    public synchronized void agregarTurno(Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException(
                    "No se puede agregar un turno nulo."
            );
        }

        turnos.add(turno);
    }


public synchronized Turno generarTurno(
        Servicio servicio,
        PrioridadTurno prioridad) {

    if (servicio == null) {
        throw new IllegalArgumentException(
                "Debe seleccionar un servicio."
        );
    }

    if (prioridad == null) {
        throw new IllegalArgumentException(
                "Debe seleccionar una prioridad."
        );
    }

    if (servicio.getCodigo() == null
            || servicio.getCodigo().isBlank()) {
        throw new IllegalArgumentException(
                "El servicio debe tener un código válido."
        );
    }

    Turno turno = new Turno();
    turno.setServicio(servicio);
    turno.setPrioridad(prioridad);
    turno.setEstado(EstadoTurno.EN_ESPERA);
    turno.setFechaGeneracion(LocalDateTime.now());

    try {
        // El código (A001, A002...) lo asigna la base de datos dentro de
        // una transacción, por eso es seguro con varios clientes a la vez
        // y reinicia cada día.
        turnoDAO.insertarConCodigoDiario(turno);
    } catch (SQLException e) {
        throw new IllegalStateException(
                "No se pudo guardar el turno en la base de datos.",
                e
        );
    }

    turnos.add(turno);

    return turno;
}


    public synchronized List<Turno> obtenerTurnos() {
        return List.copyOf(turnos);
    }

    public synchronized List<Turno> obtenerTurnosPendientes() {
        return turnos.stream()
                .filter(turno ->
                        turno.getEstado() == EstadoTurno.EN_ESPERA)
                .toList();
    }

    public synchronized Turno obtenerSiguienteTurno() {
        return turnos.stream()
                .filter(turno ->
                        turno.getEstado() == EstadoTurno.EN_ESPERA)
                .findFirst()
                .orElse(null);
    }

  


/**
 * Cancela los turnos EN_ESPERA generados antes de hoy.
 *
 * @return cantidad de turnos cancelados
 */
public int cancelarTurnosDeDiasAnteriores() {
    try {
        return turnoDAO.cancelarTurnosPendientesDeDiasAnteriores(
            java.time.LocalDate.now()
        );
    } catch (SQLException e) {
        throw new IllegalStateException(
            "No se pudieron cancelar los turnos de días anteriores.",
            e
        );
    }
}

public synchronized Turno llamarSiguienteTurno(
        int idFuncionario,
        int idVentanilla) {

    if (idFuncionario <= 0 || idVentanilla <= 0) {
        throw new IllegalArgumentException(
            "El funcionario y la ventanilla deben ser válidos."
        );
    }

    // Red de seguridad: si el servidor estuvo encendido pasada la
    // medianoche, nunca se llama un turno de un día anterior.
    try {
        cancelarTurnosDeDiasAnteriores();
    } catch (RuntimeException e) {
        System.err.println(
            "No se pudieron cancelar turnos antiguos: " + e.getMessage()
        );
    }

    try {
        return turnoDAO.llamarSiguienteTurno(
            idFuncionario,
            idVentanilla
        );

    } catch (SQLException e) {
        throw new IllegalStateException(
            "No se pudo llamar al siguiente turno.",
            e
        );
    }
}




    public synchronized Turno asignarSiguienteTurno(
            Ventanilla ventanilla) {

        if (ventanilla == null) {
            throw new IllegalArgumentException(
                    "La ventanilla no puede ser nula."
            );
        }

        if (ventanilla.getEstado() != EstadoVentanilla.DISPONIBLE) {
            throw new IllegalStateException(
                    "La ventanilla no está disponible."
            );
        }

        Turno siguiente = obtenerSiguienteTurno();

        if (siguiente == null) {
            return null;
        }

        ventanilla.asignarTurno(siguiente);
        siguiente.setIdVentanilla(ventanilla.getId());

        return siguiente;
    }

    public synchronized void iniciarAtencion(Ventanilla ventanilla) {
        if (ventanilla == null) {
            throw new IllegalArgumentException(
                    "La ventanilla no puede ser nula."
            );
        }

        if (ventanilla.getTurnoActual() == null) {
            throw new IllegalStateException(
                    "La ventanilla no tiene un turno asignado."
            );
        }

        ventanilla.iniciarAtencion();
    }
    
    

public synchronized void iniciarAtencionTurno(Turno turno) {

    if (turno == null) {
        throw new IllegalArgumentException(
                "No hay un turno seleccionado."
        );
    }

    if (turno.getEstado() != EstadoTurno.LLAMADO) {
        throw new IllegalStateException(
                "Solo se puede iniciar la atención de un turno llamado."
        );
    }

    LocalDateTime ahora = LocalDateTime.now();

    try {
        turnoDAO.cambiarEstadoConHistorial(
                turno,
                EstadoTurno.LLAMADO,
                EstadoTurno.EN_ATENCION,
                ahora,
                "Inicio de atención"
        );

        turno.setEstado(EstadoTurno.EN_ATENCION);
        turno.setFechaInicioAtencion(ahora);

    } catch (SQLException e) {
        throw new IllegalStateException(
                "No se pudo iniciar la atención del turno.",
                e
        );
    }
}



    public synchronized void finalizarAtencion(Ventanilla ventanilla) {
        if (ventanilla == null) {
            throw new IllegalArgumentException(
                    "La ventanilla no puede ser nula."
            );
        }

        if (ventanilla.getTurnoActual() == null) {
            throw new IllegalStateException(
                    "La ventanilla no tiene un turno asignado."
            );
        }

        ventanilla.finalizarAtencion();
    }
    public synchronized List<Turno> consultarTurnosPendientes()
        throws SQLException {
    return turnoDAO.obtenerTurnosPendientes();
}

/** Pendientes de los servicios que atiende una ventanilla. */
public synchronized List<Turno> consultarTurnosPendientes(
        Integer idVentanilla) throws SQLException {
    return turnoDAO.obtenerTurnosPendientes(idVentanilla);
}
  

public synchronized void finalizarAtencionTurno(Turno turno) {

    if (turno == null) {
        throw new IllegalArgumentException(
                "No hay un turno seleccionado."
        );
    }

    if (turno.getEstado() != EstadoTurno.EN_ATENCION) {
        throw new IllegalStateException(
                "Solo se puede finalizar un turno que está en atención."
        );
    }

    LocalDateTime ahora = LocalDateTime.now();

    try {
        turnoDAO.cambiarEstadoConHistorial(
                turno,
                EstadoTurno.EN_ATENCION,
                EstadoTurno.FINALIZADO,
                ahora,
                "Atención finalizada"
        );

        turno.setEstado(EstadoTurno.FINALIZADO);
        turno.setFechaFinAtencion(ahora);

    } catch (SQLException e) {
        throw new IllegalStateException(
                "No se pudo finalizar la atención del turno.",
                e
        );
    }
}

public synchronized List<Turno> consultarUltimosTurnosLlamados()
        throws SQLException {
    return turnoDAO.obtenerUltimosTurnosLlamados();
}


private final cr.ac.una.turnos.server.dao.ServicioDAO servicioDAO =
        new cr.ac.una.turnos.server.dao.ServicioDAO();

/** Todos los servicios (administración). */
public List<Servicio> consultarServicios() throws SQLException {
    return servicioDAO.obtenerTodos();
}

/** Solo los servicios activos: los que se ofrecen al generar turnos. */
public List<Servicio> consultarServiciosActivos() throws SQLException {
    return servicioDAO.obtenerTodos().stream()
            .filter(Servicio::isActivo)
            .toList();
}

public int crearServicio(String prefijo, String nombre)
        throws SQLException {

    if (prefijo == null || !prefijo.matches("[A-Z]")) {
        throw new IllegalArgumentException(
            "El prefijo debe ser una sola letra de la A a la Z."
        );
    }

    return servicioDAO.crear(prefijo, validarNombreServicio(nombre));
}

public void actualizarServicio(int idServicio, String nombre,
        boolean activo) throws SQLException {

    servicioDAO.actualizar(
        idServicio, validarNombreServicio(nombre), activo
    );
}

private String validarNombreServicio(String nombre) {
    String limpio = nombre == null ? "" : nombre.trim();

    if (limpio.length() < 3 || limpio.length() > 50) {
        throw new IllegalArgumentException(
            "El nombre del servicio debe tener entre 3 y 50 caracteres."
        );
    }

    return limpio;
}

public Turno generarTurnoTCP(
        int idServicio,
        PrioridadTurno prioridad) throws SQLException {

    Servicio servicio = servicioDAO.obtenerPorId(idServicio);

    if (servicio == null) {
        throw new IllegalArgumentException(
            "El servicio seleccionado no existe."
        );
    }

    if (!servicio.isActivo()) {
        throw new IllegalArgumentException(
            "El servicio seleccionado no está disponible."
        );
    }

    if (prioridad == null) {
        throw new IllegalArgumentException(
            "Debe seleccionar una prioridad."
        );
    }

    return generarTurno(servicio, prioridad);
}


public Turno consultarTurnoPorId(int idTurno) throws SQLException {

    if (idTurno <= 0) {
        throw new IllegalArgumentException(
            "El ID del turno debe ser válido."
        );
    }

    return turnoDAO.obtenerPorId(idTurno);
}

public synchronized void reclamarTurno(
        Turno turno,
        int idFuncionario,
        int idVentanilla) {

    if (turno == null) {
        throw new IllegalArgumentException(
                "No hay un turno seleccionado."
        );
    }

    if (turno.getEstado() != EstadoTurno.LLAMADO) {
        throw new IllegalStateException(
                "Solo se puede reclamar un turno que está llamado."
        );
    }

    if (idFuncionario <= 0 || idVentanilla <= 0) {
        throw new IllegalArgumentException(
                "El funcionario y la ventanilla deben ser válidos."
        );
    }

    if (turno.getIdFuncionario() != idFuncionario
            || turno.getIdVentanilla() != idVentanilla) {
        throw new IllegalStateException(
                "El turno no pertenece a este funcionario o ventanilla."
        );
    }

    try {
        turnoDAO.reclamarTurno(
                turno.getId(),
                idFuncionario,
                idVentanilla
        );

        turno.setFechaLlamado(LocalDateTime.now());

    } catch (SQLException e) {
        throw new IllegalStateException(
                "No se pudo reclamar el turno.",
                e
        );
    }
}

}
