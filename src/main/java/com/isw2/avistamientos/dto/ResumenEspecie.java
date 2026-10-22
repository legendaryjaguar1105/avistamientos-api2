package com.isw2.avistamientos.dto;

/**
 * Una fila del resumen (bonus): cuántos avistamientos hay de una especie.
 * Se convierte a JSON como {"especie": "colibrí", "cantidad": 3}.
 */
public record ResumenEspecie(String especie, Long cantidad) {
}
