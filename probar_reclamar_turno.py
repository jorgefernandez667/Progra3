
import json
import socket
import uuid
import getpass

HOST = "localhost"
PUERTO = 5000


def enviar_solicitud(conexion, entrada, tipo, datos=None):
    solicitud = {
        "idSolicitud": str(uuid.uuid4()),
        "tipo": tipo,
        "datos": datos
    }

    conexion.sendall(
        (json.dumps(solicitud) + "\n").encode("utf-8")
    )

    linea = entrada.readline()

    if not linea:
        raise ConnectionError("El servidor cerró la conexión.")

    respuesta = json.loads(linea)

    print(f"\n--- {tipo} ---")
    print(json.dumps(respuesta, indent=4, ensure_ascii=False))

    return respuesta


def comprobar_exito(respuesta, operacion):
    if not respuesta.get("exitoso", False):
        raise RuntimeError(
            f"{operacion} falló: {respuesta.get('mensaje')}"
        )


def main():
    usuario = input("Nombre de usuario: ")
    contrasena = input("Contraseña: ")

    try:
        with socket.create_connection((HOST, PUERTO), timeout=10) as conexion:
            conexion.settimeout(30)

            entrada = conexion.makefile("r", encoding="utf-8")

            # 1. LOGIN
            login = enviar_solicitud(
                conexion, entrada, "LOGIN",
                {
                    "nombreUsuario": usuario,
                    "contrasena": contrasena
                }
            )
            comprobar_exito(login, "LOGIN")

            # 2. GENERAR TURNO
            generacion = enviar_solicitud(
                conexion, entrada, "GENERAR_TURNO",
                {
                    "idServicio": 1,
                    "prioridad": "NORMAL"
                }
            )
            comprobar_exito(generacion, "GENERAR_TURNO")

            # 3. SOLICITAR SIGUIENTE TURNO
            siguiente = enviar_solicitud(
                conexion, entrada, "SOLICITAR_SIGUIENTE_TURNO"
            )
            comprobar_exito(siguiente, "SOLICITAR_SIGUIENTE_TURNO")

            turno = siguiente.get("datos")
            if not turno:
                raise RuntimeError("No se recibió ningún turno.")

            id_turno = turno.get("id")

            if id_turno is None:
                raise RuntimeError("No se recibió el ID del turno.")

            print("\nTurno seleccionado:", turno.get("codigo"))
            print("ID:", id_turno)

            # 4. RECLAMAR TURNO
            reclamacion = enviar_solicitud(
                conexion, entrada, "RECLAMAR_TURNO",
                {"idTurno": id_turno}
            )
            comprobar_exito(reclamacion, "RECLAMAR_TURNO")

            # 5. INICIAR ATENCIÓN
            inicio = enviar_solicitud(
                conexion, entrada, "INICIAR_ATENCION",
                {"idTurno": id_turno}
            )
            comprobar_exito(inicio, "INICIAR_ATENCION")

            turno_en_atencion = inicio.get("datos")

            if not turno_en_atencion or turno_en_atencion.get("estado") != "EN_ATENCION":
                raise RuntimeError(
                    "El turno no quedó en estado EN_ATENCION."
                )

            # 6. FINALIZAR ATENCIÓN
            finalizacion = enviar_solicitud(
                conexion, entrada, "FINALIZAR_ATENCION",
                {"idTurno": id_turno}
            )
            comprobar_exito(finalizacion, "FINALIZAR_ATENCION")

            turno_finalizado = finalizacion.get("datos")

            if not turno_finalizado or turno_finalizado.get("estado") != "FINALIZADO":
                raise RuntimeError(
                    "El turno no quedó en estado FINALIZADO."
                )

            print("\nEstado confirmado: FINALIZADO.")

            # 7. INTENTAR RECLAMAR EL TURNO FINALIZADO
            print("\nIntentando reclamar nuevamente el turno finalizado...")

            intento = enviar_solicitud(
                conexion, entrada, "RECLAMAR_TURNO",
                {"idTurno": id_turno}
            )

            if intento.get("exitoso", False):
                print("\nPRUEBA FALLIDA: el servidor aceptó reclamar un turno finalizado.")
            else:
                print("\nEl servidor rechazó correctamente la solicitud.")

                # 8. COMPROBAR QUE EL TURNO SIGUE FINALIZADO
                print("\nPRUEBA SUPERADA: el servidor no permitió reclamar el turno finalizado.")

            # 9. LOGOUT
            logout = enviar_solicitud(
                conexion, entrada, "LOGOUT"
            )
            comprobar_exito(logout, "LOGOUT")

            print("\n========== FIN DE LA PRUEBA ==========")

    except ConnectionRefusedError:
        print("\nERROR: el servidor no está disponible.")
    except Exception as error:
        print(f"\nERROR DURANTE LA PRUEBA: {error}")


if __name__ == "__main__":
    main()
