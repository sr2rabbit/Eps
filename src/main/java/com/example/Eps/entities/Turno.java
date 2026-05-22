package com.example.Eps.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo; 

    @Column(nullable = false)
    private String servicio; // Ejemplo: "MEDICINA_GENERAL", "LABORATORIO", "PEDIATRIA"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTurno estado; // ESPERANDO, LLAMADO, ATENDIDO

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // Relación con la Caja/Módulo que está atendiendo este turno (puede ser nulo si está esperando)
    @ManyToOne
    @JoinColumn(name = "caja_id", nullable = true)
    private Caja caja;
    
    @Column(nullable = false)
    private int nivelPrioridad; 
    // 0 = Regular
    // 1 = Embarazada
    // 2 = Discapacitado
    // 3 = Adulto Mayor (Máxima prioridad)

}