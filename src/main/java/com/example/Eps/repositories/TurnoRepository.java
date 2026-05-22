package com.example.Eps.repositories;

import com.example.Eps.entities.Turno;
import com.example.Eps.entities.EstadoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    
    // Para el Kiosco/Caja: Busca el turno en espera con MAYOR prioridad y MÁS antiguo de llegada
    Optional<Turno> findFirstByServicioAndEstadoOrderByNivelPrioridadDescFechaCreacionAsc(String servicio, EstadoTurno estado);
    
    // Para la Pantalla: Trae todos los turnos que se están llamando activamente
    List<Turno> findByEstado(EstadoTurno estado);
    
    // Lista todos los turnos en espera para aplicarles el algoritmo de envejecimiento
    List<Turno> findByEstadoOrderByFechaCreacionAsc(EstadoTurno estado);

    // ─── EL MÉTODO QUE FALTA COMPILAR 
    List<Turno> findByEstadoOrderByNivelPrioridadDescFechaCreacionAsc(EstadoTurno estado);
}