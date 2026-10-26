
package cr.ac.una.turnos.common.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class AsignacionVentanilla {

    private int id;
    private Funcionario funcionario;
    private Ventanilla ventanilla;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public AsignacionVentanilla() {
    }

    public AsignacionVentanilla(
            int id,
            Funcionario funcionario,
            Ventanilla ventanilla,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin) {
        this.id = id;
        this.funcionario = funcionario;
        this.ventanilla = ventanilla;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Ventanilla getVentanilla() {
        return ventanilla;
    }

    public void setVentanilla(Ventanilla ventanilla) {
        this.ventanilla = ventanilla;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}
