
package cr.ac.una.turnos.server.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.protocol.Request;
import cr.ac.una.turnos.common.protocol.Response;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ObjectMapper mapper;

    public ClientHandler(Socket socket, ObjectMapper mapper) {
        this.socket = socket;
        this.mapper = mapper;
    }

    @Override
    public void run() {
        String direccion = socket.getInetAddress().getHostAddress();

        System.out.println("Cliente conectado: " + direccion);

        try (
            Socket cliente = socket;
            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(
                    cliente.getInputStream(),
                    StandardCharsets.UTF_8
                )
            );
            BufferedWriter salida = new BufferedWriter(
                new OutputStreamWriter(
                    cliente.getOutputStream(),
                    StandardCharsets.UTF_8
                )
            )
        ) {
            String linea;

            while ((linea = entrada.readLine()) != null) {

                Request request = mapper.readValue(
                    linea,
                    Request.class
                );

                Response response = procesar(request);

                salida.write(mapper.writeValueAsString(response));
                salida.newLine();
                salida.flush();
            }

        } catch (IOException e) {
            System.out.println(
                "Conexión cerrada con " + direccion
            );
        } finally {
            System.out.println("Cliente desconectado: " + direccion);
        }
    }

    private Response procesar(Request request) {

        if (request == null || request.getTipo() == null) {
            return new Response(
                request == null ? null : request.getIdSolicitud(),
                TipoMensaje.ERROR,
                false,
                "Solicitud inválida.",
                null
            );
        }

        return new Response(
            request.getIdSolicitud(),
            TipoMensaje.ERROR,
            false,
            "Tipo de solicitud todavía no implementado: "
                + request.getTipo(),
            null
        );
    }
}
