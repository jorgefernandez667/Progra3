package cr.ac.una.turnos.server;

import cr.ac.una.turnos.common.domain.Servicio;
import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.common.domain.Ventanilla;
import cr.ac.una.turnos.common.enums.EstadoVentanilla;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import cr.ac.una.turnos.server.service.TurnoService;

public class PruebaTurno {

    public static void main(String[] args) {

        TurnoService servicioTurnos = new TurnoService();

        Servicio servicio = new Servicio();
        servicio.setCodigo("A");
        servicio.setNombre("Información general");

        System.out.println("-------------------------------------------");
        System.out.println("PRUEBA DEL SERVICIO DE TURNOS");
        System.out.println("-------------------------------------------");

        Turno turno = servicioTurnos.generarTurno(
                servicio,
                PrioridadTurno.NORMAL
        );

        System.out.println("Turno generado: " + turno.getCodigo());
        System.out.println("Estado inicial: " + turno.getEstado());

        System.out.println("\nTurnos pendientes:");

        servicioTurnos.obtenerTurnosPendientes()
                .forEach(t ->
                        System.out.println("- " + t.getCodigo())
                );

        Ventanilla ventanilla = new Ventanilla();
        ventanilla.setEstado(EstadoVentanilla.DISPONIBLE);

        System.out.println("\nEstado inicial de la ventanilla: "
                + ventanilla.getEstado());

        Turno turnoAsignado =
                servicioTurnos.asignarSiguienteTurno(ventanilla);

        System.out.println("Turno asignado: "
                + turnoAsignado.getCodigo());

        System.out.println("Estado del turno: "
                + turnoAsignado.getEstado());

        System.out.println("Estado de la ventanilla: "
                + ventanilla.getEstado());

        System.out.println("\nIniciando atención...");

        servicioTurnos.iniciarAtencion(ventanilla);

        System.out.println("Estado del turno: "
                + turnoAsignado.getEstado());

        System.out.println("\nFinalizando atención...");

        servicioTurnos.finalizarAtencion(ventanilla);

        System.out.println("Estado del turno: "
                + turnoAsignado.getEstado());

        System.out.println("Estado final de la ventanilla: "
                + ventanilla.getEstado());

        System.out.println("Turno actual de la ventanilla: "
                + ventanilla.getTurnoActual());
    }
}