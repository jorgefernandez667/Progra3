-- =====================================================================
--  Sistema Distribuido de Gestión de Turnos — EIF206 Programación III
--  Script de creación de la base de datos (esquema completo y final)
--
--  Motor: MySQL 8.x (probado también en MariaDB 10.11).
--  Uso:   1) crear/seleccionar la base de datos (ver abajo)
--         2) ejecutar este script
--         3) ejecutar datos_prueba.sql
--
--  Este script YA incluye los cambios de 002_…006_*.sql. Esos archivos
--  solo sirven para actualizar una base creada antes con el esquema viejo.
-- =====================================================================

-- Descomente si tiene permiso para crear bases de datos:
-- CREATE DATABASE IF NOT EXISTS gamabasis_p3g3
--     DEFAULT CHARACTER SET utf8mb4;
-- USE gamabasis_p3g3;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS contador_turno;
DROP TABLE IF EXISTS historial_turno;
DROP TABLE IF EXISTS turno;
DROP TABLE IF EXISTS asignacion_ventanilla;
DROP TABLE IF EXISTS ventanilla_servicio;
DROP TABLE IF EXISTS funcionario;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS ventanilla;
DROP TABLE IF EXISTS servicio;
DROP TABLE IF EXISTS prioridad;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
--  Catálogos
-- ---------------------------------------------------------------------

