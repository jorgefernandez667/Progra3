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

    try {
        int cantidad = turnoDAO.contarPorPrefijo(prefijo);

        return String.format("%s%03d", prefijo, cantidad + 1);

    } catch (SQLException e) {
        throw new IllegalStateException(
                "No se pudo generar el código del turno.",
                e
        );
    }
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

  


public synchronized Turno llamarSiguienteTurno(
        int idFuncionario,
        int idVentanilla) {

    if (idFuncionario <= 0 || idVentanilla <= 0) {
        throw new IllegalArgumentException(
            "El funcionario y la ventanilla deben ser válidos."
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

}
