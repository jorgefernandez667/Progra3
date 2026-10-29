/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cr.ac.una.turnos.operador;

import javax.swing.JOptionPane;
import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.server.service.TurnoService;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class OperadorTurnosFrame extends javax.swing.JFrame {
    private Turno turnoActual;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(OperadorTurnosFrame.class.getName());

    /**
     * Creates new form OperadorTurnosFrame
     */
    public OperadorTurnosFrame() {
        initComponents();
        tablaTurnos.setModel(modeloTabla);
        cargarTurnosPendientes();
    }

    private final TurnoService turnoService = new TurnoService();

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
        botonActualizar = new javax.swing.JButton();
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

        botonActualizar.setText("Actualizar");
        botonActualizar.addActionListener(this::botonActualizarActionPerformed);

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
                .addComponent(etiquetaTurnoActual, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(230, 230, 230))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(etiquetaTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 150, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(botonActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(botonLlamar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(botonFinalizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(botonIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(botonLlamar, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(botonActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(botonIniciar, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(botonFinalizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        etiquetaTitulo.getAccessibleContext().setAccessibleName("");
        etiquetaTitulo.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonActualizarActionPerformed
        cargarTurnosPendientes();
    }//GEN-LAST:event_botonActualizarActionPerformed

    private void botonLlamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLlamarActionPerformed
        try {
        turnoActual = turnoService.llamarSiguienteTurno();

        if (turnoActual == null) {
            etiquetaTurnoActual.setText(
                    "Turno actual: ninguno"
            );

            JOptionPane.showMessageDialog(
                    this,
                    "No hay turnos pendientes."
            );
            return;
        }

        etiquetaTurnoActual.setText(
                "Turno actual: " + turnoActual.getCodigo()
        );

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
        try {
        if (turnoActual == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe llamar un turno.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        turnoService.iniciarAtencionTurno(turnoActual);

        etiquetaTurnoActual.setText(
                "En atención: " + turnoActual.getCodigo()
        );

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
        try {
        if (turnoActual == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay un turno seleccionado.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        turnoService.finalizarAtencionTurno(turnoActual);

        JOptionPane.showMessageDialog(
                this,
                "Se finalizó la atención del turno "
                        + turnoActual.getCodigo()
        );

        turnoActual = null;

        etiquetaTurnoActual.setText(
                "Turno actual: ninguno"
        );

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
        java.awt.EventQueue.invokeLater(() -> new OperadorTurnosFrame().setVisible(true));
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonActualizar;
    private javax.swing.JButton botonFinalizar;
    private javax.swing.JButton botonIniciar;
    private javax.swing.JButton botonLlamar;
    private javax.swing.JLabel etiquetaTitulo;
    private javax.swing.JLabel etiquetaTurnoActual;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaTurnos;
    // End of variables declaration//GEN-END:variables
}
