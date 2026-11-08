package cr.ac.una.turnos.server.service;

import cr.ac.una.turnos.common.domain.Funcionario;
import cr.ac.una.turnos.server.dao.UsuarioDAO;
import cr.ac.una.turnos.server.dao.VentanillaDAO;

import java.sql.SQLException;

public class SesionService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final VentanillaDAO ventanillaDAO = new VentanillaDAO();

    private Funcionario funcionarioActual;
    private Integer idVentanillaActual;

    public synchronized void iniciarSesion(
            String nombreUsuario,
            String contrasena) throws SQLException {

        if (funcionarioActual != null) {
            throw new IllegalStateException(
                    "Ya existe una sesión activa."
            );
        }

        if (nombreUsuario == null || nombreUsuario.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe completar usuario y contraseña."
            );
        }

        Funcionario funcionario =
                usuarioDAO.autenticar(nombreUsuario, contrasena);

        if (funcionario == null) {
            throw new IllegalArgumentException(
                    "Usuario o contraseña incorrectos, "
                    + "o el usuario no tiene permisos de operador."
            );
        }

        int idVentanilla =
                ventanillaDAO.asignarVentanilla(funcionario.getId());

        funcionarioActual = funcionario;
        idVentanillaActual = idVentanilla;
    }

    public synchronized void cerrarSesion() throws SQLException {

        if (funcionarioActual == null || idVentanillaActual == null) {
            throw new IllegalStateException(
                    "No hay una sesión activa."
            );
        }

        ventanillaDAO.liberarVentanilla(
                idVentanillaActual,
                funcionarioActual.getId()
        );

        funcionarioActual = null;
        idVentanillaActual = null;
    }

    public synchronized boolean haySesionActiva() {
        return funcionarioActual != null;
    }

    public synchronized Funcionario getFuncionarioActual() {
        return funcionarioActual;
    }

    public synchronized Integer getIdVentanillaActual() {
        return idVentanillaActual;
    }
}