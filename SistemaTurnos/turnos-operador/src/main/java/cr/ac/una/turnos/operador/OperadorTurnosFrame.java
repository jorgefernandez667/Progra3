package cr.ac.una.turnos.operador;
import java.util.List;
import javax.swing.Timer;
import javax.swing.SwingWorker;
import cr.ac.una.turnos.common.enums.EstadoTurno;
import javax.swing.JOptionPane;
import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.server.service.TurnoService;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import cr.ac.una.turnos.common.domain.Funcionario;
import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.protocol.Response;
import cr.ac.una.turnos.common.network.TcpSessionClient;

public class OperadorTurnosFrame extends javax.swing.JFrame {
    private Turno turnoActual;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(OperadorTurnosFrame.class.getName());

    private final TurnoService turnoService = new TurnoService();

private final TcpSessionClient cliente;
private final Funcionario funcionario;
private final int idVentanilla;
private Timer timerActualizacion;
private boolean actualizacionEnCurso = false;

public OperadorTurnosFrame(
        TcpSessionClient cliente,
        Funcionario funcionario,
        int idVentanilla) {

    this.cliente = cliente;
    this.funcionario = funcionario;
    this.idVentanilla = idVentanilla;

    // El resto del constructor se conserva temporalmente.

    initComponents();

    tablaTurnos.setModel(modeloTabla);

    etiquetaTitulo.setText(
            "OPERADOR: " + funcionario.getNombreCompleto()
    );

    etiquetaTurnoActual.setText(
            "Ventanilla asignada: " + idVentanilla
    );

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
            cerrarSesion();
        }
    });

    cargarTurnosPendientes();
    iniciarActualizacionAutomatica();
    actualizarEstadoBotones();
}

