package cr.ac.una.turnos.pantalla;
import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.server.service.TurnoService;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;
public class PantallaTurnosFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PantallaTurnosFrame.class.getName());
private final TurnoService turnoService = new TurnoService();
private final javax.swing.Timer temporizador =
        new javax.swing.Timer(3000, e -> cargarUltimosTurnos());
private final DefaultTableModel modeloTabla =
        new DefaultTableModel(
                new Object[]{"Turno", "Estado", "Ventanilla"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    public PantallaTurnosFrame() {
        initComponents();
        tablaTurnos.setModel(modeloTabla);
        cargarUltimosTurnos();
        temporizador.start();
    }
private void cargarUltimosTurnos() {
    try {
        modeloTabla.setRowCount(0);

        List<Turno> turnos =
                turnoService.consultarUltimosTurnosLlamados();

        Turno turnoEnAtencion = null;

        for (Turno turno : turnos) {

            String ventanilla = turno.getIdVentanilla() == null
                    ? "---"
                    : turno.getIdVentanilla().toString();

            modeloTabla.addRow(new Object[]{
                turno.getCodigo(),
                turno.getEstado(),
                ventanilla
            });

            if (turno.getEstado()
                    == cr.ac.una.turnos.common.enums.EstadoTurno.EN_ATENCION) {
                turnoEnAtencion = turno;
            }
        }

        // Último turno llamado
        if (turnos.isEmpty()) {
            etiquetaTurnoLlamado.setText(
                    "Último turno llamado: ---"
            );
        } else {
            etiquetaTurnoLlamado.setText(
                    "Último turno llamado: "
                    + turnos.get(0).getCodigo()
            );
        }

        // Turno actualmente en atención
        if (turnoEnAtencion == null) {
            etiquetaEnAtencion.setText(
                    "Turno en atención: ---"
            );

            etiquetaVentanilla.setText(
                    "Ventanilla: ---"
            );
        } else {
            etiquetaEnAtencion.setText(
                    "Turno en atención: "
                    + turnoEnAtencion.getCodigo()
            );

            String numeroVentanilla =
                    turnoEnAtencion.getIdVentanilla() == null
                    ? "---"
                    : turnoEnAtencion.getIdVentanilla().toString();

            etiquetaVentanilla.setText(
                    "Ventanilla: " + numeroVentanilla
            );
        }

    } catch (SQLException e) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Error al consultar los últimos turnos: "
                + e.getMessage(),
                "Error de conexión",
                javax.swing.JOptionPane.ERROR_MESSAGE
        );
    }
}
@Override
public void dispose() {
    temporizador.stop();
    super.dispose();
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        etiquetaTitulo = new javax.swing.JLabel();
        etiquetaTurnoLlamado = new javax.swing.JLabel();
        etiquetaVentanilla = new javax.swing.JLabel();
        etiquetaEnAtencion = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaTurnos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        etiquetaTitulo.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        etiquetaTitulo.setText("SISTEMA DE TURNOS");

        etiquetaTurnoLlamado.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaTurnoLlamado.setText("Último turno llamado: ---");

        etiquetaVentanilla.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaVentanilla.setText("Ventanilla: ---");

        etiquetaEnAtencion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaEnAtencion.setText("Turno en atención: ---");

        tablaTurnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaTurnos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(etiquetaTitulo))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(99, 99, 99)
                                .addComponent(etiquetaTurnoLlamado)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 7, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(etiquetaVentanilla, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(etiquetaEnAtencion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(etiquetaTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(etiquetaTurnoLlamado)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiquetaVentanilla, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiquetaEnAtencion, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        java.awt.EventQueue.invokeLater(() -> new PantallaTurnosFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel etiquetaEnAtencion;
    private javax.swing.JLabel etiquetaTitulo;
    private javax.swing.JLabel etiquetaTurnoLlamado;
    private javax.swing.JLabel etiquetaVentanilla;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaTurnos;
    // End of variables declaration//GEN-END:variables
}
