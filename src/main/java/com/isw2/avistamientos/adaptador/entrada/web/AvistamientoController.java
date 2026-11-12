package com.isw2.avistamientos.adaptador.entrada.web;

import com.isw2.avistamientos.aplicacion.puerto.entrada.GestionarAvistamientos;
import com.isw2.avistamientos.dominio.Avistamiento;
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
 * Adaptador de entrada web: traduce HTTP + JSON a llamadas al puerto GestionarAvistamientos
 * y la respuesta del núcleo a JSON. No tiene reglas de negocio.
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
    public List<AvistamientoResponse> listar() {
        return avistamientos.listar().stream().map(AvistamientoResponse::desde).toList();
    }

    /**
     * GET /avistamientos/resumen → 200 con el conteo por especie (bonus).
     * Spring prioriza esta ruta fija sobre /{id}, así que "resumen" no se confunde con un id.
     */
    @GetMapping("/resumen")
    public List<ResumenEspecieResponse> resumen() {
        return avistamientos.resumirPorEspecie().stream().map(ResumenEspecieResponse::desde).toList();
    }

    /** GET /avistamientos/{id} → 200 con el avistamiento, o 404 si no existe. */
    @GetMapping("/{id}")
    public AvistamientoResponse obtener(@PathVariable Long id) {
        return AvistamientoResponse.desde(avistamientos.obtener(id));
    }

    /**
     * POST /avistamientos → 201 con el avistamiento creado, o 400 si faltan datos.
     * @Valid dispara las validaciones de AvistamientoRequest antes de entrar al método.
     * Además se devuelve el header Location con la URL del nuevo recurso.
     */
    @PostMapping
    public ResponseEntity<AvistamientoResponse> crear(@Valid @RequestBody AvistamientoRequest nuevo) {
        Avistamiento guardado = avistamientos.registrar(nuevo.aDominio());
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(guardado.getId())
                .toUri();
        return ResponseEntity.created(ubicacion).body(AvistamientoResponse.desde(guardado));
    }

    /** PUT /avistamientos/{id} → 200 con el avistamiento actualizado, 404 si no existe o 400 si faltan datos. */
    @PutMapping("/{id}")
    public AvistamientoResponse actualizar(@PathVariable Long id, @Valid @RequestBody AvistamientoRequest datos) {
        return AvistamientoResponse.desde(avistamientos.actualizar(id, datos.aDominio()));
    }

    /** DELETE /avistamientos/{id} → 204 sin cuerpo, o 404 si no existe. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        avistamientos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
