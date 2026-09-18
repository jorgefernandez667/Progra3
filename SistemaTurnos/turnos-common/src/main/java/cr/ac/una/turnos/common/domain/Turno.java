package cr.ac.una.turnos.common.domain;

import cr.ac.una.turnos.common.enums.EstadoTurno;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import java.time.LocalDateTime;

public class Turno {

    private int id;
    private String codigo;
    private Servicio servicio;
    private PrioridadTurno prioridad;
    private EstadoTurno estado;
    private LocalDateTime fechaGeneracion;

    public void llamar() {
        if (estado != EstadoTurno.EN_ESPERA) {
            throw new IllegalStateException(
                    "Solo se puede llamar un turno que está en espera."
            );
        }

        estado = EstadoTurno.LLAMADO;
    }

    public void iniciarAtencion() {
        if (estado != EstadoTurno.LLAMADO) {
            throw new IllegalStateException(
                    "Solo se puede iniciar la atención de un turno llamado."
            );
        }

        estado = EstadoTurno.EN_ATENCION;
    }

    public void finalizarAtencion() {
        if (estado != EstadoTurno.EN_ATENCION) {
            throw new IllegalStateException(
                    "Solo se puede finalizar la atención de un turno en atención."
            );
        }

        estado = EstadoTurno.FINALIZADO;
    }
    public String getCodigo() {
    return codigo;
}

public void setCodigo(String codigo) {
    this.codigo = codigo;
}

public Servicio getServicio() {
    return servicio;
}

public void setServicio(Servicio servicio) {
    this.servicio = servicio;
}

public PrioridadTurno getPrioridad() {
    return prioridad;
}

public void setPrioridad(PrioridadTurno prioridad) {
    this.prioridad = prioridad;
}

public EstadoTurno getEstado() {
    return estado;
}

public void setEstado(EstadoTurno estado) {
    this.estado = estado;
}

public LocalDateTime getFechaGeneracion() {
    return fechaGeneracion;
}

public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
    this.fechaGeneracion = fechaGeneracion;
}
}