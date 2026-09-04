package cr.ac.una.turnos.common.exception;

public class ServerException extends Exception {

    public ServerException(String mensaje) {
        super(mensaje);
    }

    public ServerException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}