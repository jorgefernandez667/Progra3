
package cr.ac.una.turnos.server.network;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cr.ac.una.turnos.common.domain.Funcionario;
import cr.ac.una.turnos.common.enums.TipoMensaje;
import cr.ac.una.turnos.common.protocol.Request;
import cr.ac.una.turnos.common.protocol.Response;

import cr.ac.una.turnos.server.service.SesionService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ObjectMapper mapper;

    // Cada conexión TCP tiene su propia sesión.
    private final SesionService sesionService = new SesionService();

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

            // Si el cliente se desconecta sin hacer LOGOUT,
            // intentamos liberar su ventanilla.
            liberarSesionAlDesconectar();

            System.out.println(
                "Cliente desconectado: " + direccion
            );
        }
    }

    private Response procesar(Request request) {

        if (request == null || request.getTipo() == null) {
            return respuestaError(
                request == null ? null : request.getIdSolicitud(),
                "Solicitud inválida."
            );
        }

        try {

            switch (request.getTipo()) {

                case LOGIN:
                    return iniciarSesion(request);

                case LOGOUT:
                    return cerrarSesion(request);

                default:
                    return respuestaError(
                        request.getIdSolicitud(),
                        "Tipo de solicitud todavía no implementado: "
                            + request.getTipo()
                    );
            }

        } catch (IllegalArgumentException | IllegalStateException e) {

            return respuestaError(
                request.getIdSolicitud(),
                e.getMessage()
            );

        } catch (SQLException e) {

            System.err.println(
                "Error de base de datos: " + e.getMessage()
            );

            return respuestaError(
                request.getIdSolicitud(),
                "No fue posible completar la operación."
            );
        }
    }

    private Response iniciarSesion(Request request)
            throws SQLException {

        if (sesionService.haySesionActiva()) {
            throw new IllegalStateException(
                "Ya existe una sesión activa en esta conexión."
            );
        }

        if (request.getDatos() == null) {
            throw new IllegalArgumentException(
                "Faltan los datos de inicio de sesión."
            );
        }

        Map<String, Object> datos = mapper.convertValue(
            request.getDatos(),
            new TypeReference<Map<String, Object>>() {}
        );

        String nombreUsuario = obtenerTexto(
            datos.get("nombreUsuario")
        );

        String contrasena = obtenerTexto(
            datos.get("contrasena")
        );

        sesionService.iniciarSesion(
            nombreUsuario,
            contrasena
        );

        Funcionario funcionario =
            sesionService.getFuncionarioActual();

        Integer idVentanilla =
            sesionService.getIdVentanillaActual();

        Map<String, Object> datosRespuesta = new LinkedHashMap<>();

        datosRespuesta.put(
            "idFuncionario",
            funcionario.getId()
        );

        datosRespuesta.put(
            "nombreCompleto",
            funcionario.getNombreCompleto()
        );

        datosRespuesta.put(
            "idUsuario",
            funcionario.getUsuario().getId()
        );

        datosRespuesta.put(
            "nombreUsuario",
            funcionario.getUsuario().getNombreUsuario()
        );

        datosRespuesta.put(
            "tipo",
            funcionario.getUsuario().getTipo()
        );

        datosRespuesta.put(
            "idVentanilla",
            idVentanilla
        );

        return new Response(
            request.getIdSolicitud(),
            TipoMensaje.LOGIN,
            true,
            "Inicio de sesión exitoso.",
            datosRespuesta
        );
    }

    private Response cerrarSesion(Request request)
            throws SQLException {

        if (!sesionService.haySesionActiva()) {
            throw new IllegalStateException(
                "No existe una sesión activa."
            );
        }

        sesionService.cerrarSesion();

        return new Response(
            request.getIdSolicitud(),
            TipoMensaje.LOGOUT,
            true,
            "Sesión cerrada correctamente.",
            null
        );
    }

    private String obtenerTexto(Object valor) {

        if (valor == null) {
            return null;
        }

        return valor.toString();
    }

    private Response respuestaError(
            String idSolicitud,
            String mensaje) {

        return new Response(
            idSolicitud,
            TipoMensaje.ERROR,
            false,
            mensaje,
            null
        );
    }

    private void liberarSesionAlDesconectar() {

        if (!sesionService.haySesionActiva()) {
            return;
        }

        try {

            sesionService.cerrarSesion();

            System.out.println(
                "Sesión liberada por desconexión del cliente."
            );

        } catch (SQLException | IllegalStateException e) {

            System.err.println(
                "No se pudo liberar la sesión al desconectar: "
                    + e.getMessage()
            );
        }
    }
}

