
package cr.ac.una.turnos.pantalla;

import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.server.service.TurnoService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class PantallaTurnosFrame extends JFrame {

    private final TurnoService turnoService = new TurnoService();

    private final JLabel etiquetaUltimoLlamado =
            new JLabel("Esperando turnos...", SwingConstants.CENTER);

    private final JLabel etiquetaEstado =
            new JLabel("Conectando con el servidor...", SwingConstants.CENTER);

    private final JPanel panelTurnosAtencion = new JPanel(
            new GridLayout(0, 2, 12, 12)
    );

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(
                    new Object[]{"Turno", "Estado", "Ventanilla"}, 0
            ) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };

    private final JTable tablaTurnos = new JTable(modeloTabla);

    private final Timer temporizador =
            new Timer(3000, e -> actualizarPantalla());

    private boolean actualizacionEnCurso = false;

    public PantallaTurnosFrame() {

        configurarVentana();
        inicializarComponentes();

        actualizarPantalla();
        temporizador.start();
    }

    private void configurarVentana() {

        setTitle("Sistema de Gestión de Turnos");
        setSize(950, 750);
        setMinimumSize(new Dimension(750, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void inicializarComponentes() {

        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        // ENCABEZADO

        JLabel titulo = new JLabel(
                "SISTEMA DE GESTIÓN DE TURNOS",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));

        // ÚLTIMO TURNO LLAMADO

        JPanel panelLlamado = new JPanel(new BorderLayout(10, 10));

        panelLlamado.setBorder(
                BorderFactory.createTitledBorder("Último turno llamado")
        );

        etiquetaUltimoLlamado.setFont(
                new Font("SansSerif", Font.BOLD, 32)
        );

        etiquetaUltimoLlamado.setForeground(
                new Color(20, 90, 160)
        );

        panelLlamado.add(
                etiquetaUltimoLlamado,
                BorderLayout.CENTER
        );

        panelLlamado.setPreferredSize(new Dimension(800, 120));

        // TURNOS EN ATENCIÓN

        JLabel tituloAtencion = new JLabel(
                "TURNOS ACTUALMENTE EN ATENCIÓN"
        );

        tituloAtencion.setFont(
                new Font("SansSerif", Font.BOLD, 19)
        );

        panelTurnosAtencion.setBorder(
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        );

        JScrollPane scrollAtencion =
                new JScrollPane(panelTurnosAtencion);

        scrollAtencion.setPreferredSize(
                new Dimension(800, 260)
        );

        JPanel panelAtencion = new JPanel(new BorderLayout(5, 10));

        panelAtencion.add(
                tituloAtencion,
                BorderLayout.NORTH
        );

        panelAtencion.add(
                scrollAtencion,
                BorderLayout.CENTER
        );

        // ÚLTIMOS TURNOS

        JLabel tituloHistorial = new JLabel(
                "ÚLTIMOS TURNOS"
        );

        tituloHistorial.setFont(
                new Font("SansSerif", Font.BOLD, 19)
        );

        tablaTurnos.setFont(
                new Font("SansSerif", Font.PLAIN, 15)
        );

        tablaTurnos.setRowHeight(30);
        tablaTurnos.setFillsViewportHeight(true);

        tablaTurnos.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        JScrollPane scrollHistorial =
                new JScrollPane(tablaTurnos);

        JPanel panelHistorial = new JPanel(new BorderLayout(5, 10));

        panelHistorial.add(
                tituloHistorial,
                BorderLayout.NORTH
        );

        panelHistorial.add(
                scrollHistorial,
                BorderLayout.CENTER
        );

        // DISTRIBUCIÓN GENERAL

        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));

        panelSuperior.add(titulo, BorderLayout.NORTH);
        panelSuperior.add(panelLlamado, BorderLayout.CENTER);

        JPanel panelCentral = new JPanel(new BorderLayout(10, 15));

        panelCentral.add(panelAtencion, BorderLayout.CENTER);
        panelCentral.add(panelHistorial, BorderLayout.SOUTH);

        panelPrincipal.add(
                panelSuperior,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                panelCentral,
                BorderLayout.CENTER
        );

        etiquetaEstado.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        panelPrincipal.add(
                etiquetaEstado,
                BorderLayout.SOUTH
        );

        setContentPane(panelPrincipal);
    }

    private void actualizarPantalla() {

        // Evitar consultas simultáneas.
        if (actualizacionEnCurso) {
            return;
        }

        actualizacionEnCurso = true;

        SwingWorker<DatosPantalla, Void> trabajador =
                new SwingWorker<>() {

            @Override
            protected DatosPantalla doInBackground()
                    throws Exception {

                Turno ultimo =
                        turnoService.consultarUltimoTurnoLlamado();

                List<Turno> enAtencion =
                        turnoService.consultarTurnosEnAtencion();

                List<Turno> recientes =
                        turnoService.consultarUltimosTurnosLlamados();

                return new DatosPantalla(
                        ultimo,
                        enAtencion,
                        recientes
                );
            }

            @Override
            protected void done() {

                try {
                    DatosPantalla datos = get();

                    mostrarUltimoLlamado(datos.ultimoLlamado);
                    mostrarTurnosEnAtencion(datos.enAtencion);
                    mostrarHistorial(datos.recientes);

                    etiquetaEstado.setText(
                            "Pantalla actualizada automáticamente."
                    );

                } catch (Exception e) {

                    etiquetaEstado.setText(
                            "No se pudo actualizar la información. "
                            + "Verifique la conexión con el servidor."
                    );

                    System.err.println(
                            "Error al actualizar pantalla: "
                            + e.getMessage()
                    );

                } finally {
                    actualizacionEnCurso = false;
                }
            }
        };

        trabajador.execute();
    }

    private void mostrarUltimoLlamado(Turno turno) {

        if (turno == null) {

            etiquetaUltimoLlamado.setText(
                    "No hay turnos llamados"
            );

            return;
        }

        String ventanilla = turno.getIdVentanilla() == null
                ? "---"
                : turno.getIdVentanilla().toString();

        etiquetaUltimoLlamado.setText(
                turno.getCodigo()
                + "     →     VENTANILLA "
                + ventanilla
        );
    }

    private void mostrarTurnosEnAtencion(List<Turno> turnos) {

        panelTurnosAtencion.removeAll();

        if (turnos.isEmpty()) {

            JLabel mensaje = new JLabel(
                    "No hay turnos en atención actualmente.",
                    SwingConstants.CENTER
            );

            mensaje.setFont(
                    new Font("SansSerif", Font.PLAIN, 16)
            );

            panelTurnosAtencion.add(mensaje);

        } else {

            for (Turno turno : turnos) {

                String ventanilla =
                        turno.getIdVentanilla() == null
                        ? "---"
                        : turno.getIdVentanilla().toString();

                JPanel tarjeta = new JPanel(new BorderLayout(5, 8));

                tarjeta.setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(
                                        new Color(190, 190, 190)
                                ),
                                BorderFactory.createEmptyBorder(
                                        12, 12, 12, 12
                                )
                        )
                );

                tarjeta.setBackground(
                        new Color(235, 245, 255)
                );

                JLabel codigo = new JLabel(
                        turno.getCodigo(),
                        SwingConstants.CENTER
                );

                codigo.setFont(
                        new Font("SansSerif", Font.BOLD, 27)
                );

                JLabel numeroVentanilla = new JLabel(
                        "Ventanilla " + ventanilla,
                        SwingConstants.CENTER
                );

                numeroVentanilla.setFont(
                        new Font("SansSerif", Font.PLAIN, 16)
                );

                tarjeta.add(codigo, BorderLayout.CENTER);
                tarjeta.add(numeroVentanilla, BorderLayout.SOUTH);

                panelTurnosAtencion.add(tarjeta);
            }
        }

        panelTurnosAtencion.revalidate();
        panelTurnosAtencion.repaint();
    }

    private void mostrarHistorial(List<Turno> turnos) {

        modeloTabla.setRowCount(0);

        for (Turno turno : turnos) {

            String ventanilla =
                    turno.getIdVentanilla() == null
                    ? "---"
                    : turno.getIdVentanilla().toString();

            modeloTabla.addRow(new Object[]{
                turno.getCodigo(),
                turno.getEstado(),
                ventanilla
            });
        }
    }

    private static class DatosPantalla {

        private final Turno ultimoLlamado;
        private final List<Turno> enAtencion;
        private final List<Turno> recientes;

        public DatosPantalla(
                Turno ultimoLlamado,
                List<Turno> enAtencion,
                List<Turno> recientes) {

            this.ultimoLlamado = ultimoLlamado;
            this.enAtencion = enAtencion;
            this.recientes = recientes;
        }
    }

    @Override
    public void dispose() {

        temporizador.stop();
        super.dispose();
    }

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() ->
                new PantallaTurnosFrame().setVisible(true)
        );
    }
}
