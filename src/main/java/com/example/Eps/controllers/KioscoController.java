package com.example.Eps.controllers;

import com.example.Eps.entities.Turno;
import com.example.Eps.services.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.Eps.controllers.TurnoRequest; 

@RestController
@RequestMapping("/api/kiosco")
@CrossOrigin(origins = "*")
public class KioscoController {

    @Autowired private TurnoService turnoService;

            @PostMapping("/pedir")
    public ResponseEntity<?> pedirTurno(@Valid @RequestBody TurnoRequest request) { 
        // Al usar @RequestBody, pasamos de usar QueryParams a recibir un JSON limpio
        try {
            Turno nuevoTurno = turnoService.generarTurno(
                request.getServicio().toUpperCase(), 
                request.getPrioridad(), 
                request.getCorreo()
            );
            return ResponseEntity.ok(nuevoTurno);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar el turno: " + e.getMessage());
        }
    }
}