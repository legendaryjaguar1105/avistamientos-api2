package com.isw2.avistamientos.adaptador.entrada.web;

import com.isw2.avistamientos.dominio.ResumenEspecie;

/** Una fila del resumen en JSON: {"especie": "colibrí", "cantidad": 3}. */
public record ResumenEspecieResponse(String especie, Long cantidad) {

    public static ResumenEspecieResponse desde(ResumenEspecie r) {
        return new ResumenEspecieResponse(r.especie(), r.cantidad());
    }
}
