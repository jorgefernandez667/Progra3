
package cr.ac.una.turnos.generador;

import javax.swing.*;
import java.awt.*;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import cr.ac.una.turnos.common.dto.TurnoDTO;
import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.network.TcpClient;
import cr.ac.una.turnos.common.protocol.Response;
import cr.ac.una.turnos.common.util.JsonUtil;
import java.util.Map;

public class GeneradorTurnosFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GeneradorTurnosFrame.class.getName());

    /**
     * Creates new form GeneradorTurnosFrame
     */
    
   
    
    public GeneradorTurnosFrame() {
        initComponents();
        configurarVentana();
        inicializarComponentes();
    }

   private void configurarVentana() {
    setTitle("Sistema de Gestión de Turnos");
    setSize(650, 560);
    setMinimumSize(new Dimension(600, 520));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(true);
}
    
    private void inicializarComponentes() {

    JPanel panelPrincipal = new JPanel(new BorderLayout(15, 20));
    panelPrincipal.setBorder(
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
    );

    // Título
    JLabel titulo = new JLabel(
            "GENERADOR DE TURNOS",
            SwingConstants.CENTER
    );
    titulo.setFont(new Font("SansSerif", Font.BOLD, 25));

    JLabel subtitulo = new JLabel(
            "Seleccione el servicio que necesita",
            SwingConstants.CENTER
    );
    subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 15));

    JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 5, 5));
    panelSuperior.add(titulo);
    panelSuperior.add(subtitulo);

    // Servicio
    // Los servicios se piden al servidor: así aparecen los que el
    // administrador crea y desaparecen los que desactiva.
    comboServicios = new JComboBox<>();

    comboServicios.setFont(new Font("SansSerif", Font.PLAIN, 16));

    // Prioridad
    comboPrioridades = new JComboBox<>(PrioridadTurno.values());
    comboPrioridades.setFont(new Font("SansSerif", Font.PLAIN, 16));

    // Botón
    botonGenerar = new JButton("Generar turno");
    botonGenerar.setFont(new Font("SansSerif", Font.BOLD, 16));
    botonGenerar.addActionListener(e -> generarTurno());

    // Panel de selección
    JPanel panelSeleccion = new JPanel(new GridLayout(5, 1, 8, 8));

    panelSeleccion.add(new JLabel("Servicio:"));
    panelSeleccion.add(comboServicios);
    panelSeleccion.add(new JLabel("Prioridad:"));
    panelSeleccion.add(comboPrioridades);
    panelSeleccion.add(botonGenerar);

    // Resultado
    etiquetaTurno = new JLabel("---", SwingConstants.CENTER);
    etiquetaTurno.setFont(new Font("SansSerif", Font.BOLD, 42));

    etiquetaMensaje = new JLabel(
            "Seleccione un servicio para comenzar.",
            SwingConstants.CENTER
    );
    etiquetaMensaje.setFont(new Font("SansSerif", Font.PLAIN, 13));

    JPanel panelResultado = new JPanel(new GridLayout(2, 1, 5, 5));
    panelResultado.setBorder(
            BorderFactory.createTitledBorder("Su turno")
    );
    panelResultado.setPreferredSize(new Dimension(500, 125));

    panelResultado.add(etiquetaTurno);
    panelResultado.add(etiquetaMensaje);

    // Distribución central
    JPanel panelCentral = new JPanel(new BorderLayout(15, 15));
    panelCentral.add(panelSeleccion, BorderLayout.NORTH);
    panelCentral.add(panelResultado, BorderLayout.CENTER);

    panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
    panelPrincipal.add(panelCentral, BorderLayout.CENTER);

    setContentPane(panelPrincipal);

    cargarServicios();

    // Mantiene la lista al día mientras la terminal está encendida.
    new javax.swing.Timer(30000, e -> cargarServicios()).start();
}

/** Servicio que se muestra en la lista (se ve el nombre, se envía el id). */
private record ServicioItem(int id, String nombre) {
    @Override
    public String toString() {
        return nombre;
    }
}

/** Pide al servidor los servicios activos y actualiza la lista. */
private void cargarServicios() {

    try {
        Response respuesta = new TcpClient().enviarSolicitud(
                TipoMensaje.OBTENER_SERVICIOS,
                null
        );

        if (!respuesta.isExitoso()) {
            throw new IllegalStateException(respuesta.getMensaje());
        }

        ServicioItem elegido =
                (ServicioItem) comboServicios.getSelectedItem();

        comboServicios.removeAllItems();

        if (respuesta.getDatos() instanceof java.util.List<?> lista) {
            for (Object elemento : lista) {
                Map<?, ?> datos = (Map<?, ?>) elemento;
                ServicioItem item = new ServicioItem(
                        ((Number) datos.get("id")).intValue(),
                        datos.get("nombre").toString()
                );
                comboServicios.addItem(item);

                if (elegido != null && elegido.id() == item.id()) {
                    comboServicios.setSelectedItem(item);
                }
            }
        }

        botonGenerar.setEnabled(comboServicios.getItemCount() > 0);

    } catch (Exception e) {
        // Se conserva la lista anterior; el siguiente intento reintenta.
        if (comboServicios.getItemCount() == 0) {
            botonGenerar.setEnabled(false);
            etiquetaMensaje.setText(
                    "No hay conexión con el servidor. Reintentando…"
            );
        }
    }
}
    

private void generarTurno() {
    ServicioItem servicio =
            (ServicioItem) comboServicios.getSelectedItem();

    if (servicio == null) {
        JOptionPane.showMessageDialog(
                this,
                "Debe seleccionar un servicio.",
                "Validación",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    int idServicio = servicio.id();
    String servicioSeleccionado = servicio.nombre();

    try {
       PrioridadTurno prioridadSeleccionada =
        (PrioridadTurno) comboPrioridades.getSelectedItem();

if (prioridadSeleccionada == null) {
    JOptionPane.showMessageDialog(
            this,
            "Debe seleccionar una prioridad.",
            "Validación",
            JOptionPane.WARNING_MESSAGE
    );
    return;
}

TcpClient cliente = new TcpClient();

Map<String, Object> datos = Map.of(
        "idServicio", idServicio,
        "prioridad", prioridadSeleccionada.name()
);

Response respuesta = cliente.enviarSolicitud(
        TipoMensaje.GENERAR_TURNO,
        datos
);

if (!respuesta.isExitoso()) {
    throw new Exception(respuesta.getMensaje());
}

TurnoDTO turno = JsonUtil.convertirDesdeJson(
        JsonUtil.convertirAJson(respuesta.getDatos()),
        TurnoDTO.class
);

etiquetaTurno.setText(turno.getCodigo());
etiquetaMensaje.setText(
        "Turno generado: " + servicioSeleccionado
);

JOptionPane.showMessageDialog(
        this,
        "Turno generado y guardado correctamente: "
                + turno.getCodigo(),
        "Turno generado",
        JOptionPane.INFORMATION_MESSAGE
);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(
                this,
                "No se pudo generar el turno:\n"
                        + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
        e.printStackTrace();

        // Puede ser que el servicio ya no esté disponible.
        cargarServicios();
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 451, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new GeneradorTurnosFrame().setVisible(true));
    }
    
    


private javax.swing.JComboBox<ServicioItem> comboServicios;
private javax.swing.JLabel etiquetaTurno;
private javax.swing.JLabel etiquetaMensaje;
private javax.swing.JButton botonGenerar;
private javax.swing.JComboBox<PrioridadTurno> comboPrioridades;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
