package com.isw2.avistamientos.adaptador.salida.persistencia;

import com.isw2.avistamientos.aplicacion.puerto.salida.RepositorioDeAvistamientos;
import com.isw2.avistamientos.model.Avistamiento;
import com.isw2.avistamientos.dto.ResumenEspecie;
import com.isw2.avistamientos.repository.AvistamientoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Adaptador de salida: implementa el puerto de persistencia con Spring Data JPA. */
@Component
public class AdaptadorPersistenciaJpa implements RepositorioDeAvistamientos {

    private final AvistamientoRepository jpa;

    public AdaptadorPersistenciaJpa(AvistamientoRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Avistamiento> listarTodos() {
        return jpa.findAll(Sort.by("id"));
    }

    @Override
    public Optional<Avistamiento> buscarPorId(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Avistamiento guardar(Avistamiento avistamiento) {
        return jpa.save(avistamiento);
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
