package com.example.Eps.controllers;

import com.example.Eps.entities.Turno;
import com.example.Eps.services.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/caja")
@CrossOrigin(origins = "*")
public class CajaController {

    @Autowired 
    private TurnoService turnoService;
    @Autowired private com.example.Eps.repositories.CajaRepository cajaRepository;

    @PostMapping("/llamar/{numeroCaja}")
    public ResponseEntity<?> llamar(@PathVariable Integer numeroCaja) {
        Optional<Turno> turnoOpt = turnoService.llamarSiguiente(numeroCaja);
        if (turnoOpt.isPresent()) {
            return ResponseEntity.ok(turnoOpt.get());
        } else {
            return ResponseEntity.ok().body(Map.of("mensaje", "No hay pacientes en espera"));
        }
    }

    @PostMapping("/despachar/{numeroCaja}")
    public ResponseEntity<?> despachar(@PathVariable Integer numeroCaja) {
        Optional<Turno> turnoOpt = turnoService.despacharActual(numeroCaja);
        if (turnoOpt.isPresent()) {
            return ResponseEntity.ok(turnoOpt.get());
        } else {
            return ResponseEntity.ok().body(Map.of("mensaje", "No tiene ningún paciente en llamado activo"));
        }
    }
    
    @GetMapping("/espera/{numeroCaja}")
    public ResponseEntity<?> obtenerFilaEspera(@PathVariable Integer numeroCaja) {
        Optional<com.example.Eps.entities.Caja> cajaOpt = cajaRepository.findByNumeroCaja(numeroCaja);
        if (cajaOpt.isPresent()) {
            String servicio = cajaOpt.get().getServicioAsignado();
            return ResponseEntity.ok(turnoService.obtenerPacientesEnEspera(servicio));
        }
        return ResponseEntity.badRequest().body(Map.of("mensaje", "Caja no encontrada"));
    }
}