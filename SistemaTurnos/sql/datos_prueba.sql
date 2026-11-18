-- =====================================================================
--  Datos de prueba — Sistema Distribuido de Gestión de Turnos
--  Ejecutar DESPUÉS de crear_base_datos.sql.
--
--  Usuarios (contraseñas guardadas como hash BCrypt):
--     admin       Admin123*     ADMINISTRADOR
--     operador1   Turnos123*    OPERADOR  (Operador 1)
--     operador2   Turnos123*    OPERADOR  (Operador 2)
--     operador3   Turnos123*    OPERADOR  (Operador 3)
--
--  Incluye turnos históricos finalizados, cancelados y abandonados de
--  varios días para que los 5 reportes tengan información que mostrar.
--  No deja turnos en espera: se generan desde la aplicación generadora.
-- =====================================================================

START TRANSACTION;

INSERT INTO prioridad (id_prioridad, nombre, nivel) VALUES
 (1, 'NORMAL', 10),
 (2, 'PREFERENCIAL', 20),
 (3, 'URGENTE', 30);

INSERT INTO servicio (id_servicio, nombre, prefijo, activo) VALUES
 (1, 'Información general', 'A', 1),
 (2, 'Trámites', 'B', 1),
 (3, 'Pagos', 'C', 1),
 (4, 'Atención preferencial', 'D', 1);

INSERT INTO ventanilla (id_ventanilla, nombre, estado) VALUES
 (1, 'Ventanilla 1', 'DISPONIBLE'),
 (2, 'Ventanilla 2', 'DISPONIBLE'),
 (3, 'Ventanilla 3', 'DISPONIBLE');

INSERT INTO usuario (id_usuario, nombre_usuario, contrasena, rol, activo) VALUES
 (1, 'operador1', '$2a$12$KBiyc0PSxBCaT.cM6D/i/ulX4S1.onhGZ5gRJ7vYOp.56x30HMi1m', 'OPERADOR', 1),
 (2, 'operador2', '$2a$12$H0ShKMsMWogU5EnWF/Gnq.EPwumI6Oo.YvD9en3yR.CLbWnoA.BXq', 'OPERADOR', 1),
 (3, 'admin',     '$2a$12$Z8tYzlM/7wMusowwwVfpBOwSCBvOo1j7ww6Kcc9Hzg5YGEkPH5dBG', 'ADMINISTRADOR', 1),
 (4, 'operador3', '$2a$12$Anx8YHTBYjlMghMWfR.bLectjiUTwRd9R6zZUUF5TRvSoo6EvOWou', 'OPERADOR', 1);

-- El administrador no tiene funcionario (no atiende turnos).
INSERT INTO funcionario (id_funcionario, id_usuario, nombre_completo) VALUES
 (1, 1, 'Operador 1'),
 (2, 2, 'Operador 2'),
 (3, 4, 'Operador 3');

-- ventanilla_servicio queda vacía a propósito: sin configuración, cada
-- ventanilla atiende todos los servicios. Se configura desde el panel
-- de administración (pestaña Ventanillas, "Servicios que atiende").

INSERT INTO asignacion_ventanilla (id_funcionario, id_ventanilla, fecha, hora_inicio, hora_fin) VALUES
 (1, 1, '2026-11-09', '08:00:00', '17:00:00'),
 (2, 2, '2026-11-09', '08:00:00', '17:00:00'),
 (3, 3, '2026-11-09', '08:00:00', '17:00:00'),
 (1, 1, '2026-11-10', '08:00:00', '17:00:00'),
 (2, 2, '2026-11-10', '08:00:00', '17:00:00'),
 (3, 3, '2026-11-10', '08:00:00', '17:00:00'),
 (1, 1, '2026-11-11', '08:00:00', '17:00:00'),
 (2, 2, '2026-11-11', '08:00:00', '17:00:00'),
 (3, 3, '2026-11-11', '08:00:00', '17:00:00'),
 (1, 1, '2026-11-12', '08:00:00', '17:00:00'),
 (2, 2, '2026-11-12', '08:00:00', '17:00:00'),
 (3, 3, '2026-11-12', '08:00:00', '17:00:00');

