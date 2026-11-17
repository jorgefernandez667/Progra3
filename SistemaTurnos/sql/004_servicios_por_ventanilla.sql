-- Servicios que puede atender cada ventanilla.
-- Una ventanilla SIN filas en esta tabla atiende todos los servicios.
CREATE TABLE IF NOT EXISTS ventanilla_servicio (
    id_ventanilla INT NOT NULL,
    id_servicio   INT NOT NULL,
    PRIMARY KEY (id_ventanilla, id_servicio),
    CONSTRAINT fk_vs_ventanilla FOREIGN KEY (id_ventanilla)
        REFERENCES ventanilla (id_ventanilla),
    CONSTRAINT fk_vs_servicio FOREIGN KEY (id_servicio)
        REFERENCES servicio (id_servicio)
);
