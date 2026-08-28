package comun.modelo;

import java.util.Objects;

public class Funcionario {

    private Integer idFuncionario;
    private Integer idUsuario;
    private String nombreCompleto;

    public Funcionario() {
    }

    public Funcionario(Integer idUsuario, String nombreCompleto) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
    }

    public Funcionario(Integer idFuncionario, Integer idUsuario, String nombreCompleto) {
        this.idFuncionario = idFuncionario;
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Funcionario)) return false;
        Funcionario that = (Funcionario) o;
        return Objects.equals(idFuncionario, that.idFuncionario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFuncionario);
    }

    @Override
    public String toString() {
        return "Funcionario{idFuncionario=" + idFuncionario
                + ", nombreCompleto='" + nombreCompleto + '\''
                + ", idUsuario=" + idUsuario + '}';
    }
}