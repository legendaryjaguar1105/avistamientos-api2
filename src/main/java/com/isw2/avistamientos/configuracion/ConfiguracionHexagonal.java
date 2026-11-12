package com.isw2.avistamientos.configuracion;

import com.isw2.avistamientos.aplicacion.ServicioDeAvistamientos;
import com.isw2.avistamientos.aplicacion.puerto.entrada.GestionarAvistamientos;
import com.isw2.avistamientos.aplicacion.puerto.salida.RepositorioDeAvistamientos;
import com.isw2.avistamientos.dominio.ValidadorDeAvistamiento;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ensambla el hexágono: le pasa al servicio el adaptador de salida que Spring encontró.
 * Es el único lugar donde se conectan las piezas; por eso el núcleo no necesita
 * anotaciones de Spring (como en el ejemplo de Cockburn, donde el adaptador de
 * entrada le entrega a la aplicación el repositorio que debe usar).
 */
@Configuration
public class ConfiguracionHexagonal {

    @Bean
    public GestionarAvistamientos gestionarAvistamientos(RepositorioDeAvistamientos repositorio) {
        return new ServicioDeAvistamientos(repositorio, new ValidadorDeAvistamiento());
    }
}
