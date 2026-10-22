package com.isw2.avistamientos;

import com.isw2.avistamientos.model.Avistamiento;
import com.isw2.avistamientos.repository.AvistamientoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Carga unos avistamientos de ejemplo la primera vez que arranca la API
 * (solo si la base de datos está vacía), para tener algo que ver al probar.
 * Se desactiva con app.datos-ejemplo=false en application.properties.
 */
@Component
@ConditionalOnProperty(name = "app.datos-ejemplo", havingValue = "true", matchIfMissing = true)
public class DatosDeEjemplo implements CommandLineRunner {

    private final AvistamientoRepository repositorio;

    public DatosDeEjemplo(AvistamientoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() > 0) {
            return;
        }
        repositorio.saveAll(List.of(
                new Avistamiento("colibrí", "Jardín Botánico de Bogotá", "2026-09-12", "Ana Pérez"),
                new Avistamiento("barranquero", "Parque La Florida", "2026-09-14", "Luis Gómez"),
                new Avistamiento("colibrí", "Humedal Santa María del Lago", "2026-09-20", "Luis Gómez"),
                new Avistamiento("tingua bogotana", "Humedal Juan Amarillo", "2026-09-21", "Ana Pérez"),
                new Avistamiento("copetón", "Universidad Nacional", "2026-09-25", "Sofía Ruiz")
        ));
    }
}
