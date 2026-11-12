package com.isw2.avistamientos.aplicacion.puerto.salida;

import com.isw2.avistamientos.model.Avistamiento;
import com.isw2.avistamientos.dto.ResumenEspecie;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (secundario o "driven"): lo que la aplicación necesita para guardar
 * y consultar avistamientos. Lo define el núcleo y lo implementa un adaptador de afuera
 * (hoy JPA + H2), así la dependencia de código apunta hacia adentro.
 */
public interface RepositorioDeAvistamientos {

    /** Todos los avistamientos, ordenados por id. */
    List<Avistamiento> listarTodos();

    Optional<Avistamiento> buscarPorId(Long id);

    /** Inserta si no tiene id, actualiza si ya lo tiene. Devuelve el avistamiento con su id. */
    Avistamiento guardar(Avistamiento avistamiento);

    boolean existePorId(Long id);

    void eliminarPorId(Long id);

    List<ResumenEspecie> contarPorEspecie();
}
