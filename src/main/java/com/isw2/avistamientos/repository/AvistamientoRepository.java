package com.isw2.avistamientos.repository;

import com.isw2.avistamientos.dto.ResumenEspecie;
import com.isw2.avistamientos.model.Avistamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Acceso a la base de datos. Al extender JpaRepository, Spring genera solo
 * los métodos findAll, findById, save, existsById, deleteById, etc.
 */
public interface AvistamientoRepository extends JpaRepository<Avistamiento, Long> {

    /** Cuenta avistamientos agrupando por especie, de la más vista a la menos vista. */
    @Query("""
           SELECT new com.isw2.avistamientos.dto.ResumenEspecie(a.especie, COUNT(a))
           FROM Avistamiento a
           GROUP BY a.especie
           ORDER BY COUNT(a) DESC, a.especie ASC
           """)
    List<ResumenEspecie> contarPorEspecie();
}
