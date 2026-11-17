-- Permite desactivar un servicio sin borrarlo (los turnos históricos
-- siguen apuntando a él). Un servicio inactivo no se ofrece al generar turnos.
ALTER TABLE servicio
    ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1;