INSERT INTO turno (id_turno, codigo, id_servicio, id_prioridad, estado, fecha_generacion, hora_generacion, hora_llamado, hora_inicio_atencion, hora_fin_atencion, id_ventanilla, id_funcionario) VALUES
 (1, 'D001', 4, 2, 'FINALIZADO', '2026-11-09', '2026-11-09 08:01:40', '2026-11-09 08:03:38', '2026-11-09 08:03:51', '2026-11-09 08:11:40', 1, 1),
 (2, 'A001', 1, 3, 'FINALIZADO', '2026-11-09', '2026-11-09 08:05:16', '2026-11-09 08:08:12', '2026-11-09 08:08:48', '2026-11-09 08:13:55', 2, 2),
 (3, 'A002', 1, 1, 'FINALIZADO', '2026-11-09', '2026-11-09 08:08:10', '2026-11-09 08:09:03', '2026-11-09 08:09:30', '2026-11-09 08:14:07', 3, 3),
 (4, 'A003', 1, 1, 'FINALIZADO', '2026-11-09', '2026-11-09 08:10:15', '2026-11-09 08:12:20', '2026-11-09 08:12:57', '2026-11-09 08:18:23', 1, 1),
 (5, 'A004', 1, 1, 'FINALIZADO', '2026-11-09', '2026-11-09 08:13:07', '2026-11-09 08:14:15', '2026-11-09 08:14:55', '2026-11-09 08:16:58', 2, 2),
 (6, 'D002', 4, 2, 'FINALIZADO', '2026-11-09', '2026-11-09 08:16:13', '2026-11-09 08:19:41', '2026-11-09 08:20:10', '2026-11-09 08:28:40', 3, 3),
 (7, 'A005', 1, 2, 'FINALIZADO', '2026-11-09', '2026-11-09 08:18:47', '2026-11-09 08:20:07', '2026-11-09 08:20:20', '2026-11-09 08:22:19', 2, 2),
 (8, 'A006', 1, 2, 'FINALIZADO', '2026-11-09', '2026-11-09 08:22:38', '2026-11-09 08:26:24', '2026-11-09 08:26:43', '2026-11-09 08:36:05', 1, 1),
 (9, 'A007', 1, 1, 'FINALIZADO', '2026-11-09', '2026-11-09 08:25:51', '2026-11-09 08:29:22', '2026-11-09 08:29:30', '2026-11-09 08:37:23', 2, 2),
 (10, 'C001', 3, 1, 'FINALIZADO', '2026-11-10', '2026-11-10 08:06:24', '2026-11-10 08:08:04', '2026-11-10 08:08:09', '2026-11-10 08:12:36', 1, 1),
 (11, 'C002', 3, 1, 'FINALIZADO', '2026-11-10', '2026-11-10 08:07:43', '2026-11-10 08:11:26', '2026-11-10 08:11:33', '2026-11-10 08:13:07', 2, 2),
 (12, 'B001', 2, 1, 'FINALIZADO', '2026-11-10', '2026-11-10 08:08:43', '2026-11-10 08:09:18', '2026-11-10 08:09:38', '2026-11-10 08:11:14', 3, 3),
 (13, 'D001', 4, 2, 'FINALIZADO', '2026-11-10', '2026-11-10 08:14:21', '2026-11-10 08:18:43', '2026-11-10 08:18:59', '2026-11-10 08:27:05', 3, 3),
 (14, 'B002', 2, 1, 'FINALIZADO', '2026-11-10', '2026-11-10 08:19:20', '2026-11-10 08:22:02', '2026-11-10 08:22:27', '2026-11-10 08:26:02', 1, 1),
 (15, 'D002', 4, 2, 'FINALIZADO', '2026-11-10', '2026-11-10 08:24:10', '2026-11-10 08:26:39', '2026-11-10 08:26:56', '2026-11-10 08:31:35', 2, 2),
 (16, 'D003', 4, 2, 'FINALIZADO', '2026-11-10', '2026-11-10 08:27:59', '2026-11-10 08:31:31', '2026-11-10 08:31:49', '2026-11-10 08:33:22', 1, 1),
 (17, 'A001', 1, 1, 'FINALIZADO', '2026-11-10', '2026-11-10 08:31:08', '2026-11-10 08:31:56', '2026-11-10 08:32:11', '2026-11-10 08:38:23', 3, 3),
 (18, 'C003', 3, 1, 'CANCELADO', '2026-11-10', '2026-11-10 08:37:41', NULL, NULL, NULL, NULL, NULL),
 (19, 'C001', 3, 1, 'FINALIZADO', '2026-11-11', '2026-11-11 08:05:49', '2026-11-11 08:06:18', '2026-11-11 08:06:28', '2026-11-11 08:12:05', 1, 1),
 (20, 'C002', 3, 1, 'FINALIZADO', '2026-11-11', '2026-11-11 08:09:19', '2026-11-11 08:11:29', '2026-11-11 08:11:49', '2026-11-11 08:15:09', 2, 2),
 (21, 'D001', 4, 2, 'FINALIZADO', '2026-11-11', '2026-11-11 08:14:39', '2026-11-11 08:16:25', '2026-11-11 08:16:56', '2026-11-11 08:25:26', 3, 3),
 (22, 'C003', 3, 1, 'FINALIZADO', '2026-11-11', '2026-11-11 08:18:50', '2026-11-11 08:19:40', '2026-11-11 08:20:07', '2026-11-11 08:26:18', 1, 1),
 (23, 'C004', 3, 1, 'FINALIZADO', '2026-11-11', '2026-11-11 08:21:37', '2026-11-11 08:22:49', '2026-11-11 08:23:05', '2026-11-11 08:32:28', 2, 2),
 (24, 'A001', 1, 2, 'FINALIZADO', '2026-11-11', '2026-11-11 08:28:19', '2026-11-11 08:29:14', '2026-11-11 08:29:52', '2026-11-11 08:38:54', 3, 3),
 (25, 'A002', 1, 1, 'FINALIZADO', '2026-11-11', '2026-11-11 08:34:42', '2026-11-11 08:38:58', '2026-11-11 08:39:14', '2026-11-11 08:48:55', 1, 1),
 (26, 'B001', 2, 1, 'FINALIZADO', '2026-11-11', '2026-11-11 08:40:07', '2026-11-11 08:44:51', '2026-11-11 08:45:09', '2026-11-11 08:48:00', 2, 2),
 (27, 'D002', 4, 2, 'FINALIZADO', '2026-11-11', '2026-11-11 08:44:10', '2026-11-11 08:44:55', '2026-11-11 08:45:00', '2026-11-11 08:54:08', 3, 3),
 (28, 'B001', 2, 1, 'FINALIZADO', '2026-11-12', '2026-11-12 08:05:55', '2026-11-12 08:10:18', '2026-11-12 08:10:23', '2026-11-12 08:15:22', 1, 1),
 (29, 'A001', 1, 1, 'FINALIZADO', '2026-11-12', '2026-11-12 08:12:27', '2026-11-12 08:14:32', '2026-11-12 08:14:38', '2026-11-12 08:22:51', 2, 2),
 (30, 'A002', 1, 1, 'FINALIZADO', '2026-11-12', '2026-11-12 08:13:52', '2026-11-12 08:17:36', '2026-11-12 08:17:59', '2026-11-12 08:23:07', 3, 3),
 (31, 'B002', 2, 1, 'ABANDONADO', '2026-11-12', '2026-11-12 08:17:33', '2026-11-12 08:21:12', '2026-11-12 08:21:20', NULL, 1, 1),
 (32, 'C001', 3, 1, 'FINALIZADO', '2026-11-12', '2026-11-12 08:20:02', '2026-11-12 08:24:32', '2026-11-12 08:24:42', '2026-11-12 08:31:18', 1, 1),
 (33, 'C002', 3, 1, 'FINALIZADO', '2026-11-12', '2026-11-12 08:21:14', '2026-11-12 08:24:55', '2026-11-12 08:25:03', '2026-11-12 08:30:27', 2, 2),
 (34, 'D001', 4, 2, 'FINALIZADO', '2026-11-12', '2026-11-12 08:24:47', '2026-11-12 08:28:24', '2026-11-12 08:29:00', '2026-11-12 08:35:55', 3, 3),
 (35, 'C003', 3, 1, 'FINALIZADO', '2026-11-12', '2026-11-12 08:29:44', '2026-11-12 08:34:20', '2026-11-12 08:34:40', '2026-11-12 08:39:19', 2, 2),
 (36, 'C004', 3, 2, 'CANCELADO', '2026-11-12', '2026-11-12 08:32:21', NULL, NULL, NULL, NULL, NULL);

