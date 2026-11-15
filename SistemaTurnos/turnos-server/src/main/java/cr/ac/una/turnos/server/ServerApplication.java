
package cr.ac.una.turnos.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cr.ac.una.turnos.server.config.ServerConfig;
import cr.ac.una.turnos.server.network.ClientHandler;
import cr.ac.una.turnos.server.service.TurnoService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerApplication {

    /**
     * Cierre diario de la cola: al arrancar y cada medianoche cancela los
     * turnos que nadie atendió el día en que se generaron.
     */
    private static void iniciarCierreDiario() {

        TurnoService turnoService = new TurnoService();

        Runnable cierre = () -> {
            try {
                int cancelados =
                    turnoService.cancelarTurnosDeDiasAnteriores();

                if (cancelados > 0) {
                    System.out.println(
                        "Cierre diario: " + cancelados
                        + " turno(s) sin atender fueron cancelados."
                    );
                }

            } catch (Exception e) {
                System.err.println(
                    "Error en el cierre diario: " + e.getMessage()
                );
            }
        };

        ScheduledExecutorService reloj =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread hilo = new Thread(r, "cierre-diario");
                hilo.setDaemon(true);
                return hilo;
            });

        reloj.execute(cierre);

        long segundosHastaMedianoche = Duration.between(
            LocalDateTime.now(),
            LocalDate.now().plusDays(1).atStartOfDay()
        ).getSeconds();

        reloj.scheduleAtFixedRate(
            cierre,
            segundosHastaMedianoche + 5,
            Duration.ofDays(1).getSeconds(),
            TimeUnit.SECONDS
        );
    }

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        ExecutorService pool = Executors.newFixedThreadPool(
            ServerConfig.MAX_CLIENTES
        );

        try (ServerSocket servidor = new ServerSocket(
                ServerConfig.PORT,
                ServerConfig.MAX_CLIENTES
        )) {

            System.out.println("=================================");
            System.out.println("     SISTEMA DE TURNOS");
            System.out.println("        SERVIDOR CENTRAL");
            System.out.println("=================================");
            System.out.println(
                "Servidor escuchando en el puerto: "
                + ServerConfig.PORT
            );

            iniciarCierreDiario();

            while (!Thread.currentThread().isInterrupted()) {

                Socket cliente = servidor.accept();

                cliente.setSoTimeout(ServerConfig.SOCKET_TIMEOUT);

                pool.execute(
                    new ClientHandler(cliente, mapper)
                );
            }

        } catch (IOException e) {
            System.err.println(
                "Error en el servidor: " + e.getMessage()
            );
        } finally {
            pool.shutdown();
            System.out.println("Servidor detenido.");
        }
    }
}
