package cr.ac.una.turnos.common.protocol;

import cr.ac.una.turnos.common.enums.TipoMensaje;

public class Request extends Mensaje {

    private String idSolicitud;

    public Request() {
        super();
    }

    public Request(String idSolicitud, TipoMensaje tipo, Object datos) {
        super(tipo, datos);
        this.idSolicitud = idSolicitud;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }
}