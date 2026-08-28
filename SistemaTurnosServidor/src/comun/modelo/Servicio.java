package comun.modelo;

import java.util.Objects;

public class Servicio {

    private Integer idServicio;
    private String nombre;
    private char prefijo;

    public Servicio() {
    }

    public Servicio(String nombre, char prefijo) {
        this.nombre = nombre;
        this.prefijo = prefijo;
    }

    public Servicio(Integer idServicio, String nombre, char prefijo) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.prefijo = prefijo;
    }

    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public char getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(char prefijo) {
        this.prefijo = prefijo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Servicio)) return false;
        Servicio servicio = (Servicio) o;
        return Objects.equals(idServicio, servicio.idServicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idServicio);
    }

    @Override
    public String toString() {
        return "Servicio{idServicio=" + idServicio
                + ", nombre='" + nombre + '\''
                + ", prefijo=" + prefijo + '}';
    }
}
