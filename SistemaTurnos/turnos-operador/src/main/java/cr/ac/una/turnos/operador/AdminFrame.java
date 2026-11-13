
package cr.ac.una.turnos.operador;

import cr.ac.una.turnos.common.domain.Usuario;
import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.network.TcpSessionClient;
import cr.ac.una.turnos.common.protocol.Response;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminFrame extends JFrame {

    private final TcpSessionClient cliente;
    private final Usuario usuario;
    private final String nombreCompleto;

    private final DefaultTableModel modeloVentanillas;
    private final DefaultTableModel modeloUsuarios;

    private final JTable tablaVentanillas;
    private final JTable tablaUsuarios;

    private final JButton botonActualizarVentanillas;
    private final JButton botonCambiarEstado;
    private final JButton botonActualizarUsuarios;
    private final JButton botonCrearOperador;

    public AdminFrame(
            TcpSessionClient cliente,
            Usuario usuario,
            String nombreCompleto) {

        this.cliente = cliente;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;

        setTitle("Administración - Sistema de Turnos");
        setSize(900, 580);
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

        modeloVentanillas = new DefaultTableModel(
                new Object[]{"ID", "Ventanilla", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaVentanillas = new JTable(modeloVentanillas);
        tablaVentanillas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        modeloUsuarios = new DefaultTableModel(
                new Object[]{
                    "ID", "Usuario", "Rol",
                    "Funcionario", "Nombre completo"
                }, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaUsuarios = new JTable(modeloUsuarios);
        tablaUsuarios.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JTabbedPane pestañas = new JTabbedPane();

        // Pestaña de ventanillas
        JPanel panelVentanillas = new JPanel(new BorderLayout(10, 10));
        panelVentanillas.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        panelVentanillas.add(
                new JScrollPane(tablaVentanillas),
                BorderLayout.CENTER
        );

        botonActualizarVentanillas = new JButton("Actualizar");
        botonCambiarEstado = new JButton("Cambiar estado");

        JPanel botonesVentanillas = new JPanel(new FlowLayout());
        botonesVentanillas.add(botonActualizarVentanillas);
        botonesVentanillas.add(botonCambiarEstado);

        panelVentanillas.add(
                botonesVentanillas,
                BorderLayout.SOUTH
        );

        // Pestaña de usuarios
        JPanel panelUsuarios = new JPanel(new BorderLayout(10, 10));
        panelUsuarios.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        panelUsuarios.add(
                new JScrollPane(tablaUsuarios),
                BorderLayout.CENTER
        );

        botonActualizarUsuarios = new JButton("Actualizar usuarios");
        botonCrearOperador = new JButton("Crear operador");

        JPanel botonesUsuarios = new JPanel(new FlowLayout());
        botonesUsuarios.add(botonActualizarUsuarios);
        botonesUsuarios.add(botonCrearOperador);

        panelUsuarios.add(botonesUsuarios, BorderLayout.SOUTH);

        pestañas.addTab("Ventanillas", panelVentanillas);
        pestañas.addTab("Usuarios", panelUsuarios);

        JButton botonCerrarSesion = new JButton("Cerrar sesión");

        JPanel panelInferior = new JPanel(new FlowLayout());
        panelInferior.add(botonCerrarSesion);

        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(pestañas, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        botonActualizarVentanillas.addActionListener(
                e -> cargarVentanillas()
        );

        botonCambiarEstado.addActionListener(
                e -> cambiarEstado()
        );

        botonActualizarUsuarios.addActionListener(
                e -> cargarUsuarios()
        );

        botonCrearOperador.addActionListener(
                e -> mostrarFormularioOperador()
        );

        botonCerrarSesion.addActionListener(
                e -> cerrarSesion()
        );

        pestañas.addChangeListener(e -> {
            if (pestañas.getSelectedIndex() == 1) {
                cargarUsuarios();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarSesion();
            }
        });

        cargarVentanillas();
    }

    private void cargarVentanillas() {

        botonActualizarVentanillas.setEnabled(false);
        botonCambiarEstado.setEnabled(false);

        try {
            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.OBTENER_VENTANILLAS,
                    null
            );

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            modeloVentanillas.setRowCount(0);

            List<?> ventanillas = (List<?>) respuesta.getDatos();

            if (ventanillas != null) {
                for (Object elemento : ventanillas) {
                    Map<?, ?> datos = (Map<?, ?>) elemento;

                    modeloVentanillas.addRow(new Object[]{
                        datos.get("id"),
                        datos.get("nombre"),
                        datos.get("estado")
                    });
                }
            }

        } catch (Exception e) {
            mostrarError("No fue posible consultar las ventanillas.", e);
        } finally {
            botonActualizarVentanillas.setEnabled(true);
            botonCambiarEstado.setEnabled(true);
        }
    }

    private void cargarUsuarios() {

        botonActualizarUsuarios.setEnabled(false);

        try {
            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.OBTENER_USUARIOS,
                    null
            );

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            modeloUsuarios.setRowCount(0);

            List<?> usuarios = (List<?>) respuesta.getDatos();

            if (usuarios != null) {
                for (Object elemento : usuarios) {
                    Map<?, ?> datos = (Map<?, ?>) elemento;

                    modeloUsuarios.addRow(new Object[]{
                        datos.get("id"),
                        datos.get("nombreUsuario"),
                        datos.get("rol"),
                        datos.get("idFuncionario") == null
                                ? "No aplica"
                                : datos.get("idFuncionario"),
                        datos.get("nombreCompleto") == null
                                ? "Sin funcionario"
                                : datos.get("nombreCompleto")
                    });
                }
            }

        } catch (Exception e) {
            mostrarError("No fue posible consultar los usuarios.", e);
        } finally {
            botonActualizarUsuarios.setEnabled(true);
        }
    }

    private void mostrarFormularioOperador() {

        JTextField campoUsuario = new JTextField(20);
        JPasswordField campoContrasena = new JPasswordField(20);
        JTextField campoNombre = new JTextField(20);

        JPanel formulario = new JPanel(new GridLayout(0, 1, 5, 5));

        formulario.add(new JLabel("Nombre de usuario:"));
        formulario.add(campoUsuario);

        formulario.add(new JLabel("Contraseña (mínimo 8 caracteres):"));
        formulario.add(campoContrasena);

        formulario.add(new JLabel("Nombre completo del funcionario:"));
        formulario.add(campoNombre);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Registrar nuevo operador",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String nombreUsuario = campoUsuario.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());
        String nombreCompleto = campoNombre.getText().trim();

        if (nombreUsuario.isBlank()
                || contrasena.isBlank()
                || nombreCompleto.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Todos los campos son obligatorios.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (contrasena.length() < 8) {
            JOptionPane.showMessageDialog(
                    this,
                    "La contraseña debe tener al menos 8 caracteres.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.CREAR_USUARIO,
                    Map.of(
                            "nombreUsuario", nombreUsuario,
                            "contrasena", contrasena,
                            "nombreCompleto", nombreCompleto
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

            cargarUsuarios();

        } catch (Exception e) {
            mostrarError("No fue posible crear el operador.", e);
        }
    }

    private void cambiarEstado() {

        int fila = tablaVentanillas.getSelectedRow();

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
                modeloVentanillas.getValueAt(fila, 0).toString()
        );

        String estadoActual =
                modeloVentanillas.getValueAt(fila, 2).toString();

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
            mostrarError("No fue posible cambiar el estado.", e);
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
            mostrarError("Error al cerrar sesión.", e);
        }
    }

    private void mostrarError(String mensaje, Exception e) {
        JOptionPane.showMessageDialog(
                this,
                mensaje + "\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}