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

    String codigo = generarCodigo(servicio);

    Turno turno = new Turno();
    turno.setCodigo(codigo);
    turno.setServicio(servicio);
    turno.setPrioridad(prioridad);
    turno.setEstado(EstadoTurno.EN_ESPERA);
    turno.setFechaGeneracion(LocalDateTime.now());

    try {
        turnoDAO.insertar(turno);
    } catch (SQLException e) {
        throw new IllegalStateException(
                "No se pudo guardar el turno en la base de datos.",
                e
        );
    }

    turnos.add(turno);

    return turno;
}


    private String generarCodigo(Servicio servicio) {
        String prefijo = servicio.getCodigo();

        long cantidad = turnos.stream()
                .filter(t -> t.getServicio() != null)
                .filter(t -> prefijo.equals(
                        t.getServicio().getCodigo()))
                .count();

        return String.format("%s%03d", prefijo, cantidad + 1);
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

    public synchronized Turno llamarSiguienteTurno() {
        Turno siguiente = obtenerSiguienteTurno();

        if (siguiente == null) {
            return null;
        }

        siguiente.llamar();

        return siguiente;
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
}
