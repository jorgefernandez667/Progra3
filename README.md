# Sistema Distribuido de Gestión de Turnos

**EIF206 — Programación III · Universidad Nacional**

Sistema cliente-servidor en Java que simula la atención de personas en una institución con varios servicios: una terminal genera turnos, los funcionarios los llaman desde sus ventanillas y una pantalla pública muestra el turno llamado en tiempo real. Todas las aplicaciones se comunican con un servidor central por **sockets TCP**; solo el servidor accede a la base de datos.

## Aplicaciones

| Módulo | Qué es |
|---|---|
| `turnos-server` | Servidor central (puerto 5000). Reglas del negocio, concurrencia y acceso a la base de datos. |
| `turnos-generador` | Terminal para generar turnos. |
| `turnos-operador` | Inicio de sesión; ventana del operador (llamar, volver a llamar, iniciar/finalizar atención, marcar no disponible) y panel del administrador (ventanillas, servicios, usuarios, reportes). |
| `turnos-pantalla` | Pantalla pública: último turno, llamados recientes, hora actual y sonido. |
| `turnos-common` | Código compartido: DTO, enumeraciones, protocolo y clientes TCP. |

## Qué incluye

- Turnos con código por servicio que reinicia cada día (`A001`…), prioridad **URGENTE > PREFERENCIAL > NORMAL** y desempate por antigüedad.
- Concurrencia segura: dos ventanillas nunca reciben el mismo turno (transacciones y bloqueos de fila en la base de datos).
- Ocho estados de turno con historial de cada cambio.
- Administración de usuarios, ventanillas, servicios y de qué servicios atiende cada ventanilla.
- Cinco reportes: turnos atendidos, atención por funcionario, atención por servicio, tiempo de espera y tiempo de atención.
- Innovación: sonido de llamado, cierre diario automático de turnos pendientes, liberación de turnos al cerrar sesión, estado de ventanilla "no disponible".

## Ejecución rápida

1. **Base de datos (MySQL 8):** cree una base vacía y ejecute `SistemaTurnos/sql/crear_base_datos.sql` y luego `SistemaTurnos/sql/datos_prueba.sql`.
2. **Credenciales:** defina las variables de entorno `TURNOS_DB_USER` y `TURNOS_DB_PASSWORD`. La URL de la base está en `turnos-server/.../config/DatabaseConfig.java`.
3. **Compilar** (desde `SistemaTurnos`): `mvn clean install -DskipTests`, o *Clean and Build* sobre el proyecto padre en NetBeans.
4. **Ejecutar**, en este orden:
   1. `cr.ac.una.turnos.server.ServerApplication`
   2. `cr.ac.una.turnos.pantalla.PantallaTurnosFrame`
   3. `cr.ac.una.turnos.operador.OperadorTurnosFrame` (una vez por operador)
   4. `cr.ac.una.turnos.generador.GeneradorTurnosFrame`

**Usuarios de prueba:** `admin` / `Admin123*` · `operador1`, `operador2`, `operador3` / `Turnos123*`.

Requisitos: JDK 21 o superior, Maven 3.9+, MySQL 8.x. El `pom.xml` usa `maven.compiler.release` 26; si su JDK es 21, cámbielo a 21.

## Documentación

- [Documento técnico](docs/DOCUMENTO_TECNICO.md): problema, solución, arquitectura, base de datos, comunicación, hilos, concurrencia, patrones, instrucciones completas y escenario de demostración.
- Diagramas: [base de datos](docs/img/diagrama_bd.png) y [componentes](docs/img/diagrama_componentes.png) (fuentes Mermaid en `docs/src/`).
- Scripts SQL en `SistemaTurnos/sql/`: `crear_base_datos.sql` (esquema final) y `datos_prueba.sql`. Los archivos `002…006_*.sql` son migraciones solo para bases creadas con el esquema anterior.

## Repositorio

GitHub: `jorgefernandez667/progra3`
