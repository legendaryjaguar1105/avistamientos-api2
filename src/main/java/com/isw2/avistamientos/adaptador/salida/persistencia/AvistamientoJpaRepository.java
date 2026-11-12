package com.isw2.avistamientos.adaptador.salida.persistencia;

import com.isw2.avistamientos.dominio.ResumenEspecie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data genera la implementación (findAll, findById, save, ...).
 * Solo lo usa el adaptador de persistencia; el núcleo no sabe que existe.
 */
public interface AvistamientoJpaRepository extends JpaRepository<AvistamientoEntidad, Long> {

    /** Cuenta avistamientos agrupando por especie, de la más vista a la menos vista. */
    @Query("""
           SELECT new com.isw2.avistamientos.dominio.ResumenEspecie(a.especie, COUNT(a))
           FROM AvistamientoEntidad a
           GROUP BY a.especie
           ORDER BY COUNT(a) DESC, a.especie ASC
           """)
    List<ResumenEspecie> contarPorEspecie();
}
