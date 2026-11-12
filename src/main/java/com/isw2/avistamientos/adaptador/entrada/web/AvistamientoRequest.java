package com.isw2.avistamientos.adaptador.entrada.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.isw2.avistamientos.dominio.Avistamiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * El JSON que llega en POST y PUT. Las anotaciones de validación responden 400 con
 * los mismos mensajes que antes del refactor. No tiene id: si el cliente lo envía,
 * se ignora (ignoreUnknown).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvistamientoRequest {

    @NotBlank(message = "La especie es obligatoria")
    @Size(min = 3, max = 50, message = "La especie debe tener entre 3 y 50 caracteres")
    private String especie;

    @NotBlank(message = "El lugar es obligatorio")
    @Size(max = 150, message = "El lugar no puede tener más de 150 caracteres")
    private String lugar;

    @NotBlank(message = "La fecha es obligatoria")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
             message = "La fecha debe tener el formato AAAA-MM-DD")
    private String fecha;

    @NotBlank(message = "El observador es obligatorio")
    @Size(max = 100, message = "El observador no puede tener más de 100 caracteres")
    private String observador;

    public Avistamiento aDominio() {
        return new Avistamiento(especie, lugar, fecha, observador);
    }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getObservador() { return observador; }
    public void setObservador(String observador) { this.observador = observador; }
}
