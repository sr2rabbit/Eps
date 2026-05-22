package com.example.Eps.controllers;

import com.example.Eps.entities.Turno;
import com.example.Eps.services.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pantalla")
@CrossOrigin(origins = "*")
public class PantallaController {

    @Autowired
    private TurnoService turnoService;

    // Endpoint para ver los turnos activos en el tablero (Ej: GET a /api/pantalla/activos)
    @GetMapping("/activos")
    public List<Turno> verTablero() {
        return turnoService.obtenerTurnosLlamados();
    }
}
