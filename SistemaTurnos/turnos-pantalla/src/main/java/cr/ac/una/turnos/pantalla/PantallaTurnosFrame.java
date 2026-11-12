
package cr.ac.una.turnos.pantalla;

import cr.ac.una.turnos.common.dto.TurnoDTO;
import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.network.TcpClient;
import cr.ac.una.turnos.common.protocol.Response;
import cr.ac.una.turnos.common.util.JsonUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
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

    private final TcpClient cliente = new TcpClient();

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

        JLabel titulo = new JLabel(
                "SISTEMA DE GESTIÓN DE TURNOS",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));

        JPanel panelLlamado = new JPanel(new BorderLayout(10, 10));

        panelLlamado.setBorder(
                BorderFactory.createTitledBorder("Último turno llamado")
        );

        etiquetaUltimoLlamado.setFont(
                new Font("SansSerif", Font.BOLD, 32)
        );

        etiquetaUltimoLlamado.setForeground(new Color(20, 90, 160));

        panelLlamado.add(
                etiquetaUltimoLlamado,
                BorderLayout.CENTER
        );

        panelLlamado.setPreferredSize(new Dimension(800, 120));

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

        scrollAtencion.setPreferredSize(new Dimension(800, 260));

        JPanel panelAtencion = new JPanel(new BorderLayout(5, 10));

        panelAtencion.add(tituloAtencion, BorderLayout.NORTH);
        panelAtencion.add(scrollAtencion, BorderLayout.CENTER);

        JLabel tituloHistorial = new JLabel("ÚLTIMOS TURNOS");

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

        panelHistorial.add(tituloHistorial, BorderLayout.NORTH);
        panelHistorial.add(scrollHistorial, BorderLayout.CENTER);

        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));

        panelSuperior.add(titulo, BorderLayout.NORTH);
        panelSuperior.add(panelLlamado, BorderLayout.CENTER);

        JPanel panelCentral = new JPanel(new BorderLayout(10, 15));

        panelCentral.add(panelAtencion, BorderLayout.CENTER);
        panelCentral.add(panelHistorial, BorderLayout.SOUTH);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);

        etiquetaEstado.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        panelPrincipal.add(etiquetaEstado, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private void actualizarPantalla() {

        if (actualizacionEnCurso) {
            return;
        }

        actualizacionEnCurso = true;

        SwingWorker<DatosPantalla, Void> trabajador =
                new SwingWorker<>() {

            @Override
            protected DatosPantalla doInBackground() throws Exception {

                Response respuesta = cliente.enviarSolicitud(
                        TipoMensaje.ACTUALIZAR_PANTALLA,
                        null
                );

                if (!respuesta.isExitoso()) {
                    throw new IllegalStateException(
                            respuesta.getMensaje()
                    );
                }

                Map<?, ?> datos = (Map<?, ?>) respuesta.getDatos();

                TurnoDTO ultimo = convertirTurno(
                        datos.get("ultimoLlamado")
                );

                List<TurnoDTO> enAtencion = convertirListaTurnos(
                        datos.get("enAtencion")
                );

                List<TurnoDTO> recientes = convertirListaTurnos(
                        datos.get("recientes")
                );

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

    private TurnoDTO convertirTurno(Object datos) {

        if (datos == null) {
            return null;
        }

        return JsonUtil.convertirDesdeJson(
                JsonUtil.convertirAJson(datos),
                TurnoDTO.class
        );
    }

    private List<TurnoDTO> convertirListaTurnos(Object datos) {

        List<TurnoDTO> turnos = new ArrayList<>();

        if (!(datos instanceof List<?> lista)) {
            return turnos;
        }

        for (Object elemento : lista) {

            TurnoDTO turno = convertirTurno(elemento);

            if (turno != null) {
                turnos.add(turno);
            }
        }

        return turnos;
    }

    private String obtenerVentanilla(TurnoDTO turno) {

        return turno.getIdVentanilla() <= 0
                ? "---"
                : String.valueOf(turno.getIdVentanilla());
    }

    private void mostrarUltimoLlamado(TurnoDTO turno) {

        if (turno == null) {

            etiquetaUltimoLlamado.setText(
                    "No hay turnos llamados"
            );

            return;
        }

        etiquetaUltimoLlamado.setText(
                turno.getCodigo()
                + "     →     VENTANILLA "
                + obtenerVentanilla(turno)
        );
    }

    private void mostrarTurnosEnAtencion(List<TurnoDTO> turnos) {

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

            for (TurnoDTO turno : turnos) {

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

                tarjeta.setBackground(new Color(235, 245, 255));

                JLabel codigo = new JLabel(
                        turno.getCodigo(),
                        SwingConstants.CENTER
                );

                codigo.setFont(
                        new Font("SansSerif", Font.BOLD, 27)
                );

                JLabel numeroVentanilla = new JLabel(
                        "Ventanilla " + obtenerVentanilla(turno),
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

    private void mostrarHistorial(List<TurnoDTO> turnos) {

        modeloTabla.setRowCount(0);

        for (TurnoDTO turno : turnos) {

            modeloTabla.addRow(new Object[]{
                turno.getCodigo(),
                turno.getEstado(),
                obtenerVentanilla(turno)
            });
        }
    }

    private static class DatosPantalla {

        private final TurnoDTO ultimoLlamado;
        private final List<TurnoDTO> enAtencion;
        private final List<TurnoDTO> recientes;

        public DatosPantalla(
                TurnoDTO ultimoLlamado,
                List<TurnoDTO> enAtencion,
                List<TurnoDTO> recientes) {

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

