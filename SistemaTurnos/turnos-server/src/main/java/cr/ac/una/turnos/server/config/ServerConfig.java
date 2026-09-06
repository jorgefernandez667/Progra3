package cr.ac.una.turnos.server.config;

public final class ServerConfig {

    private ServerConfig() {
    }

    public static final String HOST = "localhost";
    public static final int PORT = 5000;

    public static final int MAX_CLIENTES = 50;

    public static final int SOCKET_TIMEOUT = 10000;
}
