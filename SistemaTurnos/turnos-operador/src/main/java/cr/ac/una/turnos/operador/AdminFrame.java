
package cr.ac.una.turnos.operador;

import cr.ac.una.turnos.common.domain.Usuario;
import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.network.TcpSessionClient;
import cr.ac.una.turnos.common.protocol.Response;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.*;

public class AdminFrame extends JFrame {

    private final TcpSessionClient cliente;
    private final Usuario usuario;
    private final String nombreCompleto;

    public AdminFrame(
            TcpSessionClient cliente,
            Usuario usuario,
            String nombreCompleto) {

        this.cliente = cliente;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;

        setTitle("Administración - Sistema de Turnos");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JLabel titulo = new JLabel(
                "PANEL DE ADMINISTRACIÓN",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel bienvenida = new JLabel(
                "Bienvenido, " + nombreCompleto
                + " | Usuario: " + usuario.getNombreUsuario(),
                SwingConstants.CENTER
        );

        JLabel informacion = new JLabel(
                "<html><center>"
                + "Sesión de administrador iniciada correctamente.<br><br>"
                + "Desde este panel se gestionarán las ventanillas,"
                + " usuarios y reportes del sistema."
                + "</center></html>",
                SwingConstants.CENTER
        );

        JButton botonCerrarSesion = new JButton("Cerrar sesión");

        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.add(titulo, BorderLayout.NORTH);
        panelSuperior.add(bienvenida, BorderLayout.SOUTH);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(informacion, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.add(botonCerrarSesion);

        setLayout(new BorderLayout(15, 15));
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        botonCerrarSesion.addActionListener(e -> cerrarSesion());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarSesion();
            }
        });
    }

    private void cerrarSesion() {

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cerrar la sesión?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.LOGOUT,
                    null
            );

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            cliente.close();
            dispose();
            new LoginFrame().setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cerrar sesión: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
