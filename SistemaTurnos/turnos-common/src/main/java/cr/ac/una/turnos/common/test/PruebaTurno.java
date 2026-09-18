package cr.ac.una.turnos.common.test;

import cr.ac.una.turnos.common.domain.Servicio;
import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import cr.ac.una.turnos.common.service.TurnoService;

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
    }
}
