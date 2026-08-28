package comun.modelo;

import java.util.Objects;

public class Ventanilla {

    private Integer idVentanilla;
    private String nombre;
    private EstadoVentanilla estado;
    private Integer idFuncionario; // puede ser null si nadie la está usando

    public Ventanilla() {
    }

    public Ventanilla(String nombre, EstadoVentanilla estado) {
        this.nombre = nombre;
        this.estado = estado;
    }

    public Ventanilla(Integer idVentanilla, String nombre, EstadoVentanilla estado, Integer idFuncionario) {
        this.idVentanilla = idVentanilla;
        this.nombre = nombre;
        this.estado = estado;
        this.idFuncionario = idFuncionario;
    }

    public Integer getIdVentanilla() {
        return idVentanilla;
    }

    public void setIdVentanilla(Integer idVentanilla) {
        this.idVentanilla = idVentanilla;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoVentanilla getEstado() {
        return estado;
    }

    public void setEstado(EstadoVentanilla estado) {
        this.estado = estado;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ventanilla)) return false;
        Ventanilla that = (Ventanilla) o;
        return Objects.equals(idVentanilla, that.idVentanilla);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVentanilla);
    }

    @Override
    public String toString() {
        return "Ventanilla{idVentanilla=" + idVentanilla
                + ", nombre='" + nombre + '\''
                + ", estado=" + estado
                + ", idFuncionario=" + idFuncionario + '}';
    }
}