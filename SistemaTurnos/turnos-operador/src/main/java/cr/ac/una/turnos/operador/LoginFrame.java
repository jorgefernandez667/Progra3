
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

        String contrasena = new String(
                campoContrasena.getPassword()
        );

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

            Map<String, Object> datos = Map.of(
                    "nombreUsuario", usuario,
                    "contrasena", contrasena
            );

            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.LOGIN,
                    datos
            );

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(
                        respuesta.getMensaje()
                );
            }

            Map<?, ?> datosRespuesta =
                    (Map<?, ?>) respuesta.getDatos();

            int idFuncionario = ((Number)
                    datosRespuesta.get("idFuncionario")).intValue();

            int idUsuario = ((Number)
                    datosRespuesta.get("idUsuario")).intValue();

            int idVentanilla = ((Number)
                    datosRespuesta.get("idVentanilla")).intValue();

            String nombreCompleto =
                    datosRespuesta.get("nombreCompleto").toString();

            String nombreUsuario =
                    datosRespuesta.get("nombreUsuario").toString();

            TipoUsuario tipo = TipoUsuario.valueOf(
                    datosRespuesta.get("tipo").toString()
            );

            Usuario usuarioActual = new Usuario(
                    idUsuario,
                    nombreUsuario,
                    tipo,
                    true
            );

            Funcionario funcionario = new Funcionario(
                    idFuncionario,
                    usuarioActual,
                    nombreCompleto
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Bienvenido, " + funcionario.getNombreCompleto()
                    + "\nVentanilla asignada: " + idVentanilla,
                    "Inicio de sesión exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            OperadorTurnosFrame ventanaOperador =
                    new OperadorTurnosFrame(
                            cliente,
                            funcionario,
                            idVentanilla
                    );

            ventanaOperador.setLocationRelativeTo(null);
            ventanaOperador.setVisible(true);

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
