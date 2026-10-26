
package cr.ac.una.turnos.common.domain;

public class Funcionario {

    private int id;
    private Usuario usuario;
    private String nombreCompleto;

    public Funcionario() {
    }

    public Funcionario(int id, Usuario usuario, String nombreCompleto) {
        this.id = id;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
}

