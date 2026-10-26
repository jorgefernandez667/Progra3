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
    private LocalDateTime fechaLlamado;
    private LocalDateTime fechaInicioAtencion;
    private LocalDateTime fechaFinAtencion;

    private Integer idVentanilla;
    private Integer idFuncionario;

    public Turno() {
        this.estado = EstadoTurno.EN_ESPERA;
        this.fechaGeneracion = LocalDateTime.now();
    }

    public Turno(int id, String codigo, Servicio servicio,
                 PrioridadTurno prioridad, EstadoTurno estado,
                 LocalDateTime fechaGeneracion) {

        this.id = id;
        this.codigo = codigo;
        this.servicio = servicio;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaGeneracion = fechaGeneracion;
    }

    public void llamar() {
        if (estado != EstadoTurno.EN_ESPERA) {
            throw new IllegalStateException(
                    "Solo se puede llamar un turno que está en espera."
            );
        }

        estado = EstadoTurno.LLAMADO;
        fechaLlamado = LocalDateTime.now();
    }

    public void iniciarAtencion() {
        if (estado != EstadoTurno.LLAMADO) {
            throw new IllegalStateException(
                    "Solo se puede iniciar la atención de un turno llamado."
            );
        }

        estado = EstadoTurno.EN_ATENCION;
        fechaInicioAtencion = LocalDateTime.now();
    }

    public void finalizarAtencion() {
        if (estado != EstadoTurno.EN_ATENCION) {
            throw new IllegalStateException(
                    "Solo se puede finalizar la atención de un turno en atención."
            );
        }

        estado = EstadoTurno.FINALIZADO;
        fechaFinAtencion = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDateTime getFechaLlamado() {
        return fechaLlamado;
    }

    public void setFechaLlamado(LocalDateTime fechaLlamado) {
        this.fechaLlamado = fechaLlamado;
    }

    public LocalDateTime getFechaInicioAtencion() {
        return fechaInicioAtencion;
    }

    public void setFechaInicioAtencion(
            LocalDateTime fechaInicioAtencion) {
        this.fechaInicioAtencion = fechaInicioAtencion;
    }

    public LocalDateTime getFechaFinAtencion() {
        return fechaFinAtencion;
    }

    public void setFechaFinAtencion(
            LocalDateTime fechaFinAtencion) {
        this.fechaFinAtencion = fechaFinAtencion;
    }

    public Integer getIdVentanilla() {
        return idVentanilla;
    }

    public void setIdVentanilla(Integer idVentanilla) {
        this.idVentanilla = idVentanilla;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }
}
