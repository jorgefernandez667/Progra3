package cr.ac.una.turnos.common.domain;

public class Servicio {

    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}