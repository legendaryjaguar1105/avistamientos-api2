package com.isw2.avistamientos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * El recurso principal: un avistamiento de un ave.
 *
 * @Entity hace que JPA lo guarde como una fila de la tabla "avistamientos".
 * Las anotaciones de validación (@NotBlank, @Pattern) son las que permiten
 * responder 400 cuando faltan datos o vienen mal.
 */
@Entity
@Table(name = "avistamientos")
public class Avistamiento {

    /** Lo asigna la base de datos. READ_ONLY: sale en el JSON, pero se ignora si el cliente lo envía. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "La especie es obligatoria")
    @Size(min = 3, max = 50, message = "La especie debe tener entre 3 y 50 caracteres")
    @Column(nullable = false, length = 50)
    private String especie;

    @NotBlank(message = "El lugar es obligatorio")
    @Size(max = 150, message = "El lugar no puede tener más de 150 caracteres")
    @Column(nullable = false, length = 150)
    private String lugar;

    /** Se guarda como texto AAAA-MM-DD, tal como pide el enunciado. */
    @NotBlank(message = "La fecha es obligatoria")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
             message = "La fecha debe tener el formato AAAA-MM-DD")
    @Column(nullable = false, length = 10)
    private String fecha;

    @NotBlank(message = "El observador es obligatorio")
    @Size(max = 100, message = "El observador no puede tener más de 100 caracteres")
    @Column(nullable = false, length = 100)
    private String observador;

    /** JPA necesita un constructor vacío. */
    public Avistamiento() {
    }

    public Avistamiento(String especie, String lugar, String fecha, String observador) {
        this.especie = especie;
        this.lugar = lugar;
        this.fecha = fecha;
        this.observador = observador;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getObservador() { return observador; }
    public void setObservador(String observador) { this.observador = observador; }
}
