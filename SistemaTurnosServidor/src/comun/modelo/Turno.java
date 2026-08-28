package comun.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Turno {

    private Integer idTurno;
    private String codigo; // ej. "A023"
    private Integer idServicio;
    private EstadoTurno estado;
    private LocalDate fechaGeneracion;
    private LocalDateTime horaGeneracion;
    private LocalDateTime horaLlamado;
    private LocalDateTime horaInicioAtencion;
    private LocalDateTime horaFinAtencion;
    private Integer idVentanilla;
    private Integer idFuncionario;

    public Turno() {
    }

    /** Constructor para crear un turno nuevo, recién generado. */
    public Turno(String codigo, Integer idServicio) {
        this.codigo = codigo;
        this.idServicio = idServicio;
        this.estado = EstadoTurno.EN_ESPERA;
        this.fechaGeneracion = LocalDate.now();
        this.horaGeneracion = LocalDateTime.now();
    }

    public Turno(Integer idTurno, String codigo, Integer idServicio, EstadoTurno estado,
                 LocalDate fechaGeneracion, LocalDateTime horaGeneracion, LocalDateTime horaLlamado,
                 LocalDateTime horaInicioAtencion, LocalDateTime horaFinAtencion,
                 Integer idVentanilla, Integer idFuncionario) {
        this.idTurno = idTurno;
        this.codigo = codigo;
        this.idServicio = idServicio;
        this.estado = estado;
        this.fechaGeneracion = fechaGeneracion;
        this.horaGeneracion = horaGeneracion;
        this.horaLlamado = horaLlamado;
        this.horaInicioAtencion = horaInicioAtencion;
        this.horaFinAtencion = horaFinAtencion;
        this.idVentanilla = idVentanilla;
        this.idFuncionario = idFuncionario;
    }

    public Integer getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(Integer idTurno) {
        this.idTurno = idTurno;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public LocalDateTime getHoraGeneracion() {
        return horaGeneracion;
    }

    public void setHoraGeneracion(LocalDateTime horaGeneracion) {
        this.horaGeneracion = horaGeneracion;
    }

    public LocalDateTime getHoraLlamado() {
        return horaLlamado;
    }

    public void setHoraLlamado(LocalDateTime horaLlamado) {
        this.horaLlamado = horaLlamado;
    }

    public LocalDateTime getHoraInicioAtencion() {
        return horaInicioAtencion;
    }

    public void setHoraInicioAtencion(LocalDateTime horaInicioAtencion) {
        this.horaInicioAtencion = horaInicioAtencion;
    }

    public LocalDateTime getHoraFinAtencion() {
        return horaFinAtencion;
    }

    public void setHoraFinAtencion(LocalDateTime horaFinAtencion) {
        this.horaFinAtencion = horaFinAtencion;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Turno)) return false;
        Turno turno = (Turno) o;
        return Objects.equals(idTurno, turno.idTurno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTurno);
    }

    @Override
    public String toString() {
        return "Turno{idTurno=" + idTurno
                + ", codigo='" + codigo + '\''
                + ", estado=" + estado
                + ", idServicio=" + idServicio
                + ", idVentanilla=" + idVentanilla + '}';
    }
}