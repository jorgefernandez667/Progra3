package cr.ac.una.turnos.common.dto;

import cr.ac.una.turnos.common.enums.TipoUsuario;

public class UsuarioDTO {

    private int id;
    private String nombreUsuario;
    private TipoUsuario tipo;
    private boolean activo;

    public UsuarioDTO() {
    }

    public UsuarioDTO(int id, String nombreUsuario,
                      TipoUsuario tipo, boolean activo) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.tipo = tipo;
        this.activo = activo;
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

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
