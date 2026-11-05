package cr.ac.una.turnos.server.security;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña no puede estar vacía."
            );
        }

        return BCrypt.hashpw(
                contrasena,
                BCrypt.gensalt(12)
        );
    }

    public static boolean verificar(
            String contrasena,
            String hash) {

        if (contrasena == null || hash == null) {
            return false;
        }

        return BCrypt.checkpw(contrasena, hash);
    }
}