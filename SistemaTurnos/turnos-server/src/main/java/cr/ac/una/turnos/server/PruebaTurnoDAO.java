package cr.ac.una.turnos.server;

import cr.ac.una.turnos.common.domain.Servicio;
import cr.ac.una.turnos.common.domain.Turno;
import cr.ac.una.turnos.common.enums.EstadoTurno;
import cr.ac.una.turnos.common.enums.PrioridadTurno;
import cr.ac.una.turnos.server.dao.TurnoDAO;

import java.time.LocalDateTime;

public class PruebaTurnoDAO {

    public static void main(String[] args) {
        System.out.println("PRUEBA DE INSERCIÓN DE TURNOS EN MYSQL");
        System.out.println("---------------------------------------");

        try {
            Servicio servicio = new Servicio();
            servicio.setId(1);
            servicio.setCodigo("A");
            servicio.setNombre("Información general");

            Turno turno = new Turno();
            turno.setCodigo("A999");
            turno.setServicio(servicio);
            turno.setPrioridad(PrioridadTurno.NORMAL);
            turno.setEstado(EstadoTurno.EN_ESPERA);
            turno.setFechaGeneracion(LocalDateTime.now());

            TurnoDAO turnoDAO = new TurnoDAO();
            int idGenerado = turnoDAO.insertar(turno);

            System.out.println("Turno insertado correctamente.");
            System.out.println("ID generado: " + idGenerado);
            System.out.println("Código: " + turno.getCodigo());
            System.out.println("Estado: " + turno.getEstado());

        } catch (Exception e) {
            System.err.println("Error al insertar el turno:");
            e.printStackTrace();
        }
    }
}
