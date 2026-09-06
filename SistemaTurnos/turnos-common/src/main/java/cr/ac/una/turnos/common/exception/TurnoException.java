package cr.ac.una.turnos.common.exception;

public class TurnoException extends Exception {

    public TurnoException(String mensaje) {
        super(mensaje);
    }

    public TurnoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}