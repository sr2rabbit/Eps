package com.example.Eps.services;

import com.example.Eps.entities.*;
import com.example.Eps.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class TurnoService {

    @Autowired private TurnoRepository turnoRepository;
    @Autowired private CajaRepository cajaRepository;
    @Autowired private NotificacionService notificacionService;
    
    // Contador persistente en memoria para la regla del Ratio Fairness
    private int prioritariosAtendidosSeguidos = 0;

    // Generación de turnos con los 3 parámetros requeridos (incluye envío de correo asíncrono)
    public Turno generarTurno(String servicio, int tipoPrioridad, String correo) {
        // --- MOTOR DE ENVEJECIMIENTO ---
        List<Turno> enEspera = turnoRepository.findByEstadoOrderByFechaCreacionAsc(EstadoTurno.ESPERANDO);
        for (Turno t : enEspera) {
            long minutosEsperando = Duration.between(t.getFechaCreacion(), LocalDateTime.now()).toMinutes();
            if (minutosEsperando > 15 && t.getNivelPrioridad() < 3) {
                t.setNivelPrioridad(t.getNivelPrioridad() + 1);
                turnoRepository.save(t);
            }
        }
        // -------------------------------

        List<Turno> todos = turnoRepository.findAll();
        long conteo = todos.stream().filter(t -> t.getServicio().equals(servicio)).count();
        long siguienteNumero = conteo + 1;

        String prefijo = switch(tipoPrioridad) {
            case 1 -> "EMB-"; 
            case 2 -> "DIS-"; 
            case 3 -> "AM-";  
            default -> "REG-"; 
        };

        String letraServicio = servicio.substring(0, 1); 
        String codigoGenerado = String.format("%s%s%02d", prefijo, letraServicio, siguienteNumero);

        Turno nuevoTurno = new Turno();
        nuevoTurno.setCodigo(codigoGenerado);
        nuevoTurno.setServicio(servicio);
        nuevoTurno.setEstado(EstadoTurno.ESPERANDO);
        nuevoTurno.setFechaCreacion(LocalDateTime.now());
        nuevoTurno.setNivelPrioridad(tipoPrioridad);

        Turno turnoGuardado = turnoRepository.save(nuevoTurno);

        // Disparar la notificación asíncrona si el usuario ingresó un correo válido
        if (correo != null && !correo.trim().isEmpty() && correo.contains("@")) {
            notificacionService.enviarTurnoPorCorreo(correo.trim(), turnoGuardado.getCodigo(), servicio);
        }

        return turnoGuardado;
    }

    // CAJA - ACCIÓN 1: LLAMAR (Firma unificada con CajaController)
    public Optional<Turno> llamarSiguiente(Integer numeroCaja) {
        Optional<Caja> cajaOpt = cajaRepository.findByNumeroCaja(numeroCaja);
        if (cajaOpt.isEmpty()) return Optional.empty();
        Caja cajaActual = cajaOpt.get();

        // 1. LIMPIEZA AUTOMÁTICA: Si esta caja ya estaba llamando a alguien, se despacha
        List<Turno> llamados = turnoRepository.findByEstado(EstadoTurno.LLAMADO);
        llamados.stream()
            .filter(t -> t.getCaja() != null && t.getCaja().getNumeroCaja().equals(numeroCaja))
            .forEach(t -> {
                t.setEstado(EstadoTurno.ATENDIDO);
                turnoRepository.save(t);
            });

        // 2. FILTRAR FILA: Traer los turnos ESPERANDO ordenados por prioridad y llegada
        List<Turno> enEspera = turnoRepository.findByEstadoOrderByNivelPrioridadDescFechaCreacionAsc(EstadoTurno.ESPERANDO);
        
        // Filtrar solo los pacientes asignados a la especialidad de este médico
        List<Turno> filaServicio = enEspera.stream()
                .filter(t -> t.getServicio().equals(cajaActual.getServicioAsignado()))
                .toList();

        if (filaServicio.isEmpty()) {
            return Optional.empty();
        }

        Turno turnoSeleccionado = null;

        // 3. REGLA RATIO FAIRNESS: (3 prioritarios : 1 regular)
        if (prioritariosAtendidosSeguidos >= 3) {
            turnoSeleccionado = filaServicio.stream()
                    .filter(t -> t.getNivelPrioridad() == 0)
                    .findFirst()
                    .orElse(null);
            
            if (turnoSeleccionado != null) {
                prioritariosAtendidosSeguidos = 0; // Se cumple la equidad, reiniciamos el contador
                System.out.println("⚖️ Ratio Fairness 3:1: Se forzó el llamado de un turno Regular.");
            }
        }

        // 4. Si no se cumple el ratio o no había ningún regular en fila, se saca el primero por orden natural (Alta prioridad)
        if (turnoSeleccionado == null) {
            turnoSeleccionado = filaServicio.get(0);
            
            if (turnoSeleccionado.getNivelPrioridad() > 0) {
                prioritariosAtendidosSeguidos++;
            } else {
                prioritariosAtendidosSeguidos = 0;
            }
        }

        // 5. Asignar la relación de la caja y actualizar estado
        turnoSeleccionado.setEstado(EstadoTurno.LLAMADO);
        turnoSeleccionado.setCaja(cajaActual);
        
        return Optional.of(turnoRepository.save(turnoSeleccionado));
    }

    // CAJA - ACCIÓN 2: DESPACHAR (Pasa de LLAMADO a ATENDIDO definitivo)
    public Optional<Turno> despacharActual(Integer numeroCaja) {
        List<Turno> llamados = turnoRepository.findByEstado(EstadoTurno.LLAMADO);
        Optional<Turno> turnoEnConsulta = llamados.stream()
            .filter(t -> t.getCaja() != null && t.getCaja().getNumeroCaja().equals(numeroCaja))
            .findFirst();

        if (turnoEnConsulta.isPresent()) {
            Turno t = turnoEnConsulta.get();
            t.setEstado(EstadoTurno.ATENDIDO);
            return Optional.of(turnoRepository.save(t));
        }
        return Optional.empty();
    }

    // PANEL MÉDICO: Ver cola de espera en vivo para la especialidad
    public List<Turno> obtenerPacientesEnEspera(String servicio) {
        List<Turno> todos = turnoRepository.findAll();
        return todos.stream()
            .filter(t -> t.getServicio().equals(servicio) && t.getEstado() == EstadoTurno.ESPERANDO)
            .sorted((t1, t2) -> {
                int compPrioridad = Integer.compare(t2.getNivelPrioridad(), t1.getNivelPrioridad());
                if (compPrioridad != 0) return compPrioridad;
                return t1.getFechaCreacion().compareTo(t2.getFechaCreacion());
            })
            .toList();
    }

    // PANTALLA TELEVISOR: Listar los llamados en vivo
    public List<Turno> obtenerTurnosLlamados() {
        return turnoRepository.findByEstado(EstadoTurno.LLAMADO);
    }
}