-- Niveles de prioridad. El orden de llamado usa id_prioridad
-- (3 URGENTE > 2 PREFERENCIAL > 1 NORMAL).
CREATE TABLE prioridad (
    id_prioridad INT         NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(30) NOT NULL,
    nivel        INT         NOT NULL,
    PRIMARY KEY (id_prioridad),
    UNIQUE KEY uq_prioridad_nombre (nombre),
    UNIQUE KEY uq_prioridad_nivel  (nivel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tipos de servicio. El prefijo (una letra) forma el código del turno.
-- Un servicio inactivo no se ofrece al generar turnos (no se borra para
-- conservar el historial).
CREATE TABLE servicio (
    id_servicio INT         NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(50) NOT NULL,
    prefijo     CHAR(1)     NOT NULL,
    activo      TINYINT(1)  NOT NULL DEFAULT 1,
    PRIMARY KEY (id_servicio),
    UNIQUE KEY uq_servicio_prefijo (prefijo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Ventanillas físicas. NO_DISPONIBLE = el operador la pausó
-- temporalmente (con la sesión abierta); INACTIVA = la desactiva el
-- administrador; OCUPADA = hay un operador con sesión en ella.
CREATE TABLE ventanilla (
    id_ventanilla INT         NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(50) NOT NULL,
    estado        ENUM('DISPONIBLE','OCUPADA','NO_DISPONIBLE','INACTIVA')
                  NOT NULL DEFAULT 'DISPONIBLE',
    PRIMARY KEY (id_ventanilla)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
--  Seguridad y personal
-- ---------------------------------------------------------------------

-- Credenciales. La contraseña se guarda como hash BCrypt, nunca en claro.
CREATE TABLE usuario (
    id_usuario     INT          NOT NULL AUTO_INCREMENT,
    nombre_usuario VARCHAR(50)  NOT NULL,
    contrasena     VARCHAR(200) NOT NULL,
    rol            ENUM('ADMINISTRADOR','OPERADOR') NOT NULL,
    activo         TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id_usuario),
    UNIQUE KEY uq_usuario_nombre (nombre_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Datos de la persona que atiende. Un operador es un usuario con
-- funcionario; el administrador puede no tenerlo.
CREATE TABLE funcionario (
    id_funcionario  INT          NOT NULL AUTO_INCREMENT,
    id_usuario      INT          NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_funcionario),
    UNIQUE KEY uq_funcionario_usuario (id_usuario),
    CONSTRAINT fk_funcionario_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
--  Configuración de ventanillas
-- ---------------------------------------------------------------------

-- Qué servicios atiende cada ventanilla (relación N:M).
-- Una ventanilla SIN filas aquí atiende todos los servicios.
CREATE TABLE ventanilla_servicio (
    id_ventanilla INT NOT NULL,
    id_servicio   INT NOT NULL,
    PRIMARY KEY (id_ventanilla, id_servicio),
    CONSTRAINT fk_vs_ventanilla
        FOREIGN KEY (id_ventanilla) REFERENCES ventanilla (id_ventanilla),
    CONSTRAINT fk_vs_servicio
        FOREIGN KEY (id_servicio)   REFERENCES servicio (id_servicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bitácora de quién ocupó qué ventanilla y cuándo (inicio/fin de sesión).
CREATE TABLE asignacion_ventanilla (
    id_asignacion  INT  NOT NULL AUTO_INCREMENT,
    id_funcionario INT  NOT NULL,
    id_ventanilla  INT  NOT NULL,
    fecha          DATE NOT NULL,
    hora_inicio    TIME NOT NULL,
    hora_fin       TIME DEFAULT NULL,
    PRIMARY KEY (id_asignacion),
    KEY idx_asignacion_funcionario (id_funcionario, fecha),
    KEY idx_asignacion_ventanilla  (id_ventanilla, fecha),
    CONSTRAINT fk_asignacion_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario (id_funcionario),
    CONSTRAINT fk_asignacion_ventanilla
        FOREIGN KEY (id_ventanilla)  REFERENCES ventanilla (id_ventanilla)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
--  Turnos
-- ---------------------------------------------------------------------

-- Cada turno con sus tiempos (generación, llamado, inicio y fin de
-- atención), la ventanilla y el funcionario que lo atendieron y su
-- estado actual. El código (A001) se reinicia cada día, por eso es único
-- solo dentro de la fecha.
CREATE TABLE turno (
    id_turno             INT         NOT NULL AUTO_INCREMENT,
    codigo               VARCHAR(10) NOT NULL,
    id_servicio          INT         NOT NULL,
    id_prioridad         INT         NOT NULL,
    estado               ENUM('EN_ESPERA','LLAMADO','EN_ATENCION',
                              'FINALIZADO','CANCELADO','ABANDONADO',
                              'TRANSFERIDO','SUSPENDIDO')
                         NOT NULL DEFAULT 'EN_ESPERA',
    fecha_generacion     DATE        NOT NULL,
    hora_generacion      DATETIME    NOT NULL,
    hora_llamado         DATETIME    DEFAULT NULL,
    hora_inicio_atencion DATETIME    DEFAULT NULL,
    hora_fin_atencion    DATETIME    DEFAULT NULL,
    id_ventanilla        INT         DEFAULT NULL,
    id_funcionario       INT         DEFAULT NULL,
    PRIMARY KEY (id_turno),
    UNIQUE KEY uq_turno_fecha_codigo (fecha_generacion, codigo),
    KEY idx_turno_fecha    (fecha_generacion),
    KEY idx_turno_estado   (estado),
    KEY idx_turno_servicio (id_servicio),
    CONSTRAINT fk_turno_servicio
        FOREIGN KEY (id_servicio)    REFERENCES servicio (id_servicio),
    CONSTRAINT fk_turno_prioridad
        FOREIGN KEY (id_prioridad)   REFERENCES prioridad (id_prioridad),
    CONSTRAINT fk_turno_ventanilla
        FOREIGN KEY (id_ventanilla)  REFERENCES ventanilla (id_ventanilla),
    CONSTRAINT fk_turno_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario (id_funcionario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Cada cambio de estado de un turno (trazabilidad completa).
CREATE TABLE historial_turno (
    id_historial   INT          NOT NULL AUTO_INCREMENT,
    id_turno       INT          NOT NULL,
    estado         ENUM('EN_ESPERA','LLAMADO','EN_ATENCION','FINALIZADO',
                        'CANCELADO','ABANDONADO','TRANSFERIDO','SUSPENDIDO')
                   NOT NULL,
    fecha_hora     DATETIME     NOT NULL,
    id_funcionario INT          DEFAULT NULL,
    id_ventanilla  INT          DEFAULT NULL,
    comentario     VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id_historial),
    KEY idx_historial_turno_fecha (id_turno, fecha_hora),
    CONSTRAINT fk_historial_turno
        FOREIGN KEY (id_turno)       REFERENCES turno (id_turno),
    CONSTRAINT fk_historial_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario (id_funcionario),
    CONSTRAINT fk_historial_ventanilla
        FOREIGN KEY (id_ventanilla)  REFERENCES ventanilla (id_ventanilla)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Último número usado por día y servicio. TurnoDAO lo incrementa dentro
-- de la misma transacción que inserta el turno, así dos generadores
-- simultáneos nunca obtienen el mismo código.
CREATE TABLE contador_turno (
    fecha       DATE NOT NULL,
    id_servicio INT  NOT NULL,
    ultimo      INT  NOT NULL DEFAULT 0,
    PRIMARY KEY (fecha, id_servicio),
    CONSTRAINT fk_contador_servicio
        FOREIGN KEY (id_servicio) REFERENCES servicio (id_servicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