INSERT INTO historial_turno (id_historial, id_turno, estado, fecha_hora, id_funcionario, id_ventanilla, comentario) VALUES
 (1, 1, 'LLAMADO', '2026-11-09 08:03:38', 1, 1, 'Turno llamado'),
 (2, 1, 'EN_ATENCION', '2026-11-09 08:03:51', 1, 1, 'Inicio de atención'),
 (3, 1, 'FINALIZADO', '2026-11-09 08:11:40', 1, 1, 'Atención finalizada'),
 (4, 2, 'LLAMADO', '2026-11-09 08:08:12', 2, 2, 'Turno llamado'),
 (5, 2, 'EN_ATENCION', '2026-11-09 08:08:48', 2, 2, 'Inicio de atención'),
 (6, 2, 'FINALIZADO', '2026-11-09 08:13:55', 2, 2, 'Atención finalizada'),
 (7, 3, 'LLAMADO', '2026-11-09 08:09:03', 3, 3, 'Turno llamado'),
 (8, 3, 'EN_ATENCION', '2026-11-09 08:09:30', 3, 3, 'Inicio de atención'),
 (9, 3, 'FINALIZADO', '2026-11-09 08:14:07', 3, 3, 'Atención finalizada'),
 (10, 4, 'LLAMADO', '2026-11-09 08:12:20', 1, 1, 'Turno llamado'),
 (11, 4, 'EN_ATENCION', '2026-11-09 08:12:57', 1, 1, 'Inicio de atención'),
 (12, 4, 'FINALIZADO', '2026-11-09 08:18:23', 1, 1, 'Atención finalizada'),
 (13, 5, 'LLAMADO', '2026-11-09 08:14:15', 2, 2, 'Turno llamado'),
 (14, 5, 'EN_ATENCION', '2026-11-09 08:14:55', 2, 2, 'Inicio de atención'),
 (15, 5, 'FINALIZADO', '2026-11-09 08:16:58', 2, 2, 'Atención finalizada'),
 (16, 6, 'LLAMADO', '2026-11-09 08:19:41', 3, 3, 'Turno llamado'),
 (17, 6, 'EN_ATENCION', '2026-11-09 08:20:10', 3, 3, 'Inicio de atención'),
 (18, 6, 'FINALIZADO', '2026-11-09 08:28:40', 3, 3, 'Atención finalizada'),
 (19, 7, 'LLAMADO', '2026-11-09 08:20:07', 2, 2, 'Turno llamado'),
 (20, 7, 'EN_ATENCION', '2026-11-09 08:20:20', 2, 2, 'Inicio de atención'),
 (21, 7, 'FINALIZADO', '2026-11-09 08:22:19', 2, 2, 'Atención finalizada'),
 (22, 8, 'LLAMADO', '2026-11-09 08:26:24', 1, 1, 'Turno llamado'),
 (23, 8, 'EN_ATENCION', '2026-11-09 08:26:43', 1, 1, 'Inicio de atención'),
 (24, 8, 'FINALIZADO', '2026-11-09 08:36:05', 1, 1, 'Atención finalizada'),
 (25, 9, 'LLAMADO', '2026-11-09 08:29:22', 2, 2, 'Turno llamado'),
 (26, 9, 'EN_ATENCION', '2026-11-09 08:29:30', 2, 2, 'Inicio de atención'),
 (27, 9, 'FINALIZADO', '2026-11-09 08:37:23', 2, 2, 'Atención finalizada'),
 (28, 10, 'LLAMADO', '2026-11-10 08:08:04', 1, 1, 'Turno llamado'),
 (29, 10, 'EN_ATENCION', '2026-11-10 08:08:09', 1, 1, 'Inicio de atención'),
 (30, 10, 'FINALIZADO', '2026-11-10 08:12:36', 1, 1, 'Atención finalizada'),
 (31, 11, 'LLAMADO', '2026-11-10 08:11:26', 2, 2, 'Turno llamado'),
 (32, 11, 'EN_ATENCION', '2026-11-10 08:11:33', 2, 2, 'Inicio de atención'),
 (33, 11, 'FINALIZADO', '2026-11-10 08:13:07', 2, 2, 'Atención finalizada'),
 (34, 12, 'LLAMADO', '2026-11-10 08:09:18', 3, 3, 'Turno llamado'),
 (35, 12, 'EN_ATENCION', '2026-11-10 08:09:38', 3, 3, 'Inicio de atención'),
 (36, 12, 'FINALIZADO', '2026-11-10 08:11:14', 3, 3, 'Atención finalizada'),
 (37, 13, 'LLAMADO', '2026-11-10 08:18:43', 3, 3, 'Turno llamado'),
 (38, 13, 'EN_ATENCION', '2026-11-10 08:18:59', 3, 3, 'Inicio de atención'),
 (39, 13, 'FINALIZADO', '2026-11-10 08:27:05', 3, 3, 'Atención finalizada'),
 (40, 14, 'LLAMADO', '2026-11-10 08:22:02', 1, 1, 'Turno llamado'),
 (41, 14, 'EN_ATENCION', '2026-11-10 08:22:27', 1, 1, 'Inicio de atención'),
 (42, 14, 'FINALIZADO', '2026-11-10 08:26:02', 1, 1, 'Atención finalizada'),
 (43, 15, 'LLAMADO', '2026-11-10 08:26:39', 2, 2, 'Turno llamado'),
 (44, 15, 'EN_ATENCION', '2026-11-10 08:26:56', 2, 2, 'Inicio de atención'),
 (45, 15, 'FINALIZADO', '2026-11-10 08:31:35', 2, 2, 'Atención finalizada'),
 (46, 16, 'LLAMADO', '2026-11-10 08:31:31', 1, 1, 'Turno llamado'),
 (47, 16, 'EN_ATENCION', '2026-11-10 08:31:49', 1, 1, 'Inicio de atención'),
 (48, 16, 'FINALIZADO', '2026-11-10 08:33:22', 1, 1, 'Atención finalizada'),
 (49, 17, 'LLAMADO', '2026-11-10 08:31:56', 3, 3, 'Turno llamado'),
 (50, 17, 'EN_ATENCION', '2026-11-10 08:32:11', 3, 3, 'Inicio de atención'),
 (51, 17, 'FINALIZADO', '2026-11-10 08:38:23', 3, 3, 'Atención finalizada'),
 (52, 18, 'CANCELADO', '2026-11-10 23:59:59', NULL, NULL, 'Cancelado automáticamente: no fue atendido el día en que se generó'),
 (55, 19, 'LLAMADO', '2026-11-11 08:06:18', 1, 1, 'Turno llamado'),
 (56, 19, 'EN_ATENCION', '2026-11-11 08:06:28', 1, 1, 'Inicio de atención'),
 (57, 19, 'FINALIZADO', '2026-11-11 08:12:05', 1, 1, 'Atención finalizada'),
 (58, 20, 'LLAMADO', '2026-11-11 08:11:29', 2, 2, 'Turno llamado'),
 (59, 20, 'EN_ATENCION', '2026-11-11 08:11:49', 2, 2, 'Inicio de atención'),
 (60, 20, 'FINALIZADO', '2026-11-11 08:15:09', 2, 2, 'Atención finalizada'),
 (61, 21, 'LLAMADO', '2026-11-11 08:16:25', 3, 3, 'Turno llamado'),
 (62, 21, 'EN_ATENCION', '2026-11-11 08:16:56', 3, 3, 'Inicio de atención'),
 (63, 21, 'FINALIZADO', '2026-11-11 08:25:26', 3, 3, 'Atención finalizada'),
 (64, 22, 'LLAMADO', '2026-11-11 08:19:40', 1, 1, 'Turno llamado'),
 (65, 22, 'EN_ATENCION', '2026-11-11 08:20:07', 1, 1, 'Inicio de atención'),
 (66, 22, 'FINALIZADO', '2026-11-11 08:26:18', 1, 1, 'Atención finalizada'),
 (67, 23, 'LLAMADO', '2026-11-11 08:22:49', 2, 2, 'Turno llamado'),
 (68, 23, 'EN_ATENCION', '2026-11-11 08:23:05', 2, 2, 'Inicio de atención'),
 (69, 23, 'FINALIZADO', '2026-11-11 08:32:28', 2, 2, 'Atención finalizada'),
 (70, 24, 'LLAMADO', '2026-11-11 08:29:14', 3, 3, 'Turno llamado'),
 (71, 24, 'EN_ATENCION', '2026-11-11 08:29:52', 3, 3, 'Inicio de atención'),
 (72, 24, 'FINALIZADO', '2026-11-11 08:38:54', 3, 3, 'Atención finalizada'),
 (73, 25, 'LLAMADO', '2026-11-11 08:38:58', 1, 1, 'Turno llamado'),
 (74, 25, 'EN_ATENCION', '2026-11-11 08:39:14', 1, 1, 'Inicio de atención'),
 (75, 25, 'FINALIZADO', '2026-11-11 08:48:55', 1, 1, 'Atención finalizada'),
 (76, 26, 'LLAMADO', '2026-11-11 08:44:51', 2, 2, 'Turno llamado'),
 (77, 26, 'EN_ATENCION', '2026-11-11 08:45:09', 2, 2, 'Inicio de atención'),
 (78, 26, 'FINALIZADO', '2026-11-11 08:48:00', 2, 2, 'Atención finalizada'),
 (79, 27, 'LLAMADO', '2026-11-11 08:44:55', 3, 3, 'Turno llamado'),
 (80, 27, 'EN_ATENCION', '2026-11-11 08:45:00', 3, 3, 'Inicio de atención'),
 (81, 27, 'FINALIZADO', '2026-11-11 08:54:08', 3, 3, 'Atención finalizada'),
 (82, 28, 'LLAMADO', '2026-11-12 08:10:18', 1, 1, 'Turno llamado'),
 (83, 28, 'EN_ATENCION', '2026-11-12 08:10:23', 1, 1, 'Inicio de atención'),
 (84, 28, 'FINALIZADO', '2026-11-12 08:15:22', 1, 1, 'Atención finalizada'),
 (85, 29, 'LLAMADO', '2026-11-12 08:14:32', 2, 2, 'Turno llamado'),
 (86, 29, 'EN_ATENCION', '2026-11-12 08:14:38', 2, 2, 'Inicio de atención'),
 (87, 29, 'FINALIZADO', '2026-11-12 08:22:51', 2, 2, 'Atención finalizada'),
 (88, 30, 'LLAMADO', '2026-11-12 08:17:36', 3, 3, 'Turno llamado'),
 (89, 30, 'EN_ATENCION', '2026-11-12 08:17:59', 3, 3, 'Inicio de atención'),
 (90, 30, 'FINALIZADO', '2026-11-12 08:23:07', 3, 3, 'Atención finalizada'),
 (91, 31, 'LLAMADO', '2026-11-12 08:21:12', 1, 1, 'Turno llamado'),
 (92, 31, 'EN_ATENCION', '2026-11-12 08:21:20', 1, 1, 'Inicio de atención'),
 (93, 31, 'ABANDONADO', '2026-11-12 08:21:59', 1, 1, 'Sesión cerrada con el turno en atención'),
 (94, 32, 'LLAMADO', '2026-11-12 08:24:32', 1, 1, 'Turno llamado'),
 (95, 32, 'EN_ATENCION', '2026-11-12 08:24:42', 1, 1, 'Inicio de atención'),
 (96, 32, 'FINALIZADO', '2026-11-12 08:31:18', 1, 1, 'Atención finalizada'),
 (97, 33, 'LLAMADO', '2026-11-12 08:24:55', 2, 2, 'Turno llamado'),
 (98, 33, 'EN_ATENCION', '2026-11-12 08:25:03', 2, 2, 'Inicio de atención'),
 (99, 33, 'FINALIZADO', '2026-11-12 08:30:27', 2, 2, 'Atención finalizada'),
 (100, 34, 'LLAMADO', '2026-11-12 08:28:24', 3, 3, 'Turno llamado'),
 (101, 34, 'EN_ATENCION', '2026-11-12 08:29:00', 3, 3, 'Inicio de atención'),
 (102, 34, 'FINALIZADO', '2026-11-12 08:35:55', 3, 3, 'Atención finalizada'),
 (103, 35, 'LLAMADO', '2026-11-12 08:34:20', 2, 2, 'Turno llamado'),
 (104, 35, 'EN_ATENCION', '2026-11-12 08:34:40', 2, 2, 'Inicio de atención'),
 (105, 35, 'FINALIZADO', '2026-11-12 08:39:19', 2, 2, 'Atención finalizada'),
 (106, 36, 'CANCELADO', '2026-11-12 23:59:59', NULL, NULL, 'Cancelado automáticamente: no fue atendido el día en que se generó');

-- Contador diario: el último número usado por día y servicio.
INSERT INTO contador_turno (fecha, id_servicio, ultimo) VALUES
 ('2026-11-09', 1, 7),
 ('2026-11-09', 4, 2),
 ('2026-11-10', 1, 1),
 ('2026-11-10', 2, 2),
 ('2026-11-10', 3, 3),
 ('2026-11-10', 4, 3),
 ('2026-11-11', 1, 2),
 ('2026-11-11', 2, 1),
 ('2026-11-11', 3, 4),
 ('2026-11-11', 4, 2),
 ('2026-11-12', 1, 2),
 ('2026-11-12', 2, 2),
 ('2026-11-12', 3, 4),
 ('2026-11-12', 4, 1);

COMMIT;
