
package cr.ac.una.turnos.operador;

import cr.ac.una.turnos.common.domain.Usuario;
import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.network.TcpSessionClient;
import cr.ac.una.turnos.common.protocol.Response;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminFrame extends JFrame {

    private final TcpSessionClient cliente;
    private final Usuario usuario;
    private final String nombreCompleto;

    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JButton botonActualizar;
    private final JButton botonCambiarEstado;

    public AdminFrame(
            TcpSessionClient cliente,
            Usuario usuario,
            String nombreCompleto) {

        this.cliente = cliente;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;

        setTitle("Administración - Sistema de Turnos");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JLabel titulo = new JLabel(
                "PANEL DE ADMINISTRACIÓN",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel bienvenida = new JLabel(
                "Administrador: " + nombreCompleto
                + " | Usuario: " + usuario.getNombreUsuario(),
                SwingConstants.CENTER
        );

        JPanel panelSuperior = new JPanel(new BorderLayout(5, 10));
        panelSuperior.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 10, 15)
        );
        panelSuperior.add(titulo, BorderLayout.NORTH);
        panelSuperior.add(bienvenida, BorderLayout.SOUTH);

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Ventanilla", "Estado"}, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        tabla.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(tabla);

        JLabel subtitulo = new JLabel(
                "Administración de ventanillas"
        );
        subtitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel panelTabla = new JPanel(new BorderLayout(10, 10));
        panelTabla.setBorder(
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        );
        panelTabla.add(subtitulo, BorderLayout.NORTH);
        panelTabla.add(scroll, BorderLayout.CENTER);

        botonActualizar = new JButton("Actualizar");
        botonCambiarEstado = new JButton("Cambiar estado");
        JButton botonCerrarSesion = new JButton("Cerrar sesión");

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(botonActualizar);
        panelBotones.add(botonCambiarEstado);
        panelBotones.add(botonCerrarSesion);

        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(panelTabla, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        botonActualizar.addActionListener(e -> cargarVentanillas());
        botonCambiarEstado.addActionListener(e -> cambiarEstado());
        botonCerrarSesion.addActionListener(e -> cerrarSesion());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarSesion();
            }
        });

        cargarVentanillas();
    }

    private void cargarVentanillas() {

        botonActualizar.setEnabled(false);
        botonCambiarEstado.setEnabled(false);

        try {
            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.OBTENER_VENTANILLAS,
                    null
            );

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            modelo.setRowCount(0);

            List<?> ventanillas = (List<?>) respuesta.getDatos();

            if (ventanillas != null) {
                for (Object elemento : ventanillas) {

                    Map<?, ?> datos = (Map<?, ?>) elemento;

                    modelo.addRow(new Object[]{
                        datos.get("id"),
                        datos.get("nombre"),
                        datos.get("estado")
                    });
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible consultar las ventanillas:\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            botonActualizar.setEnabled(true);
            botonCambiarEstado.setEnabled(true);
        }
    }

    private void cambiarEstado() {

        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una ventanilla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int idVentanilla = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString()
        );

        String estadoActual = modelo.getValueAt(fila, 2).toString();

        if ("OCUPADA".equals(estadoActual)) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se puede cambiar el estado de una ventanilla ocupada.",
                    "Operación no permitida",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String[] estados = {"DISPONIBLE", "INACTIVA"};

        JComboBox<String> selector = new JComboBox<>(estados);
        selector.setSelectedItem(estadoActual);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                selector,
                "Nuevo estado de la ventanilla",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String nuevoEstado = selector.getSelectedItem().toString();

        if (nuevoEstado.equals(estadoActual)) {
            return;
        }

        try {
            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.CAMBIAR_ESTADO_VENTANILLA,
                    Map.of(
                            "idVentanilla", idVentanilla,
                            "estado", nuevoEstado
                    )
            );

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            JOptionPane.showMessageDialog(
                    this,
                    respuesta.getMensaje(),
                    "Operación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarVentanillas();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible cambiar el estado:\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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
                    "Error al cerrar sesión:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

