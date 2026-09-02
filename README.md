# Sistema Distribuido de Gestión de Turnos

**Curso:** Programación III  
**Estado:** En desarrollo

## 1. Descripción

Sistema distribuido para la gestión de turnos de atención. El sistema está compuesto por un servidor central y diferentes clientes que se comunican con él para generar, gestionar y mostrar turnos.

El sistema busca permitir la administración de servicios, turnos, ventanillas, funcionarios y usuarios, además de mantener un historial de las atenciones realizadas y generar reportes.

La comunicación entre los clientes y el servidor se realiza mediante una arquitectura cliente-servidor, evitando que los clientes accedan directamente a la base de datos.

---

## 2. Tecnologías utilizadas

- **Java 21+**
- **Maven**
- **NetBeans**
- **MySQL** (utilizado con autorización del profesor)
- **Sockets TCP/IP**
- **JavaFX**
- **Git / GitHub**

---

## 3. Arquitectura del proyecto

El proyecto utiliza una estructura Maven multi-módulo:

| Módulo | Función |
|---|---|
| `turnos-common` | Clases y estructuras compartidas entre los diferentes módulos |
| `turnos-server` | Servidor central, lógica de negocio, comunicación y acceso a datos |
| `turnos-generador` | Cliente utilizado para generar turnos |
| `turnos-operador` | Cliente utilizado por los funcionarios para gestionar la atención |
| `turnos-pantalla` | Cliente encargado de mostrar públicamente los turnos llamados |

### Arquitectura general

```text
                    ┌─────────────────────┐
                    │       MySQL         │
                    └──────────┬──────────┘
                               │
                               │
                    ┌──────────▼──────────┐
                    │       Servidor      │
                    │   turnos-server     │
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
              ▼                ▼                ▼
     ┌────────────────┐ ┌───────────────┐ ┌───────────────┐
     │    Generador   │ │   Operador    │ │    Pantalla   │
     │ turnos-generador│ │ turnos-operador│ │ turnos-pantalla│
     └────────────────┘ └───────────────┘ └───────────────┘
```

---

## 4. `turnos-common`

Este módulo contiene las clases que pueden ser utilizadas por los diferentes componentes del sistema.

### 4.1 DTO

Actualmente se encuentra implementado:

- `TurnoDTO`

`TurnoDTO` representa la información de un turno y contiene datos como:

- ID.
- Código del turno.
- Servicio.
- Prioridad.
- Estado.
- Fecha de generación.
- Fecha de llamado.
- Fecha de inicio de atención.
- Fecha de finalización.
- Ventanilla.
- Funcionario.

### 4.2 Enumeraciones

Actualmente implementadas:

#### `EstadoTurno`

Representa el estado en el que se encuentra un turno:

- `EN_ESPERA`
- `LLAMADO`
- `EN_ATENCION`
- `FINALIZADO`
- `CANCELADO`
- `ABANDONADO`
- `TRANSFERIDO`
- `SUSPENDIDO`

#### `EstadoVentanilla`

Representa el estado de una ventanilla:

- `DISPONIBLE`
- `OCUPADA`
- `INACTIVA`

#### `PrioridadTurno`

Representa la prioridad de un turno:

- `NORMAL`
- `PREFERENCIAL`
- `URGENTE`

#### `TipoUsuario`

Representa los tipos de usuario del sistema.

#### `TipoMensaje`

Define los tipos de operaciones que pueden enviarse mediante la comunicación entre cliente y servidor.

Actualmente contempla operaciones como:

- `LOGIN`
- `LOGOUT`
- `OBTENER_SERVICIOS`
- `GENERAR_TURNO`
- `SOLICITAR_SIGUIENTE_TURNO`
- `LLAMAR_TURNO`
- `INICIAR_ATENCION`
- `FINALIZAR_ATENCION`
- `RECLAMAR_TURNO`
- `OBTENER_VENTANILLAS`
- `CAMBIAR_ESTADO_VENTANILLA`
- `ACTUALIZAR_PANTALLA`
- `OBTENER_USUARIOS`
- `OBTENER_FUNCIONARIOS`
- `OBTENER_REPORTES`
- `ERROR`

---

## 5. Funcionalidades del sistema

### 5.1 Generador de turnos

El módulo generador permitirá:

- Seleccionar un servicio.
- Generar un turno.
- Obtener un código único.
- Registrar la fecha y hora de generación.
- Registrar el turno en el sistema central.

### 5.2 Operador

El módulo de operador permitirá:

