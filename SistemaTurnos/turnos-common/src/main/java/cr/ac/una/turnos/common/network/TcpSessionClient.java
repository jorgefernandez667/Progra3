
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

public class TcpSessionClient implements AutoCloseable {

    private final Socket socket;
    private final BufferedReader entrada;
    private final BufferedWriter salida;

    public TcpSessionClient() throws IOException {
        String host = System.getProperty(
                "turnos.server.host",
                System.getenv().getOrDefault(
                        "TURNOS_SERVER_HOST", "localhost"
                )
        );

        String puerto = System.getProperty(
                "turnos.server.port",
                System.getenv().getOrDefault(
                        "TURNOS_SERVER_PORT", "5000"
                )
        );

        socket = new Socket(host, Integer.parseInt(puerto));
        socket.setSoTimeout(600000);

        entrada = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream(),
                        StandardCharsets.UTF_8
                )
        );

        salida = new BufferedWriter(
                new OutputStreamWriter(
                        socket.getOutputStream(),
                        StandardCharsets.UTF_8
                )
        );
    }

    public synchronized Response enviarSolicitud(
            TipoMensaje tipo,
            Object datos
    ) throws IOException {

        if (socket.isClosed()) {
            throw new IOException("La conexión con el servidor está cerrada.");
        }

        String idSolicitud = UUID.randomUUID().toString();

        Request request = new Request(idSolicitud, tipo, datos);

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

    @Override
    public void close() throws IOException {
        socket.close();
    }
}

