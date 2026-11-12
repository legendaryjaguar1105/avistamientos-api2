package com.isw2.avistamientos;

import com.isw2.avistamientos.aplicacion.puerto.entrada.GestionarAvistamientos;
import com.isw2.avistamientos.dominio.Avistamiento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Carga unos avistamientos de ejemplo la primera vez que arranca la API
 * (solo si no hay ninguno), para tener algo que ver al probar.
 * Se desactiva con app.datos-ejemplo=false en application.properties.
 */
@Component
@ConditionalOnProperty(name = "app.datos-ejemplo", havingValue = "true", matchIfMissing = true)
public class DatosDeEjemplo implements CommandLineRunner {

    private final GestionarAvistamientos avistamientos;

    public DatosDeEjemplo(GestionarAvistamientos avistamientos) {
        this.avistamientos = avistamientos;
    }

    @Override
    public void run(String... args) {
        if (!avistamientos.listar().isEmpty()) {
            return;
        }
        List.of(
                new Avistamiento("colibrí", "Jardín Botánico de Bogotá", "2026-09-12", "Ana Pérez"),
                new Avistamiento("barranquero", "Parque La Florida", "2026-09-14", "Luis Gómez"),
                new Avistamiento("colibrí", "Humedal Santa María del Lago", "2026-09-20", "Luis Gómez"),
                new Avistamiento("tingua bogotana", "Humedal Juan Amarillo", "2026-09-21", "Ana Pérez"),
                new Avistamiento("copetón", "Universidad Nacional", "2026-09-25", "Sofía Ruiz")
        ).forEach(avistamientos::registrar);
    }
}
