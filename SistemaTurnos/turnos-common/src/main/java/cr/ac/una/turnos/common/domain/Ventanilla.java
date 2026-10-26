package cr.ac.una.turnos.common.domain;

import cr.ac.una.turnos.common.enums.EstadoVentanilla;

public class Ventanilla {

    private int id;
    private String nombre;
    private EstadoVentanilla estado;
    private Turno turnoActual;

    public Ventanilla() {
        this.estado = EstadoVentanilla.DISPONIBLE;
    }

    public Ventanilla(int id, String nombre,
                      EstadoVentanilla estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }

    public void asignarTurno(Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException(
                    "No se puede asignar un turno nulo."
            );
        }

        if (estado != EstadoVentanilla.DISPONIBLE) {
            throw new IllegalStateException(
                    "La ventanilla no está disponible."
            );
        }

        turno.llamar();
        turnoActual = turno;
        estado = EstadoVentanilla.OCUPADA;
    }

    public void iniciarAtencion() {
        if (turnoActual == null) {
            throw new IllegalStateException(
                    "No hay un turno asignado a la ventanilla."
            );
        }

        turnoActual.iniciarAtencion();
    }

    public void finalizarAtencion() {
        if (turnoActual == null) {
            throw new IllegalStateException(
                    "No hay un turno asignado a la ventanilla."
            );
        }

        turnoActual.finalizarAtencion();
        turnoActual = null;
        estado = EstadoVentanilla.DISPONIBLE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoVentanilla getEstado() {
        return estado;
    }

    public void setEstado(EstadoVentanilla estado) {
        this.estado = estado;
    }

    public Turno getTurnoActual() {
        return turnoActual;
    }

    public void setTurnoActual(Turno turnoActual) {
        this.turnoActual = turnoActual;
    }
}
