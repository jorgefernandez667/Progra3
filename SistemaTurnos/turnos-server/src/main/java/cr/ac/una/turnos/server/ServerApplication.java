
package cr.ac.una.turnos.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cr.ac.una.turnos.server.config.ServerConfig;
import cr.ac.una.turnos.server.network.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApplication {

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
