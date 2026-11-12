package com.isw2.avistamientos.controller;

import com.isw2.avistamientos.aplicacion.puerto.entrada.GestionarAvistamientos;
import com.isw2.avistamientos.dto.ResumenEspecie;
import com.isw2.avistamientos.model.Avistamiento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Los endpoints del recurso /avistamientos.
 * La URL nombra el recurso y el método HTTP indica la acción.
 * @RestController hace que todo lo que se retorna se convierta a JSON.
 */
@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final GestionarAvistamientos avistamientos;

    public AvistamientoController(GestionarAvistamientos avistamientos) {
        this.avistamientos = avistamientos;
    }

    /** GET /avistamientos → 200 con la lista (vacía si no hay ninguno). */
    @GetMapping
    public List<Avistamiento> listar() {
        return avistamientos.listar();
    }

    /**
     * GET /avistamientos/resumen → 200 con el conteo por especie (bonus).
     * Spring prioriza esta ruta fija sobre /{id}, así que "resumen" no se confunde con un id.
     */
    @GetMapping("/resumen")
    public List<ResumenEspecie> resumen() {
        return avistamientos.resumirPorEspecie();
    }

    /** GET /avistamientos/{id} → 200 con el avistamiento, o 404 si no existe. */
    @GetMapping("/{id}")
    public Avistamiento obtener(@PathVariable Long id) {
        return avistamientos.obtener(id);
    }

    /**
     * POST /avistamientos → 201 con el avistamiento creado, o 400 si faltan datos.
     * @Valid dispara las validaciones del modelo antes de entrar al método.
     * Además se devuelve el header Location con la URL del nuevo recurso.
     */
    @PostMapping
    public ResponseEntity<Avistamiento> crear(@Valid @RequestBody Avistamiento nuevo) {
        Avistamiento guardado = avistamientos.registrar(nuevo);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(guardado.getId())
                .toUri();
        return ResponseEntity.created(ubicacion).body(guardado);
    }

    /** PUT /avistamientos/{id} → 200 con el avistamiento actualizado, 404 si no existe o 400 si faltan datos. */
    @PutMapping("/{id}")
    public Avistamiento actualizar(@PathVariable Long id, @Valid @RequestBody Avistamiento datos) {
        return avistamientos.actualizar(id, datos);
    }

    /** DELETE /avistamientos/{id} → 204 sin cuerpo, o 404 si no existe. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        avistamientos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
