package com.isw2.avistamientos.controller;

import com.isw2.avistamientos.dominio.Avistamiento;

/** El JSON que sale: {"id", "especie", "lugar", "fecha", "observador"}, en ese orden, igual que antes. */
public record AvistamientoResponse(Long id, String especie, String lugar, String fecha, String observador) {

    public static AvistamientoResponse desde(Avistamiento a) {
        return new AvistamientoResponse(a.getId(), a.getEspecie(), a.getLugar(), a.getFecha(), a.getObservador());
    }
}