- Iniciar sesión.
- Identificar la ventanilla.
- Consultar el siguiente turno.
- Llamar un turno.
- Iniciar la atención.
- Finalizar la atención.
- Reclamar o volver a llamar un turno.
- Cambiar el estado de la ventanilla.
- Gestionar turnos según las reglas del sistema.

### 5.3 Pantalla pública

El módulo de pantalla permitirá:

- Mostrar el turno llamado.
- Mostrar la ventanilla correspondiente.
- Actualizarse cuando el servidor notifique un nuevo llamado.

### 5.4 Administración

El sistema contempla funcionalidades administrativas para gestionar:

- Usuarios.
- Funcionarios.
- Ventanillas.
- Servicios.
- Estados de ventanillas.
- Configuración de servicios y ventanillas.

---

## 6. Servicios

El sistema contempla los siguientes servicios principales:

- **Servicio A:** Información general.
- **Servicio B:** Trámites.
- **Servicio C:** Pagos.
- **Servicio D:** Atención preferencial.

---

## 7. Comunicación y concurrencia

Uno de los aspectos principales del proyecto es la comunicación distribuida.

Los clientes se comunican con el servidor y no acceden directamente a la base de datos.

El servidor debe ser capaz de atender múltiples clientes simultáneamente.

También se debe garantizar que las operaciones sobre los turnos sean seguras cuando existen solicitudes concurrentes. Por ejemplo, dos ventanillas no deben recibir simultáneamente el mismo turno.

---

## 8. Base de datos

El sistema utiliza **MySQL** como sistema gestor de base de datos, con autorización del profesor.

La base de datos almacena información relacionada con:

- Usuarios.
- Funcionarios.
- Ventanillas.
- Servicios.
- Turnos.
- Atenciones.
- Estados.
- Historial de atención.

La base de datos permitirá conservar la información necesaria para el funcionamiento del sistema y la generación de reportes.

---

## 9. Reportes

El sistema contempla la generación de reportes relacionados con:

- Turnos atendidos entre determinadas fechas.
- Atención por funcionario.
- Atención por servicio.
- Tiempo de espera.
- Tiempo de atención.

---

## 10. Patrones de diseño

El proyecto contempla el uso de patrones de diseño para organizar la arquitectura y separar responsabilidades.

Entre los patrones considerados se encuentran:

- DAO.
- Repository.
- Observer.
- Strategy.
- Factory.
- Singleton.
- MVC.

Los patrones que finalmente sean implementados deberán documentarse junto con su propósito dentro del sistema.

---

## 11. Validaciones y manejo de errores

El sistema deberá contemplar situaciones como:

- Campos vacíos.
- Datos inválidos.
- Credenciales incorrectas.
- Operaciones no autorizadas.
- Desconexión del servidor.
- Desconexión de la base de datos.
- Ausencia de turnos pendientes.
- Ventanillas inactivas.
- Solicitudes simultáneas sobre un mismo recurso.

---

## 12. Seguridad

El sistema contempla autenticación de usuarios y diferentes niveles de acceso.

Como mínimo se consideran los roles:

- Administrador.
- Operador.

Las operaciones disponibles deberán depender de los permisos correspondientes a cada tipo de usuario.

---

## 13. Estructura de paquetes planificada

### `turnos-common`

```text
turnos-common
└── src/main/java/cr/ac/una/turnos/common
    ├── dto
    ├── enums
    ├── protocol
    ├── exception
    └── util
```

### `turnos-server`

```text
turnos-server
└── src/main/java/cr/ac/una/turnos/server
    ├── config
    ├── network
    ├── controller
    ├── service
    ├── strategy
    ├── observer
    ├── persistence
    ├── dao
    ├── repository
    ├── model
    ├── concurrency
    └── exception
```

### `turnos-generador`

```text
turnos-generador
└── src/main/java/cr/ac/una/turnos/generador
    ├── controller
    ├── service
    ├── network
    ├── model
    ├── view
    └── util
```

### `turnos-operador`

```text
turnos-operador
└── src/main/java/cr/ac/una/turnos/operador
    ├── controller
    ├── service
    ├── network
    ├── session
    ├── model
    ├── view
    └── util
```

### `turnos-pantalla`

```text
turnos-pantalla
└── src/main/java/cr/ac/una/turnos/pantalla
    ├── controller
    ├── network
    ├── service
    ├── model
    └── view
```

> La estructura anterior representa la organización planificada del proyecto. Los paquetes se irán implementando conforme avance el desarrollo.

---

