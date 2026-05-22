package com.example.Eps.entities;

public enum EstadoTurno {
    ESPERANDO,   // El paciente sacó el turno en el kiosco
    LLAMADO,     // El médico/cajero lo está llamando en la pantalla
    ATENDIDO     // Ya terminaron de atender al paciente
}
