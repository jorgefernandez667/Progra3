package cr.ac.una.turnos.common.dto;

import java.time.LocalDateTime;

public class LlamadoDTO {

    private int idTurno;
    private String codigoTurno;
    private String nombreServicio;
    private int idVentanilla;
    private String nombreVentanilla;
    private LocalDateTime fechaLlamado;

    public LlamadoDTO() {
    }

    public LlamadoDTO(int idTurno, String codigoTurno,
                      String nombreServicio, int idVentanilla,
                      String nombreVentanilla,
                      LocalDateTime fechaLlamado) {
        this.idTurno = idTurno;
        this.codigoTurno = codigoTurno;
        this.nombreServicio = nombreServicio;
        this.idVentanilla = idVentanilla;
        this.nombreVentanilla = nombreVentanilla;
        this.fechaLlamado = fechaLlamado;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public String getCodigoTurno() {
        return codigoTurno;
    }

    public void setCodigoTurno(String codigoTurno) {
        this.codigoTurno = codigoTurno;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
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

    public LocalDateTime getFechaLlamado() {
        return fechaLlamado;
    }

    public void setFechaLlamado(LocalDateTime fechaLlamado) {
        this.fechaLlamado = fechaLlamado;
    }
}