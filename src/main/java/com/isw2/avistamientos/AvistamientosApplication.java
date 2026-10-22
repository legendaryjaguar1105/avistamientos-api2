package com.isw2.avistamientos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada. Al ejecutar este main, Spring Boot levanta un servidor
 * en http://localhost:8080 con la API y la página web.
 */
@SpringBootApplication
public class AvistamientosApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvistamientosApplication.class, args);
    }
}
