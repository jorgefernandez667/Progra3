package cr.ac.una.turnos.common.protocol;

import cr.ac.una.turnos.common.enums.TipoMensaje;

public class Mensaje {

    private TipoMensaje tipo;
    private Object datos;

    public Mensaje() {
    }

    public Mensaje(TipoMensaje tipo, Object datos) {
        this.tipo = tipo;
        this.datos = datos;
    }

    public TipoMensaje getTipo() {
        return tipo;
    }

    public void setTipo(TipoMensaje tipo) {
        this.tipo = tipo;
    }

    public Object getDatos() {
        return datos;
    }

    public void setDatos(Object datos) {
        this.datos = datos;
    }
}