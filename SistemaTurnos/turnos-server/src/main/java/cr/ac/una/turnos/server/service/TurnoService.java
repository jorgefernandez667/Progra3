package cr.ac.una.turnos.server.service;

import cr.ac.una.turnos.common.domain.Servicio;
import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.common.enums.EstadoTurno;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import cr.ac.una.turnos.common.domain.Ventanilla;

public class TurnoService {

    private final List<Turno> turnos = new ArrayList<>();

    public void agregarTurno(Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException(
                    "No se puede agregar un turno nulo."
            );
        }

        turnos.add(turno);
    }

    public Turno generarTurno(
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

        String codigo = generarCodigo(servicio);

        Turno turno = new Turno();
        
        turno.setCodigo(codigo);
        turno.setServicio(servicio);
        turno.setPrioridad(prioridad);
        turno.setEstado(EstadoTurno.EN_ESPERA);
        turno.setFechaGeneracion(LocalDateTime.now());

        agregarTurno(turno);

        return turno;
    }

    private String generarCodigo(Servicio servicio) {

        String prefijo = servicio.getCodigo();

        long cantidad = turnos.stream()
                .filter(t -> t.getServicio() != null)
                .filter(t -> prefijo.equals(t.getServicio().getCodigo()))
                .count();

        return String.format(
                "%s%03d",
                prefijo,
                cantidad + 1
        );
    }

    public List<Turno> obtenerTurnos() {
        return List.copyOf(turnos);
    }
    
    public List<Turno> obtenerTurnosPendientes() {
    return turnos.stream()
            .filter(turno -> turno.getEstado() == EstadoTurno.EN_ESPERA)
            .toList();
}
    
    public Turno obtenerSiguienteTurno() {
    return turnos.stream()
            .filter(turno -> turno.getEstado() == EstadoTurno.EN_ESPERA)
            .findFirst()
            .orElse(null);
}
    
    public Turno llamarSiguienteTurno() {
    Turno siguiente = obtenerSiguienteTurno();

    if (siguiente == null) {
        return null;
    }

    siguiente.llamar();

    return siguiente;
}
 
    public Turno asignarSiguienteTurno(Ventanilla ventanilla) {

    if (ventanilla == null) {
        throw new IllegalArgumentException(
                "La ventanilla no puede ser nula."
        );
    }

    Turno siguiente = obtenerSiguienteTurno();

    if (siguiente == null) {
        return null;
    }

    ventanilla.asignarTurno(siguiente);

    return siguiente;
}
    
}