package cr.ac.una.turnos.common.domain;

import cr.ac.una.turnos.common.enums.EstadoVentanilla;

public class Ventanilla {

    private int id;
    private String nombre;
    private EstadoVentanilla estado;
    private Usuario usuario;
    private Turno turnoActual;

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
    
    public Turno getTurnoActual() {
    return turnoActual;
}
    
}