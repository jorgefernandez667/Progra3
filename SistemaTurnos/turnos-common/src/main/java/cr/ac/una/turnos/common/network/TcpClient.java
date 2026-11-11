
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

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public Response enviarSolicitud(
            TipoMensaje tipo,
            Object datos) throws IOException {

        String idSolicitud = UUID.randomUUID().toString();

        Request request = new Request(
                idSolicitud,
                tipo,
                datos
        );

        try (Socket socket = new Socket(HOST, PORT);
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
             )) {

            socket.setSoTimeout(10000);

            salida.write(JsonUtil.convertirAJson(request));
            salida.newLine();
            salida.flush();

            String respuestaJson = entrada.readLine();

            if (respuestaJson == null) {
                throw new IOException(
                        "El servidor cerró la conexión sin responder."
                );
            }

            Response response = JsonUtil.convertirDesdeJson(
                    respuestaJson,
                    Response.class
            );

            if (!idSolicitud.equals(response.getIdSolicitud())) {
                throw new IOException(
                        "La respuesta no corresponde a la solicitud enviada."
                );
            }

            return response;
        }
    }
}
