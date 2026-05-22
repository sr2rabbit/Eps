package com.example.Eps.controllers; 

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TurnoRequest {

    @NotBlank(message = "El servicio es obligatorio.")
    private String servicio;

    @NotNull(message = "La prioridad no puede ser nula.")
    @Min(value = 0, message = "La prioridad mínima es 0 (Regular).")
    @Max(value = 3, message = "La prioridad máxima es 3 (Adulto Mayor).")
    private Integer prioridad;

    @Email(message = "El formato del correo electrónico no es válido.")
    private String correo;

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public Integer getPrioridad() { return prioridad; }
    public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}
