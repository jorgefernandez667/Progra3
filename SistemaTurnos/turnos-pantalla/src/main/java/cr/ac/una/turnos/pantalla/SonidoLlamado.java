package cr.ac.una.turnos.pantalla;

import java.awt.Toolkit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * Aviso sonoro de la sala de espera: un "ding-dong" que se reproduce
 * cuando se llama un turno o se vuelve a llamar.
 *
 * El sonido se genera por código (no hace falta ningún archivo de audio) y
 * se reproduce en un hilo propio: la reproducción tarda cerca de un
 * segundo y no debe congelar la ventana ni retrasar la actualización
 * automática de la pantalla. Si dos llamadas ocurren mientras suena el
 * aviso, la segunda no se acumula: el sonido en curso ya avisó a la sala.
 *
 * Si el equipo no tiene salida de audio, usa el pitido del sistema.
 */
public class SonidoLlamado {

    static final float FRECUENCIA_MUESTREO = 44100f;

    /** Primera nota (aguda) y segunda nota (grave) del aviso, en Hz. */
    static final double NOTA_1 = 880.00;
    static final double NOTA_2 = 659.25;

    static final double DURACION_NOTA_1 = 0.32;
    static final double DURACION_NOTA_2 = 0.60;

    private final byte[] audio = generarAviso();

    private final AtomicBoolean reproduciendo = new AtomicBoolean(false);

    private final ExecutorService hilo =
            Executors.newSingleThreadExecutor(tarea -> {
                Thread t = new Thread(tarea, "sonido-llamado");
                t.setDaemon(true);
                return t;
            });

    /** Reproduce el aviso sin bloquear a quien lo llama. */
    public void reproducir() {

        if (!reproduciendo.compareAndSet(false, true)) {
            return;
        }

        hilo.execute(() -> {
            try {
                reproducirAudio();
            } catch (Exception e) {
                pitidoDelSistema();
            } finally {
                reproduciendo.set(false);
            }
        });
    }

    private void reproducirAudio() throws Exception {

        AudioFormat formato =
                new AudioFormat(FRECUENCIA_MUESTREO, 16, 1, true, false);

        try (SourceDataLine linea = AudioSystem.getSourceDataLine(formato)) {
            linea.open(formato);
            linea.start();
            linea.write(audio, 0, audio.length);
            linea.drain();
        }
    }

    private void pitidoDelSistema() {
        try {
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception ignorada) {
            // Sin audio ni pitido: la pantalla sigue funcionando igual.
        }
    }

    /**
     * Genera el audio (PCM de 16 bits, mono, little-endian): dos notas
     * con ataque corto y decaimiento suave, parecido a un timbre.
     */
    static byte[] generarAviso() {

        byte[] nota1 = generarNota(NOTA_1, DURACION_NOTA_1);
        byte[] nota2 = generarNota(NOTA_2, DURACION_NOTA_2);

        byte[] total = new byte[nota1.length + nota2.length];
        System.arraycopy(nota1, 0, total, 0, nota1.length);
        System.arraycopy(nota2, 0, total, nota1.length, nota2.length);

        return total;
    }

    private static byte[] generarNota(double frecuencia, double segundos) {

        int muestras = (int) (FRECUENCIA_MUESTREO * segundos);
        byte[] datos = new byte[muestras * 2];

        for (int i = 0; i < muestras; i++) {

            double t = i / (double) FRECUENCIA_MUESTREO;

            // Ataque de 5 ms, decaimiento exponencial y cierre de 15 ms
            // para que no se escuche un "clic" al final.
            double ataque = Math.min(1.0, t / 0.005);
            double decaimiento = Math.exp(-4.0 * t / segundos);
            double cierre = Math.min(1.0, (segundos - t) / 0.015);

            double onda = Math.sin(2 * Math.PI * frecuencia * t)
                    + 0.25 * Math.sin(2 * Math.PI * frecuencia * 2 * t);

            double valor = 0.45 * onda * ataque * decaimiento * cierre;

            short muestra = (short) Math.round(valor * Short.MAX_VALUE);

            datos[2 * i] = (byte) (muestra & 0xFF);
            datos[2 * i + 1] = (byte) ((muestra >> 8) & 0xFF);
        }

        return datos;
    }
}
