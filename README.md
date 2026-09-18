# Sistema Distribuido de Gestión de Turnos

**Curso:** Programación III  
**Estado:** En desarrollo

## 1. Descripción

Sistema distribuido para la gestión de turnos de atención.

El proyecto se está desarrollando mediante una arquitectura por capas, separando presentación, aplicación/servicios, dominio y repositorio/datos.

En el primer avance se prioriza la lógica básica del sistema. En esta etapa los datos pueden mantenerse en memoria. Todavía no se implementan los sockets, la persistencia Java/MySQL ni la concurrencia definitiva.

## 2. Tecnologías

- Java 21+
- Maven
- NetBeans
- MySQL, con autorización del profesor
- Git / GitHub

## 3. Arquitectura

```text
Presentación
      ↓
Aplicación / Servicios
      ↓
Dominio
      ↓
Repositorio / Datos
```

La arquitectura final tendrá un servidor central y diferentes clientes. Los clientes no accederán directamente a la base de datos.

## 4. Estructura actual

```text
SistemaTurnos
└── turnos-common
    └── src/main/java/cr/ac/una/turnos/common
        ├── domain
        ├── dto
        └── enums
```

La estructura se ampliará conforme avance el proyecto.

## 5. turnos-common

### DTO implementado

- `TurnoDTO`

Contiene información del turno como identificador, código, servicio, prioridad, estado, fechas, ventanilla y funcionario.

### Enumeraciones implementadas

#### EstadoTurno

- `EN_ESPERA`
- `LLAMADO`
- `EN_ATENCION`
- `FINALIZADO`
- `CANCELADO`
- `ABANDONADO`
- `TRANSFERIDO`
- `SUSPENDIDO`

#### EstadoVentanilla

- `DISPONIBLE`
- `OCUPADA`
- `INACTIVA`

#### PrioridadTurno

- `NORMAL`
- `PREFERENCIAL`
- `URGENTE`

#### TipoUsuario

Define los tipos de usuario del sistema.

#### TipoMensaje

Contiene las operaciones previstas para la comunicación del sistema, incluyendo `LOGIN`, `LOGOUT`, `OBTENER_SERVICIOS`, `GENERAR_TURNO`, `SOLICITAR_SIGUIENTE_TURNO`, `LLAMAR_TURNO`, `INICIAR_ATENCION`, `FINALIZAR_ATENCION`, `RECLAMAR_TURNO`, `OBTENER_VENTANILLAS`, `CAMBIAR_ESTADO_VENTANILLA`, `ACTUALIZAR_PANTALLA`, `OBTENER_USUARIOS`, `OBTENER_FUNCIONARIOS`, `OBTENER_REPORTES` y `ERROR`.

### Dominio

El paquete `domain` ya fue creado dentro de `turnos-common`.

## 6. Primer avance

El primer avance se enfoca en la lógica básica de los turnos, sin depender todavía de sockets, base de datos o concurrencia definitiva.

Se debe cubrir:

- Crear turnos.
- Asignar identificadores.
- Manejar estados.
- Consultar turnos pendientes.
- Llamar un turno.
- Cambiar el estado de un turno.
- Mostrar el turno llamado.
- Validar operaciones inválidas.

Los datos pueden permanecer en memoria durante esta etapa.

## 7. Funcionalidades planificadas

### Generador

- Seleccionar servicio.
- Generar turno.
- Asignar código único.
- Registrar fecha y hora.
- Mostrar el turno generado.

### Operador

- Iniciar sesión.
- Identificar ventanilla.
- Solicitar siguiente turno.
- Llamar turno.
- Iniciar atención.
- Finalizar atención.
- Reclamar turno.
- Cambiar estado de ventanilla.

### Pantalla

- Mostrar turno llamado.
- Mostrar ventanilla.
- Actualizar la información ante un nuevo llamado.

### Administración

- Usuarios.
- Funcionarios.
- Ventanillas.
- Servicios.
- Configuración de servicios y ventanillas.

## 8. Base de datos

El proyecto utilizará **MySQL**, con autorización del profesor.

La persistencia definitiva todavía no forma parte del primer avance. Por ahora los datos pueden mantenerse en memoria.

Posteriormente se almacenarán datos de usuarios, funcionarios, ventanillas, servicios, turnos, estados e historial de atención.

## 9. Comunicación y concurrencia

La comunicación mediante sockets todavía no está implementada.

La concurrencia definitiva tampoco está implementada.

En etapas posteriores el servidor deberá atender múltiples clientes simultáneamente y garantizar que un mismo turno no sea asignado a dos ventanillas al mismo tiempo.

## 10. Patrones de diseño

Se contempla la utilización de:

- DAO
- Repository
- Observer
- Strategy
- Factory
- Singleton
- MVC

La implementación y justificación se realizará conforme avance el proyecto.

## 11. Progreso

### Configuración inicial

- [x] Crear `SistemaTurnos`.
- [x] Configurar Maven multi-módulo.
- [x] Crear `turnos-common`.
- [x] Crear estructura inicial de paquetes.
- [x] Crear `TurnoDTO`.
- [x] Crear `EstadoTurno`.
- [x] Crear `EstadoVentanilla`.
- [x] Crear `PrioridadTurno`.
- [x] Crear `TipoUsuario`.
- [x] Crear `TipoMensaje`.
- [x] Verificar compilación.

### Primer avance

- [x] Definir el alcance del primer avance.
- [x] Definir la separación Presentación → Aplicación/Servicios → Dominio → Repositorio/Datos.
- [x] Crear el paquete `domain`.
- [ ] Completar la entidad de dominio `Turno`.
- [ ] Implementar generación de turnos.
- [ ] Implementar consulta de turnos pendientes.
- [ ] Implementar llamado de turnos.
- [ ] Implementar cambios de estado.
- [ ] Implementar validaciones.
- [ ] Implementar prueba completa del flujo.

### Etapas posteriores

- [ ] Repositorios.
- [ ] Persistencia con MySQL.
- [ ] Servidor.
- [ ] Sockets.
- [ ] Manejo de múltiples clientes.
- [ ] Concurrencia.
- [ ] `turnos-generador`.
- [ ] `turnos-operador`.
- [ ] `turnos-pantalla`.
- [ ] JavaFX.
- [ ] Administración.
- [ ] Reportes.
- [ ] Pruebas finales.
- [ ] Documentación.

## 12. Compilación

Desde la raíz:

```bash
mvn clean install
```

También se puede utilizar **Clean and Build** desde NetBeans.

## 13. Estado actual — 18 de septiembre de 2026

El proyecto se encuentra en el **primer avance de la lógica básica de turnos**.

La base Maven y `turnos-common` están configurados, incluyendo el DTO y las enumeraciones principales. El paquete `domain` ya fue creado y el trabajo actual se concentra en implementar el flujo básico de turnos utilizando datos en memoria.

No se consideran implementados todavía:

- Sockets.
- Persistencia Java/MySQL.
- Concurrencia definitiva.
- Servidor completo.
- Clientes completos.
- JavaFX.
- Administración.
- Reportes.

## 14. Repositorio

**GitHub:** `jorgefernandez667/Progra3`
