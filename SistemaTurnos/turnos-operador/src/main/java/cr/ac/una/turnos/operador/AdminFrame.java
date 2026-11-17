
package cr.ac.una.turnos.operador;

import cr.ac.una.turnos.common.domain.Usuario;
import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.network.TcpSessionClient;
import cr.ac.una.turnos.common.protocol.Response;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
    private final DefaultTableModel modeloReportes;

    private final JTable tablaVentanillas;
    private final JTable tablaUsuarios;
    private final JTable tablaReportes;

    private final JButton botonActualizarVentanillas;
    private final JButton botonCambiarEstado;
    private final JButton botonServicios;

    private final DefaultTableModel modeloServicios;
    private final JTable tablaServicios;
    private final JButton botonActualizarServicios;
    private final JButton botonNuevoServicio;
    private final JButton botonEditarServicio;
    private final JButton botonActivarServicio;

    /** Servicios existentes (id, código, nombre) y los de cada ventanilla. */
    private List<Map<?, ?>> serviciosExistentes = new java.util.ArrayList<>();
    private final Map<Integer, List<Integer>> serviciosDeVentanilla =
            new java.util.HashMap<>();
    private final JButton botonActualizarUsuarios;
    private final JButton botonCrearOperador;
    private final JButton botonGenerarReporte;

    private final JComboBox<String> selectorReporte;
    private final JTextField campoFechaDesde;
    private final JTextField campoFechaHasta;

    private final String[] tiposReporte = {
        "TURNOS_ATENDIDOS",
        "POR_FUNCIONARIO",
        "POR_SERVICIO",
        "TIEMPOS_ESPERA",
        "TIEMPOS_ATENCION"
    };

    public AdminFrame(
            TcpSessionClient cliente,
            Usuario usuario,
            String nombreCompleto) {

        this.cliente = cliente;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;

        setTitle("Administración - Sistema de Turnos");
        setSize(1100, 650);
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

        modeloVentanillas = crearModelo();
        tablaVentanillas = new JTable(modeloVentanillas);
        tablaVentanillas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        modeloUsuarios = crearModelo();
        tablaUsuarios = new JTable(modeloUsuarios);
        tablaUsuarios.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        modeloServicios = crearModelo();
        tablaServicios = new JTable(modeloServicios);
        tablaServicios.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        modeloReportes = crearModelo();
        tablaReportes = new JTable(modeloReportes);
        tablaReportes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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

        botonServicios = new JButton("Servicios que atiende");
        botonesVentanillas.add(botonServicios);

        panelVentanillas.add(botonesVentanillas, BorderLayout.SOUTH);

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

        // Pestaña de reportes
        JPanel panelReportes = new JPanel(new BorderLayout(10, 10));
        panelReportes.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        selectorReporte = new JComboBox<>(new String[]{
            "Turnos atendidos",
            "Atención por funcionario",
            "Turnos por servicio",
            "Tiempos de espera",
            "Tiempos de atención"
        });

        campoFechaDesde = new JTextField(10);
        campoFechaHasta = new JTextField(10);

        campoFechaDesde.setText(LocalDate.now().withDayOfMonth(1).toString());
        campoFechaHasta.setText(LocalDate.now().toString());

        botonGenerarReporte = new JButton("Generar reporte");

        filtros.add(new JLabel("Reporte:"));
        filtros.add(selectorReporte);
        filtros.add(new JLabel("Desde (AAAA-MM-DD):"));
        filtros.add(campoFechaDesde);
        filtros.add(new JLabel("Hasta (AAAA-MM-DD):"));
        filtros.add(campoFechaHasta);
        filtros.add(botonGenerarReporte);

        panelReportes.add(filtros, BorderLayout.NORTH);
        panelReportes.add(
                new JScrollPane(tablaReportes),
                BorderLayout.CENTER
        );

        pestañas.addTab("Ventanillas", panelVentanillas);
        pestañas.addTab("Usuarios", panelUsuarios);

        // Pestaña de servicios
        JPanel panelServicios = new JPanel(new BorderLayout(10, 10));
        panelServicios.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
        panelServicios.add(
                new JScrollPane(tablaServicios),
                BorderLayout.CENTER
        );

        botonActualizarServicios = new JButton("Actualizar");
        botonNuevoServicio = new JButton("Nuevo servicio");
        botonEditarServicio = new JButton("Cambiar nombre");
        botonActivarServicio = new JButton("Activar / desactivar");

        JPanel botonesServicios = new JPanel(new FlowLayout());
        botonesServicios.add(botonActualizarServicios);
        botonesServicios.add(botonNuevoServicio);
        botonesServicios.add(botonEditarServicio);
        botonesServicios.add(botonActivarServicio);
        panelServicios.add(botonesServicios, BorderLayout.SOUTH);

        pestañas.addTab("Servicios", panelServicios);
        pestañas.addTab("Reportes", panelReportes);

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

        botonServicios.addActionListener(e -> configurarServicios());

        botonActualizarServicios.addActionListener(
                e -> cargarServicios());
        botonNuevoServicio.addActionListener(e -> crearServicio());
        botonEditarServicio.addActionListener(
                e -> cambiarNombreServicio());
        botonActivarServicio.addActionListener(
                e -> alternarServicio());

        botonCambiarEstado.addActionListener(
                e -> cambiarEstado()
        );

        botonActualizarUsuarios.addActionListener(
                e -> cargarUsuarios()
        );

        botonCrearOperador.addActionListener(
                e -> mostrarFormularioOperador()
        );

        botonGenerarReporte.addActionListener(
                e -> generarReporte()
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
        cargarServicios();
    }

    private DefaultTableModel crearModelo() {
        return new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void cargarVentanillas() {

        botonActualizarVentanillas.setEnabled(false);
        botonCambiarEstado.setEnabled(false);
        botonServicios.setEnabled(false);

        try {
            cargarServiciosExistentes();

            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.OBTENER_VENTANILLAS,
                    null
            );

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            modeloVentanillas.setColumnIdentifiers(
                    new Object[]{"ID", "Ventanilla", "Estado",
                        "Servicios que atiende"}
            );
            modeloVentanillas.setRowCount(0);
            serviciosDeVentanilla.clear();

            List<?> ventanillas = (List<?>) respuesta.getDatos();

            if (ventanillas != null) {
                for (Object elemento : ventanillas) {
                    Map<?, ?> datos = (Map<?, ?>) elemento;

                    List<Integer> idServicios = new java.util.ArrayList<>();

                    if (datos.get("idServicios") instanceof List<?> ids) {
                        for (Object id : ids) {
                            idServicios.add(((Number) id).intValue());
                        }
                    }

                    int idVentanilla = ((Number) datos.get("id")).intValue();
                    serviciosDeVentanilla.put(idVentanilla, idServicios);

                    modeloVentanillas.addRow(new Object[]{
                        datos.get("id"),
                        datos.get("nombre"),
                        datos.get("estado"),
                        describirServicios(idServicios)
                    });
                }
            }

        } catch (Exception e) {
            mostrarError("No fue posible consultar las ventanillas.", e);
        } finally {
            botonActualizarVentanillas.setEnabled(true);
            botonCambiarEstado.setEnabled(true);
            botonServicios.setEnabled(true);
        }
    }

    private void cargarServicios() {

        try {
            cargarServiciosExistentes();

            modeloServicios.setColumnIdentifiers(
                    new Object[]{"ID", "Código", "Nombre", "Estado"}
            );
            modeloServicios.setRowCount(0);

            for (Map<?, ?> servicio : serviciosExistentes) {
                modeloServicios.addRow(new Object[]{
                    servicio.get("id"),
                    servicio.get("codigo"),
                    servicio.get("nombre"),
                    Boolean.TRUE.equals(servicio.get("activo"))
                            ? "ACTIVO" : "INACTIVO"
                });
            }

        } catch (Exception e) {
            mostrarError("No fue posible consultar los servicios.", e);
        }
    }

    private void crearServicio() {

        JTextField campoPrefijo = new JTextField(3);
        JTextField campoNombre = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Prefijo (una letra):"));
        panel.add(campoPrefijo);
        panel.add(new JLabel("Nombre:"));
        panel.add(campoNombre);

        int opcion = JOptionPane.showConfirmDialog(
                this, panel, "Nuevo servicio",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        enviarCambioServicio(
                TipoMensaje.CREAR_SERVICIO,
                Map.of(
                        "prefijo", campoPrefijo.getText().trim(),
                        "nombre", campoNombre.getText().trim()
                )
        );
    }

    private void cambiarNombreServicio() {

        int fila = tablaServicios.getSelectedRow();

        if (fila < 0) {
            avisarSeleccionarServicio();
            return;
        }

        Object nuevo = JOptionPane.showInputDialog(
                this,
                "Nuevo nombre del servicio "
                        + modeloServicios.getValueAt(fila, 1) + ":",
                "Cambiar nombre",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                modeloServicios.getValueAt(fila, 2)
        );

        if (nuevo == null) {
            return;
        }

        enviarCambioServicio(
                TipoMensaje.ACTUALIZAR_SERVICIO,
                Map.of(
                        "idServicio", idServicioSeleccionado(fila),
                        "nombre", nuevo.toString().trim(),
                        "activo", "ACTIVO".equals(
                                modeloServicios.getValueAt(fila, 3))
                )
        );
    }

    private void alternarServicio() {

        int fila = tablaServicios.getSelectedRow();

        if (fila < 0) {
            avisarSeleccionarServicio();
            return;
        }

        boolean activoAhora = "ACTIVO".equals(
                modeloServicios.getValueAt(fila, 3));

        int opcion = JOptionPane.showConfirmDialog(
                this,
                activoAhora
                        ? "¿Desactivar el servicio "
                        + modeloServicios.getValueAt(fila, 2) + "?\n"
                        + "Dejará de ofrecerse para nuevos turnos; los que "
                        + "ya están en espera se siguen atendiendo."
                        : "¿Activar el servicio "
                        + modeloServicios.getValueAt(fila, 2) + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        enviarCambioServicio(
                TipoMensaje.ACTUALIZAR_SERVICIO,
                Map.of(
                        "idServicio", idServicioSeleccionado(fila),
                        "nombre", modeloServicios.getValueAt(fila, 2)
                                .toString(),
                        "activo", !activoAhora
                )
        );
    }

    private int idServicioSeleccionado(int fila) {
        return Integer.parseInt(
                modeloServicios.getValueAt(fila, 0).toString());
    }

    private void avisarSeleccionarServicio() {
        JOptionPane.showMessageDialog(
                this, "Seleccione un servicio.", "Servicios",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void enviarCambioServicio(TipoMensaje tipo, Object datos) {

        try {
            Response respuesta = cliente.enviarSolicitud(tipo, datos);

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            cargarServicios();
            cargarVentanillas();

        } catch (Exception e) {
            mostrarError("No fue posible guardar el servicio.", e);
        }
    }

    private void cargarServiciosExistentes() throws Exception {

        Response respuesta = cliente.enviarSolicitud(
                TipoMensaje.OBTENER_SERVICIOS,
                Map.of("todos", true)
        );

        if (!respuesta.isExitoso()) {
            throw new IllegalStateException(respuesta.getMensaje());
        }

        serviciosExistentes = new java.util.ArrayList<>();

        if (respuesta.getDatos() instanceof List<?> lista) {
            for (Object elemento : lista) {
                serviciosExistentes.add((Map<?, ?>) elemento);
            }
        }
    }

    /** "Todos" si no hay configuración; si no, los códigos (A, B...). */
    private String describirServicios(List<Integer> idServicios) {

        if (idServicios.isEmpty()) {
            return "Todos";
        }

        StringBuilder texto = new StringBuilder();

        for (Map<?, ?> servicio : serviciosExistentes) {
            int id = ((Number) servicio.get("id")).intValue();

            if (idServicios.contains(id)) {
                if (texto.length() > 0) {
                    texto.append(", ");
                }
                texto.append(servicio.get("codigo"))
                     .append(" - ")
                     .append(servicio.get("nombre"));
            }
        }

        return texto.toString();
    }

    private void configurarServicios() {

        int fila = tablaVentanillas.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una ventanilla.",
                    "Servicios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int idVentanilla = Integer.parseInt(
                modeloVentanillas.getValueAt(fila, 0).toString()
        );
        String nombre = modeloVentanillas.getValueAt(fila, 1).toString();

        List<Integer> actuales = serviciosDeVentanilla.getOrDefault(
                idVentanilla, new java.util.ArrayList<>()
        );

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Servicios que atiende " + nombre + ":"));
        panel.add(Box.createVerticalStrut(8));

        List<JCheckBox> casillas = new java.util.ArrayList<>();
        List<Integer> ids = new java.util.ArrayList<>();

        for (Map<?, ?> servicio : serviciosExistentes) {
            int id = ((Number) servicio.get("id")).intValue();
            JCheckBox casilla = new JCheckBox(
                    servicio.get("codigo") + " - " + servicio.get("nombre"),
                    actuales.contains(id)
            );
            casillas.add(casilla);
            ids.add(id);
            panel.add(casilla);
        }

        panel.add(Box.createVerticalStrut(8));
        panel.add(new JLabel(
                "Si no marca ninguno, la ventanilla atiende todos."
        ));

        int opcion = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Servicios por ventanilla",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        List<Integer> elegidos = new java.util.ArrayList<>();

        for (int i = 0; i < casillas.size(); i++) {
            if (casillas.get(i).isSelected()) {
                elegidos.add(ids.get(i));
            }
        }

        try {
            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.ASIGNAR_SERVICIOS_VENTANILLA,
                    Map.of(
                            "idVentanilla", idVentanilla,
                            "idServicios", elegidos
                    )
            );

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            cargarVentanillas();

        } catch (Exception e) {
            mostrarError("No fue posible guardar los servicios.", e);
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

            modeloUsuarios.setColumnIdentifiers(new Object[]{
                "ID", "Usuario", "Rol",
                "Funcionario", "Nombre completo"
            });
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

    private void generarReporte() {

        LocalDate desde;
        LocalDate hasta;

        try {
            desde = LocalDate.parse(campoFechaDesde.getText().trim());
            hasta = LocalDate.parse(campoFechaHasta.getText().trim());

            if (desde.isAfter(hasta)) {
                JOptionPane.showMessageDialog(
                        this,
                        "La fecha inicial no puede ser posterior a la fecha final.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese las fechas en formato AAAA-MM-DD.",
                    "Formato de fecha incorrecto",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int indice = selectorReporte.getSelectedIndex();
        String tipo = tiposReporte[indice];

        botonGenerarReporte.setEnabled(false);

        try {
            Response respuesta = cliente.enviarSolicitud(
                    TipoMensaje.OBTENER_REPORTES,
                    Map.of(
                            "tipoReporte", tipo,
                            "fechaDesde", desde.toString(),
                            "fechaHasta", hasta.toString()
                    )
            );

            if (!respuesta.isExitoso()) {
                throw new IllegalStateException(respuesta.getMensaje());
            }

            mostrarResultadosReporte(tipo, respuesta.getDatos());

        } catch (Exception e) {
            mostrarError("No fue posible generar el reporte.", e);
        } finally {
            botonGenerarReporte.setEnabled(true);
        }
    }

    private void mostrarResultadosReporte(String tipo, Object datos) {

        modeloReportes.setRowCount(0);

        switch (tipo) {
            case "TURNOS_ATENDIDOS":
                modeloReportes.setColumnIdentifiers(new Object[]{
                    "Código", "Servicio", "Fecha generación",
                    "Hora generación", "Hora llamado",
                    "Inicio atención", "Fin atención",
                    "Ventanilla", "Funcionario", "Estado"
                });
                break;

            case "POR_FUNCIONARIO":
                modeloReportes.setColumnIdentifiers(new Object[]{
                    "ID funcionario", "Funcionario", "Cantidad"
                });
                break;

            case "POR_SERVICIO":
                modeloReportes.setColumnIdentifiers(new Object[]{
                    "ID servicio", "Servicio", "Cantidad"
                });
                break;

            case "TIEMPOS_ESPERA":
                modeloReportes.setColumnIdentifiers(new Object[]{
                    "Código", "Funcionario", "Servicio",
                    "Tiempo de espera"
                });
                break;

            case "TIEMPOS_ATENCION":
                modeloReportes.setColumnIdentifiers(new Object[]{
                    "Código", "Funcionario", "Servicio",
                    "Tiempo de atención"
                });
                break;

            default:
                throw new IllegalArgumentException("Reporte desconocido.");
        }

        if (datos instanceof List<?> resultados) {
            for (Object elemento : resultados) {

                Map<?, ?> fila = (Map<?, ?>) elemento;

                switch (tipo) {
                    case "TURNOS_ATENDIDOS":
    modeloReportes.addRow(new Object[]{
        fila.get("codigo"),
        fila.get("servicio"),
        formatoFecha(fila.get("fechaGeneracion")),
        formatoFechaHora(fila.get("horaGeneracion")),
        formatoFechaHora(fila.get("horaLlamado")),
        formatoFechaHora(fila.get("horaInicio")),
        formatoFechaHora(fila.get("horaFin")),
        fila.get("ventanilla"),
        fila.get("funcionario"),
        fila.get("estado")
    });
    break;

                    case "POR_FUNCIONARIO":
                        modeloReportes.addRow(new Object[]{
                            fila.get("idFuncionario"),
                            fila.get("nombreCompleto"),
                            fila.get("cantidad")
                        });
                        break;

                    case "POR_SERVICIO":
                        modeloReportes.addRow(new Object[]{
                            fila.get("idServicio"),
                            fila.get("servicio"),
                            fila.get("cantidad")
                        });
                        break;

                    case "TIEMPOS_ESPERA":
                    case "TIEMPOS_ATENCION":
                        modeloReportes.addRow(new Object[]{
                            fila.get("codigo"),
                            fila.get("funcionario"),
                            fila.get("servicio"),
                            formatoDuracion(fila.get("segundos"))
                        });
                        break;
                }
            }
        }

        if (modeloReportes.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se encontraron resultados para el periodo seleccionado.",
                    "Reporte sin resultados",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private String formatoDuracion(Object valor) {

        if (valor == null) {
            return "No disponible";
        }

        long segundos = ((Number) valor).longValue();

        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        long restoSegundos = segundos % 60;

        return String.format(
                "%02d:%02d:%02d",
                horas,
                minutos,
                restoSegundos
        );
    }
    private String formatoFecha(Object valor) {

    if (valor == null) {
        return "";
    }

    if (valor instanceof Number numero) {
        return Instant.ofEpochMilli(numero.longValue())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    return valor.toString();
}

private String formatoFechaHora(Object valor) {

    if (valor == null) {
        return "";
    }

    if (valor instanceof Number numero) {
        return Instant.ofEpochMilli(numero.longValue())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    return valor.toString();
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

        if ("OCUPADA".equals(estadoActual)
                || "NO_DISPONIBLE".equals(estadoActual)) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se puede cambiar el estado de una ventanilla "
                    + "que está en uso por un operador.",
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
