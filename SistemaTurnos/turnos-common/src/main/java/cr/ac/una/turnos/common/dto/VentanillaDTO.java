package cr.ac.una.turnos.common.dto;

import cr.ac.una.turnos.common.enums.EstadoVentanilla;

import java.util.ArrayList;
import java.util.List;

public class VentanillaDTO {

    private int id;
    private String nombre;
    private EstadoVentanilla estado;
    private int idFuncionario;
    private String nombreFuncionario;

    /** Servicios que atiende; vacío significa que atiende todos. */
    private List<Integer> idServicios = new ArrayList<>();

    public VentanillaDTO() {
    }

    public VentanillaDTO(int id, String nombre,
                         EstadoVentanilla estado,
                         int idFuncionario,
                         String nombreFuncionario) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.idFuncionario = idFuncionario;
        this.nombreFuncionario = nombreFuncionario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNombreFuncionario() {
        return nombreFuncionario;
    }

    public void setNombreFuncionario(String nombreFuncionario) {
        this.nombreFuncionario = nombreFuncionario;
    }

    public List<Integer> getIdServicios() {
        return idServicios;
    }

    public void setIdServicios(List<Integer> idServicios) {
        this.idServicios = idServicios == null
                ? new ArrayList<>()
                : idServicios;
    }
}
