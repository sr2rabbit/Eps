package com.example.Eps.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cajas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_caja", nullable = false, unique = true)
    private Integer numeroCaja; // Ejemplo: 1, 2, 3 (Caja 1, Caja 2)

    @Column(nullable = false)
    private String funcionario; // Nombre del médico, enfermero o asesor que atiende

    @Column(name = "servicio_asignado", nullable = false)
    private String servicioAsignado; // Ejemplo: "MEDICINA_GENERAL" o "LABORATORIO"
}
