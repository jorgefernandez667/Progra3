
package cr.ac.una.turnos.operador;

import cr.ac.una.turnos.common.domain.Funcionario;
import cr.ac.una.turnos.common.domain.Usuario;
import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.enums.TipoUsuario;
import cr.ac.una.turnos.common.network.TcpSessionClient;
import cr.ac.una.turnos.common.protocol.Response;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private final JTextField campoUsuario;
    private final JPasswordField campoContrasena;
    private final JButton botonIngresar;

    public LoginFrame() {

        setTitle("Inicio de sesión - Sistema de Turnos");
        setSize(400, 230);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel titulo = new JLabel(
                "SISTEMA DE ATENCIÓN DE TURNOS",
                SwingConstants.CENTER
        );

        titulo.setFont(new java.awt.Font(
                "Segoe UI", java.awt.Font.BOLD, 16
        ));

        campoUsuario = new JTextField();
        campoContrasena = new JPasswordField();
        botonIngresar = new JButton("Iniciar sesión");

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCampos.add(new JLabel("Usuario:"));
        panelCampos.add(campoUsuario);
        panelCampos.add(new JLabel("Contraseña:"));
        panelCampos.add(campoContrasena);

        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        panelPrincipal.add(titulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCampos, BorderLayout.CENTER);
        panelPrincipal.add(botonIngresar, BorderLayout.SOUTH);

        add(panelPrincipal);

        botonIngresar.addActionListener(e -> iniciarSesion());
        getRootPane().setDefaultButton(botonIngresar);
    }

    private void iniciarSesion() {

        String usuario = campoUsuario.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debe ingresar el usuario y la contraseña.",
                    "Campos obligatorios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        botonIngresar.setEnabled(false);
        TcpSessionClient cliente = null;

        try {
            cliente = new TcpSessionClient();

            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.LOGIN,
                    Map.of(
                            "nombreUsuario", usuario,
                            "contrasena", contrasena
                    )
            );

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            Map<?, ?> datos = (Map<?, ?>) respuesta.getDatos();

            int idUsuario = ((Number) datos.get("idUsuario")).intValue();
            String nombreUsuario = datos.get("nombreUsuario").toString();

            TipoUsuario tipo = TipoUsuario.valueOf(
                    datos.get("tipo").toString()
            );

            Usuario usuarioActual = new Usuario(
                    idUsuario,
                    nombreUsuario,
                    tipo,
                    true
            );

            String nombreCompleto = datos.get("nombreCompleto").toString();

            Funcionario funcionario = new Funcionario(
                    datos.get("idFuncionario") == null
                            ? 0
                            : ((Number) datos.get("idFuncionario")).intValue(),
                    usuarioActual,
                    nombreCompleto
            );

            if (tipo == TipoUsuario.OPERADOR) {

                if (datos.get("idVentanilla") == null) {
                    throw new IllegalStateException(
                            "No se asignó una ventanilla al operador."
                    );
                }

                int idVentanilla =
                        ((Number) datos.get("idVentanilla")).intValue();

                OperadorTurnosFrame ventana = new OperadorTurnosFrame(
                        cliente,
                        funcionario,
                        idVentanilla
                );

                ventana.setLocationRelativeTo(null);
                ventana.setVisible(true);

            } else if (tipo == TipoUsuario.ADMINISTRADOR) {

                AdminFrame ventana = new AdminFrame(
                        cliente,
                        usuarioActual,
                        nombreCompleto
                );

                ventana.setLocationRelativeTo(null);
                ventana.setVisible(true);

            } else {
                throw new IllegalStateException(
                        "Tipo de usuario no reconocido."
                );
            }

            cliente = null;
            dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al iniciar sesión",
                    JOptionPane.ERROR_MESSAGE
            );

            campoContrasena.setText("");

        } finally {

            if (cliente != null) {
                try {
                    cliente.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            botonIngresar.setEnabled(true);
        }
    }
}
