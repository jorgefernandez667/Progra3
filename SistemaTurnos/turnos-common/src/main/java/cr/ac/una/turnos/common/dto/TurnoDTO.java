package cr.ac.una.turnos.common.dto;

import cr.ac.una.turnos.common.enums.EstadoTurno;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import java.time.LocalDateTime;

public class TurnoDTO {

    private int id;
    private String codigo;
    private int idServicio;
    private String nombreServicio;
    private PrioridadTurno prioridad;
    private EstadoTurno estado;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaLlamado;
    private LocalDateTime fechaInicioAtencion;
    private LocalDateTime fechaFinAtencion;
    private int idVentanilla;
    private String nombreVentanilla;
    private int idFuncionario;

    public TurnoDTO() {
    }

    public TurnoDTO(int id, String codigo, int idServicio,
                    String nombreServicio, PrioridadTurno prioridad,
                    EstadoTurno estado, LocalDateTime fechaGeneracion,
                    LocalDateTime fechaLlamado,
                    LocalDateTime fechaInicioAtencion,
                    LocalDateTime fechaFinAtencion,
                    int idVentanilla, String nombreVentanilla,
                    int idFuncionario) {

        this.id = id;
        this.codigo = codigo;
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaLlamado = fechaLlamado;
        this.fechaInicioAtencion = fechaInicioAtencion;
        this.fechaFinAtencion = fechaFinAtencion;
        this.idVentanilla = idVentanilla;
        this.nombreVentanilla = nombreVentanilla;
        this.idFuncionario = idFuncionario;
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

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
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

    public void setFechaInicioAtencion(LocalDateTime fechaInicioAtencion) {
        this.fechaInicioAtencion = fechaInicioAtencion;
    }

    public LocalDateTime getFechaFinAtencion() {
        return fechaFinAtencion;
    }

    public void setFechaFinAtencion(LocalDateTime fechaFinAtencion) {
        this.fechaFinAtencion = fechaFinAtencion;
    }

    public int getIdVentanilla() {
        return idVentanilla;
    }

    public void setIdVentanilla(int idVentanilla) {
        this.idVentanilla = idVentanilla;
    }

    public String getNombreVentanilla() {
        return nombreVentanilla;
    }

    public void setNombreVentanilla(String nombreVentanilla) {
        this.nombreVentanilla = nombreVentanilla;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }
}