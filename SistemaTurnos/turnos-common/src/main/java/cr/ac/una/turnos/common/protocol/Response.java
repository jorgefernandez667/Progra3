package cr.ac.una.turnos.common.protocol;

import cr.ac.una.turnos.common.enums.TipoMensaje;

public class Response extends Mensaje {

    private String idSolicitud;
    private boolean exitoso;
    private String mensaje;

    public Response() {
        super();
    }

    public Response(String idSolicitud, TipoMensaje tipo,
                    boolean exitoso, String mensaje, Object datos) {
        super(tipo, datos);
        this.idSolicitud = idSolicitud;
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}