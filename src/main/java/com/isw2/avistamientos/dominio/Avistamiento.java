package com.isw2.avistamientos.dominio;

/**
 * Un avistamiento de un ave, tal como lo entiende el negocio.
 * Es Java puro: no sabe que existe una base de datos (JPA) ni un formato JSON (Jackson).
 * Esas traducciones las hace cada adaptador, afuera del hexágono.
 */
public class Avistamiento {

    private Long id;
    private String especie;
    private String lugar;
    /** Texto AAAA-MM-DD. */
    private String fecha;
    private String observador;

    public Avistamiento(String especie, String lugar, String fecha, String observador) {
        this(null, especie, lugar, fecha, observador);
    }

    public Avistamiento(Long id, String especie, String lugar, String fecha, String observador) {
        this.id = id;
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
