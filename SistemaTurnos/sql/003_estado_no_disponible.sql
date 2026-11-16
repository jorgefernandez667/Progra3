-- Estado NO_DISPONIBLE para ventanillas: el operador la marca temporalmente
-- como no disponible (por ejemplo, para un descanso) sin cerrar sesión.
-- Ejecutar UNA vez sobre la base de datos.

ALTER TABLE ventanilla
    MODIFY estado ENUM('DISPONIBLE', 'OCUPADA', 'NO_DISPONIBLE', 'INACTIVA')
    NOT NULL DEFAULT 'DISPONIBLE';