private final DefaultTableModel modeloTabla =
        new DefaultTableModel(
                new Object[]{"Código", "Servicio", "Prioridad", "Estado"},
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        etiquetaTitulo = new javax.swing.JLabel();
        etiquetaTurnoActual = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaTurnos = new javax.swing.JTable();
        botonLlamar = new javax.swing.JButton();
        botonIniciar = new javax.swing.JButton();
        botonFinalizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        etiquetaTitulo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        etiquetaTitulo.setText("SISTEMA DE ATENCIÓN DE TURNOS");

        etiquetaTurnoActual.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        etiquetaTurnoActual.setText("Turno actual: ninguno");

        tablaTurnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Código", "Servicio", "Prioridad", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaTurnos);

        botonLlamar.setText("Llamar siguiente");
        botonLlamar.addActionListener(this::botonLlamarActionPerformed);

        botonIniciar.setText("Iniciar atención");
        botonIniciar.addActionListener(this::botonIniciarActionPerformed);

        botonFinalizar.setText("Finalizar atención");
        botonFinalizar.addActionListener(this::botonFinalizarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(etiquetaTurnoActual, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(230, 230, 230))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(botonLlamar, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(181, 181, 181))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(etiquetaTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 150, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(346, 346, 346)
                        .addComponent(botonFinalizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(botonIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(etiquetaTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiquetaTurnoActual)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(botonLlamar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(botonIniciar, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(botonFinalizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        etiquetaTitulo.getAccessibleContext().setAccessibleName("");
        etiquetaTitulo.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonLlamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLlamarActionPerformed
       
    if (turnoActual != null) {
        JOptionPane.showMessageDialog(
                this,
                "Debe finalizar el turno actual antes de llamar otro.",
                "Turno activo",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {
        turnoActual = turnoService.llamarSiguienteTurno(
                funcionario.getId(),
                idVentanilla
        );

        if (turnoActual == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay turnos pendientes."
            );
            return;
        }

        etiquetaTurnoActual.setText(
                "Turno actual: " + turnoActual.getCodigo()
        );

        actualizarEstadoBotones();
        cargarTurnosPendientes();

        JOptionPane.showMessageDialog(
                this,
                "Se llamó al turno " + turnoActual.getCodigo()
        );

    } catch (RuntimeException e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    }//GEN-LAST:event_botonLlamarActionPerformed

    private void botonIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonIniciarActionPerformed
        
    if (turnoActual == null
            || turnoActual.getEstado() != EstadoTurno.LLAMADO) {

        JOptionPane.showMessageDialog(
                this,
                "Debe llamar un turno antes de iniciar la atención.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {
        turnoService.iniciarAtencionTurno(turnoActual);

        etiquetaTurnoActual.setText(
                "En atención: " + turnoActual.getCodigo()
        );

        actualizarEstadoBotones();

        JOptionPane.showMessageDialog(
                this,
                "Se inició la atención del turno "
                        + turnoActual.getCodigo()
        );

    } catch (RuntimeException e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    }//GEN-LAST:event_botonIniciarActionPerformed

    private void botonFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonFinalizarActionPerformed
       
    if (turnoActual == null
            || turnoActual.getEstado() != EstadoTurno.EN_ATENCION) {

        JOptionPane.showMessageDialog(
                this,
                "No hay un turno en atención para finalizar.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {
        turnoService.finalizarAtencionTurno(turnoActual);

        JOptionPane.showMessageDialog(
                this,
                "Se finalizó la atención del turno "
                        + turnoActual.getCodigo()
        );

        turnoActual = null;

        etiquetaTurnoActual.setText("Turno actual: ninguno");

        actualizarEstadoBotones();
        cargarTurnosPendientes();

    } catch (RuntimeException e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    }//GEN-LAST:event_botonFinalizarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> {
    new LoginFrame().setVisible(true);
});
    }

private void cargarTurnosPendientes() {
    modeloTabla.setRowCount(0);

    try {
        for (Turno turno : turnoService.consultarTurnosPendientes()) {
            modeloTabla.addRow(new Object[]{
                turno.getCodigo(),
                turno.getServicio().getNombre(),
                turno.getPrioridad().name(),
                turno.getEstado().name()
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
                this,
                "No se pudieron cargar los turnos:\n"
                        + e.getMessage(),
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
        );
        e.printStackTrace();
    }
}
private void cerrarSesion() {

    int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Desea cerrar la sesión y liberar la ventanilla?",
            "Cerrar sesión",
            JOptionPane.YES_NO_OPTION
    );

    if (respuesta != JOptionPane.YES_OPTION) {
        return;
    }

    if (timerActualizacion != null) {
        timerActualizacion.stop();
    }

    try {
        Response respuestaServidor = cliente.enviarSolicitud(
                TipoMensaje.LOGOUT,
                null
        );

        if (!respuestaServidor.isExitoso()) {
            throw new IllegalStateException(
                    respuestaServidor.getMensaje()
            );
        }

        cliente.close();

        dispose();

        JOptionPane.showMessageDialog(
                null,
                "Sesión cerrada correctamente."
        );

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo cerrar la sesión:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );

        if (timerActualizacion != null) {
            timerActualizacion.start();
        }
    }
}
private void iniciarActualizacionAutomatica() {

    timerActualizacion = new Timer(3000, e -> actualizarTablaAutomaticamente());

    timerActualizacion.start();
}

private void actualizarTablaAutomaticamente() {

    if (actualizacionEnCurso) {
        return;
    }

    actualizacionEnCurso = true;

    SwingWorker<List<Turno>, Void> worker = new SwingWorker<>() {

        @Override
        protected List<Turno> doInBackground() throws Exception {
            return turnoService.consultarTurnosPendientes();
        }

        @Override
        protected void done() {
            try {
                List<Turno> turnos = get();

                modeloTabla.setRowCount(0);

                for (Turno turno : turnos) {
                    modeloTabla.addRow(new Object[]{
                        turno.getCodigo(),
                        turno.getServicio().getNombre(),
                        turno.getPrioridad().name(),
                        turno.getEstado().name()
                    });
                }

            } catch (Exception e) {
                logger.log(
                    java.util.logging.Level.SEVERE,
                    "Error al actualizar los turnos",
                    e
                );
            } finally {
                actualizacionEnCurso = false;
            }
        }
    };

    worker.execute();
}

private void actualizarEstadoBotones() {

    boolean hayTurno = turnoActual != null;

    boolean estaLlamado = hayTurno
            && turnoActual.getEstado() == EstadoTurno.LLAMADO;

    boolean enAtencion = hayTurno
            && turnoActual.getEstado() == EstadoTurno.EN_ATENCION;

    botonLlamar.setEnabled(!hayTurno);
    botonIniciar.setEnabled(estaLlamado);
    botonFinalizar.setEnabled(enAtencion);
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonFinalizar;
    private javax.swing.JButton botonIniciar;
    private javax.swing.JButton botonLlamar;
    private javax.swing.JLabel etiquetaTitulo;
    private javax.swing.JLabel etiquetaTurnoActual;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaTurnos;
    // End of variables declaration//GEN-END:variables
}
