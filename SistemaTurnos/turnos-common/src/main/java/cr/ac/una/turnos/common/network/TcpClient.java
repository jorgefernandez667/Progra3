
package cr.ac.una.turnos.common.network;

import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.protocol.Request;
import cr.ac.una.turnos.common.protocol.Response;
import cr.ac.una.turnos.common.util.JsonUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class TcpClient {

    private static final String HOST = obtenerHost();
    private static final int PORT = obtenerPuerto();

    private static String obtenerHost() {
        return System.getProperty(
                "turnos.server.host",
                System.getenv().getOrDefault(
                        "TURNOS_SERVER_HOST",
                        "localhost"
                )
        );
    }

    private static int obtenerPuerto() {
        String puerto = System.getProperty(
                "turnos.server.port",
                System.getenv().getOrDefault(
                        "TURNOS_SERVER_PORT",
                        "5000"
                )
        );

        return Integer.parseInt(puerto);
    }

    public Response enviarSolicitud(
            TipoMensaje tipo,
            Object datos) throws IOException {

        String idSolicitud = UUID.randomUUID().toString();

        Request request = new Request(
                idSolicitud,
                tipo,
                datos
        );

        try (Socket socket = new Socket(HOST, PORT)) {

            socket.setSoTimeout(10000);

            BufferedWriter salida = new BufferedWriter(
                    new OutputStreamWriter(
                            socket.getOutputStream(),
                            StandardCharsets.UTF_8
                    )
            );

            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            );

            salida.write(JsonUtil.convertirAJson(request));
            salida.newLine();
            salida.flush();

            String respuestaJson = entrada.readLine();

            if (respuestaJson == null) {
                throw new IOException(
                        "El servidor cerró la conexión sin responder."
                );
            }

            Response response;

            try {
                response = JsonUtil.convertirDesdeJson(
                        respuestaJson,
                        Response.class
                );
            } catch (IllegalArgumentException e) {
                throw new IOException(
                        "No se pudo interpretar la respuesta del servidor.",
                        e
                );
            }

            if (!idSolicitud.equals(response.getIdSolicitud())) {
                throw new IOException(
                        "La respuesta no corresponde a la solicitud enviada."
                );
            }

            return response;
        }
    }
}
