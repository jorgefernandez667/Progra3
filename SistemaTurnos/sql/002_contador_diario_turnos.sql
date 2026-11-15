-- Código de turno por día y por servicio (A001 vuelve a empezar cada día)
-- y generación segura cuando varios generadores piden turnos a la vez.
-- Ejecutar UNA vez sobre la base de datos, con el servidor detenido.

START TRANSACTION;

-- 1. Contador por día y servicio. TurnoDAO.insertarConCodigoDiario lo
--    incrementa dentro de la misma transacción que inserta el turno.
CREATE TABLE IF NOT EXISTS contador_turno (
    fecha       DATE NOT NULL,
    id_servicio INT  NOT NULL,
    ultimo      INT  NOT NULL DEFAULT 0,
    PRIMARY KEY (fecha, id_servicio),
    CONSTRAINT fk_contador_servicio
        FOREIGN KEY (id_servicio) REFERENCES servicio (id_servicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Sembrar con el mayor número ya usado cada día, para que el primer
--    turno nuevo de un día con turnos existentes no repita un código.
INSERT INTO contador_turno (fecha, id_servicio, ultimo)
SELECT fecha_generacion, id_servicio,
       MAX(CAST(SUBSTRING(codigo, 2) AS UNSIGNED))
FROM turno
GROUP BY fecha_generacion, id_servicio
ON DUPLICATE KEY UPDATE ultimo = GREATEST(ultimo, VALUES(ultimo));

-- 3. El código ya no es único en toda la historia, solo dentro del día.
ALTER TABLE turno DROP INDEX codigo;
ALTER TABLE turno ADD UNIQUE KEY uq_turno_fecha_codigo (fecha_generacion, codigo);

COMMIT;
