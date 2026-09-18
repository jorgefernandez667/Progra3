package cr.ac.una.turnos.common.test;

import cr.ac.una.turnos.common.domain.Servicio;
import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import cr.ac.una.turnos.server.service.TurnoService;
import cr.ac.una.turnos.common.domain.Ventanilla;


public class PruebaTurno {

    public static void main(String[] args) {

        Servicio servicio = new Servicio();
        servicio.setCodigo("A");
        servicio.setNombre("Información general");

        TurnoService turnoService = new TurnoService();
      

        Turno turno = turnoService.generarTurno(
                servicio,
                PrioridadTurno.NORMAL
        );

        System.out.println("Turno generado: "
                + turno.getCodigo());

        System.out.println("Estado inicial: "
                + turno.getEstado());

        System.out.println();

        System.out.println("Turnos pendientes:");

        for (Turno pendiente : turnoService.obtenerTurnosPendientes()) {
            System.out.println("- " + pendiente.getCodigo());
        }

        System.out.println();

        Turno turnoLlamado = turnoService.llamarSiguienteTurno();

        if (turnoLlamado != null) {
            System.out.println("Turno llamado: "
                    + turnoLlamado.getCodigo());

            System.out.println("Estado después de llamar: "
                    + turnoLlamado.getEstado());
        }

        System.out.println();

        turno.iniciarAtencion();

        System.out.println("Estado después de iniciar atención: "
                + turno.getEstado());

        turno.finalizarAtencion();

        System.out.println("Estado después de finalizar: "
                + turno.getEstado());

        System.out.println();

        System.out.println("Turnos pendientes después de finalizar:");

        for (Turno pendiente : turnoService.obtenerTurnosPendientes()) {
            System.out.println("- " + pendiente.getCodigo());
        }
        
        System.out.println();
System.out.println("-------------------------------------------");
System.out.println("PRUEBA DE VENTANILLA");
System.out.println("-------------------------------------------");

Ventanilla ventanilla = new Ventanilla();

ventanilla.setEstado(
        cr.ac.una.turnos.common.enums.EstadoVentanilla.DISPONIBLE
);

Servicio servicio2 = new Servicio();
servicio2.setCodigo("B");
servicio2.setNombre("Trámites");

Turno turnoVentanilla = turnoService.generarTurno(
        servicio2,
        PrioridadTurno.NORMAL
);

System.out.println("Estado inicial de la ventanilla: "
        + ventanilla.getEstado());

System.out.println("Turno generado: "
        + turnoVentanilla.getCodigo());

Turno turnoAsignado = turnoService.asignarSiguienteTurno(
        ventanilla
);

System.out.println("Turno asignado: "
        + turnoAsignado.getCodigo());

System.out.println("Estado del turno: "
        + turnoAsignado.getEstado());

System.out.println("Estado de la ventanilla: "
        + ventanilla.getEstado());

System.out.println("Turno actual de la ventanilla: "
        + ventanilla.getTurnoActual().getCodigo());
ventanilla.iniciarAtencion();

System.out.println("Estado del turno después de iniciar atención: "
        + ventanilla.getTurnoActual().getEstado());

ventanilla.finalizarAtencion();

System.out.println("Estado de la ventanilla después de finalizar: "
        + ventanilla.getEstado());

System.out.println("Turno actual después de finalizar: "
        + ventanilla.getTurnoActual());
    }
}
