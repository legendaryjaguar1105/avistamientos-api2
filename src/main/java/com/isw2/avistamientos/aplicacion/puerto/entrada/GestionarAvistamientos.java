package com.isw2.avistamientos.aplicacion.puerto.entrada;

import com.isw2.avistamientos.dominio.Avistamiento;
import com.isw2.avistamientos.dominio.ResumenEspecie;

import java.util.List;

/**
 * Puerto de entrada (primario o "driving"): lo que la aplicación ofrece hacia afuera.
 * Cualquier adaptador de entrada (el controlador REST, la carga de datos de ejemplo,
 * una prueba) maneja la aplicación a través de esta interfaz, sin saber cómo está hecha.
 */
public interface GestionarAvistamientos {

    /** Todos los avistamientos, ordenados por id. */
    List<Avistamiento> listar();

    /** Cantidad de avistamientos por especie, de la más vista a la menos vista. */
    List<ResumenEspecie> resumirPorEspecie();

    /** @throws com.isw2.avistamientos.dominio.AvistamientoNoEncontradoException si el id no existe */
    Avistamiento obtener(Long id);

    /** @throws com.isw2.avistamientos.dominio.AvistamientoInvalidoException si no cumple las reglas de negocio */
    Avistamiento registrar(Avistamiento nuevo);

    /** Valida primero y después busca: datos inválidos dan error aunque el id no exista. */
    Avistamiento actualizar(Long id, Avistamiento datos);

    void eliminar(Long id);
}
