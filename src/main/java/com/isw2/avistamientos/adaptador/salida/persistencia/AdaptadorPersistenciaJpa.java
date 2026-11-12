package com.isw2.avistamientos.adaptador.salida.persistencia;

import com.isw2.avistamientos.aplicacion.puerto.salida.RepositorioDeAvistamientos;
import com.isw2.avistamientos.dominio.Avistamiento;
import com.isw2.avistamientos.dominio.ResumenEspecie;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto de persistencia con Spring Data JPA y H2.
 * Traduce entre el Avistamiento del dominio y la fila AvistamientoEntidad.
 * Para cambiar de base de datos basta con otro adaptador que implemente el mismo puerto.
 */
@Component
public class AdaptadorPersistenciaJpa implements RepositorioDeAvistamientos {

    private final AvistamientoJpaRepository jpa;

    public AdaptadorPersistenciaJpa(AvistamientoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Avistamiento> listarTodos() {
        return jpa.findAll(Sort.by("id")).stream()
                .map(AvistamientoEntidad::aDominio)
                .toList();
    }

    @Override
    public Optional<Avistamiento> buscarPorId(Long id) {
        return jpa.findById(id).map(AvistamientoEntidad::aDominio);
    }

    @Override
    public Avistamiento guardar(Avistamiento avistamiento) {
        return jpa.save(AvistamientoEntidad.desdeDominio(avistamiento)).aDominio();
    }

    @Override
    public boolean existePorId(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public void eliminarPorId(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public List<ResumenEspecie> contarPorEspecie() {
        return jpa.contarPorEspecie();
    }
}