## 14. Progreso del proyecto

### Fase 1 — Configuración inicial

- [x] Crear proyecto principal `SistemaTurnos`.
- [x] Configurar proyecto Maven multi-módulo.
- [x] Crear módulo `turnos-common`.
- [x] Crear estructura inicial de paquetes.
- [x] Crear `TurnoDTO`.
- [x] Crear `EstadoTurno`.
- [x] Crear `EstadoVentanilla`.
- [x] Crear `PrioridadTurno`.
- [x] Crear `TipoUsuario`.
- [x] Crear `TipoMensaje`.
- [x] Verificar compilación con `Clean and Build`.

### Fase 2 — Protocolo de comunicación

- [ ] Crear clases para mensajes.
- [ ] Crear requests y responses.
- [ ] Definir eventos.
- [ ] Implementar serialización de mensajes.
- [ ] Probar comunicación básica cliente-servidor.

### Fase 3 — Servidor

- [ ] Crear servidor TCP.
- [ ] Implementar manejo de clientes concurrentes.
- [ ] Crear sesiones de cliente.
- [ ] Implementar autenticación.
- [ ] Implementar servicios de negocio.
- [ ] Implementar gestión de turnos.
- [ ] Implementar control de concurrencia.

### Fase 4 — Base de datos

- [ ] Configurar conexión con MySQL.
- [ ] Crear modelos.
- [ ] Crear DAO.
- [ ] Crear Repository.
- [ ] Implementar persistencia de turnos.
- [ ] Implementar persistencia de usuarios.
- [ ] Implementar persistencia de funcionarios.
- [ ] Implementar persistencia de ventanillas.
- [ ] Implementar persistencia de servicios.
- [ ] Implementar historial.

### Fase 5 — Clientes

- [ ] Implementar `turnos-generador`.
- [ ] Implementar `turnos-operador`.
- [ ] Implementar `turnos-pantalla`.
- [ ] Integrar JavaFX.
- [ ] Conectar clientes con el servidor.

### Fase 6 — Administración y reportes

- [ ] Gestión de usuarios.
- [ ] Gestión de funcionarios.
- [ ] Gestión de ventanillas.
- [ ] Gestión de servicios.
- [ ] Configuración de servicios por ventanilla.
- [ ] Implementación de reportes.

### Fase 7 — Pruebas y documentación

- [ ] Probar concurrencia.
- [ ] Probar múltiples clientes simultáneos.
- [ ] Probar validaciones.
- [ ] Probar errores de conexión.
- [ ] Revisar seguridad.
- [ ] Documentar patrones de diseño.
- [ ] Completar README.
- [ ] Preparar demostración y defensa.

---

## 15. Compilación

Para compilar todo el proyecto desde la raíz:

```bash
mvn clean install
```

También se puede utilizar **Clean and Build** desde NetBeans.

---

## 16. Estado actual

Actualmente el proyecto cuenta con:

- Proyecto Maven multi-módulo configurado.
- Módulo `turnos-common`.
- Estructura inicial de paquetes.
- `TurnoDTO`.
- Enumeraciones principales.
- Definición de tipos de mensajes.
- Compilación exitosa del proyecto.

El desarrollo continuará implementando progresivamente el protocolo de comunicación, servidor, base de datos, clientes y funcionalidades restantes.

---

## 17. Objetivo final

Construir un sistema distribuido funcional para la gestión de turnos que permita:

1. Generar turnos.
2. Gestionar turnos desde las ventanillas.
3. Mostrar públicamente los turnos llamados.
4. Administrar usuarios, funcionarios, servicios y ventanillas.
5. Registrar el historial de atención.
6. Generar reportes.
7. Mantener comunicación cliente-servidor.
8. Soportar múltiples clientes concurrentes.
9. Aplicar una arquitectura organizada y patrones de diseño.
10. Mantener los datos almacenados de forma persistente en MySQL.

---

## 18. Historial de cambios

| Fecha | Cambio |
|---|---|
| 2026-08-28 | Creación y configuración inicial del proyecto Maven multi-módulo. |
| 2026-08-28 | Implementación inicial de `turnos-common`. |
| 2026-08-28 | Creación de `TurnoDTO` y enumeraciones principales. |
| 2026-08-28 | Verificación de compilación mediante `Clean and Build`. |
| 2026-09-02 | README actualizado. Se establece MySQL como base de datos utilizada con autorización del profesor. |

---

## 19. Repositorio

Repositorio del proyecto:

**GitHub:** `jorgefernandez667/Progra3`
