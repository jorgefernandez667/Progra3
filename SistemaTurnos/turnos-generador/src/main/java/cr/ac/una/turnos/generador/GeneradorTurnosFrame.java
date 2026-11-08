
package cr.ac.una.turnos.generador;

import javax.swing.*;
import java.awt.*;
import cr.ac.una.turnos.common.domain.Servicio;
import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import cr.ac.una.turnos.server.service.TurnoService;

public class GeneradorTurnosFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GeneradorTurnosFrame.class.getName());

    /**
     * Creates new form GeneradorTurnosFrame
     */
    
    private final TurnoService turnoService = new TurnoService();
    
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
    comboServicios = new JComboBox<>(new String[]{
        "Información general",
        "Trámites",
        "Pagos",
        "Atención preferencial"
    });

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
}
    

private void generarTurno() {
    String servicioSeleccionado =
            (String) comboServicios.getSelectedItem();

    if (servicioSeleccionado == null) {
        JOptionPane.showMessageDialog(
                this,
                "Debe seleccionar un servicio.",
                "Validación",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    int idServicio;
    String prefijo;

    switch (servicioSeleccionado) {
        case "Información general" -> {
            idServicio = 1;
            prefijo = "A";
        }
        case "Trámites" -> {
            idServicio = 2;
            prefijo = "B";
        }
        case "Pagos" -> {
            idServicio = 3;
            prefijo = "C";
        }
        case "Atención preferencial" -> {
            idServicio = 4;
            prefijo = "D";
        }
        default -> {
            JOptionPane.showMessageDialog(
                    this,
                    "El servicio seleccionado no es válido."
            );
            return;
        }
    }

    Servicio servicio = new Servicio();
    servicio.setId(idServicio);
    servicio.setCodigo(prefijo);
    servicio.setNombre(servicioSeleccionado);
    servicio.setActivo(true);

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

Turno turno = turnoService.generarTurno(
        servicio,
        prioridadSeleccionada
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
    
    


private javax.swing.JComboBox<String> comboServicios;
private javax.swing.JLabel etiquetaTurno;
private javax.swing.JLabel etiquetaMensaje;
private javax.swing.JButton botonGenerar;
private javax.swing.JComboBox<PrioridadTurno> comboPrioridades;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
