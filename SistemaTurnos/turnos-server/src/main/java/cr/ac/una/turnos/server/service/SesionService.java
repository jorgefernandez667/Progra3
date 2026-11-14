
package cr.ac.una.turnos.server.service;

import cr.ac.una.turnos.common.domain.Funcionario;
import cr.ac.una.turnos.common.enums.TipoUsuario;
import cr.ac.una.turnos.server.dao.TurnoDAO;
import cr.ac.una.turnos.server.dao.UsuarioDAO;
import cr.ac.una.turnos.server.dao.VentanillaDAO;

import java.sql.SQLException;

public class SesionService {

    private final TurnoDAO turnoDAO = new TurnoDAO();
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
                    "Usuario o contraseña incorrectos."
            );
        }

        Integer idVentanilla = null;

        if (funcionario.getUsuario().getTipo() == TipoUsuario.OPERADOR) {
            idVentanilla =
                    ventanillaDAO.asignarVentanilla(funcionario.getId());

            // La ventanilla estaba DISPONIBLE, así que cualquier turno
            // que aún figure activo en ella es un huérfano (por ejemplo,
            // tras una caída del servidor). Se devuelve a la cola.
            try {
                int liberados =
                        turnoDAO.liberarTurnosActivosDeVentanilla(
                                idVentanilla
                        );

                if (liberados > 0) {
                    System.out.println(
                            "Se liberaron " + liberados
                            + " turno(s) huérfano(s) de la ventanilla "
                            + idVentanilla + "."
                    );
                }

            } catch (SQLException | RuntimeException e) {
                // No dejar la ventanilla ocupada si el inicio falla.
                try {
                    ventanillaDAO.liberarVentanilla(
                            idVentanilla,
                            funcionario.getId()
                    );
                } catch (SQLException ignorada) {
                    e.addSuppressed(ignorada);
                }
                throw e;
            }
        }

        funcionarioActual = funcionario;
        idVentanillaActual = idVentanilla;
    }

    /**
     * Cierra la sesión actual. Si el operador tenía un turno llamado o
     * en atención, se libera antes de soltar la ventanilla.
     *
     * @return cantidad de turnos liberados
     */
    public synchronized int cerrarSesion() throws SQLException {

        if (funcionarioActual == null) {
            throw new IllegalStateException(
                    "No hay una sesión activa."
            );
        }

        int turnosLiberados = 0;

        if (idVentanillaActual != null) {
            turnosLiberados =
                    turnoDAO.liberarTurnosActivosDeVentanilla(
                            idVentanillaActual
                    );

            ventanillaDAO.liberarVentanilla(
                    idVentanillaActual,
                    funcionarioActual.getId()
            );
        }

        funcionarioActual = null;
        idVentanillaActual = null;

        return turnosLiberados;
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
