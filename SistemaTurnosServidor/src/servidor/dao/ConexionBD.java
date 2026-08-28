package servidor.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String HOST = "127.0.0.1";     
    private static final String PUERTO = "3306";
    private static final String BASE_DATOS = "gamabasis_p3g3";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";

    private static ConexionBD instancia;
    private Connection conexion;

    private ConexionBD() throws SQLException {
        String url = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS
                + "?useSSL=false&serverTimezone=UTC";
        this.conexion = DriverManager.getConnection(url, USUARIO, CONTRASENA);
    }

    public static synchronized ConexionBD getInstancia() throws SQLException {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            String url = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS
                    + "?useSSL=false&serverTimezone=UTC";
            conexion = DriverManager.getConnection(url, USUARIO, CONTRASENA);
        }
        return conexion;
    }
}