-- Permite desactivar usuarios sin borrarlos (conservan su historial de
-- atenciones). Un usuario inactivo no puede iniciar sesión.
ALTER TABLE usuario
    ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1;
