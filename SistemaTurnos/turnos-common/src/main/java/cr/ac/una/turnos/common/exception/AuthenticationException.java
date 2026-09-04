package cr.ac.una.turnos.common.exception;

public class AuthenticationException extends Exception {

    public AuthenticationException(String mensaje) {
        super(mensaje);
    }

    public AuthenticationException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}