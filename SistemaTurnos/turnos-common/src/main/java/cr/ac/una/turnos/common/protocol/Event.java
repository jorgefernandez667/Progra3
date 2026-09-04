package cr.ac.una.turnos.common.protocol;

import cr.ac.una.turnos.common.enums.TipoMensaje;

public class Event extends Mensaje {

    private String idEvento;

    public Event() {
        super();
    }

    public Event(String idEvento, TipoMensaje tipo, Object datos) {
        super(tipo, datos);
        this.idEvento = idEvento;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }
}