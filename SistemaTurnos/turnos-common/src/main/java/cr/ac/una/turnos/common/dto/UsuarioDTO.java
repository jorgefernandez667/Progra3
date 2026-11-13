
package cr.ac.una.turnos.common.dto;

import cr.ac.una.turnos.common.enums.TipoUsuario;

public class UsuarioDTO {

    private int id;
    private String nombreUsuario;
    private TipoUsuario rol;
    private boolean activo;
    private Integer idFuncionario;
    private String nombreCompleto;

    public UsuarioDTO() {
    }

    public UsuarioDTO(
            int id,
            String nombreUsuario,
            TipoUsuario rol,
            boolean activo,
            Integer idFuncionario,
            String nombreCompleto) {

        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
        this.activo = activo;
        this.idFuncionario = idFuncionario;
        this.nombreCompleto = nombreCompleto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public TipoUsuario getRol() {
        return rol;
    }

    public void setRol(TipoUsuario rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
}

