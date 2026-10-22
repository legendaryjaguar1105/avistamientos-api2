package com.isw2.avistamientos.validation;

import com.isw2.avistamientos.model.Avistamiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias con AAA: Arrange, Act, Assert. El reloj fijo evita pruebas dependientes del día real. */
class ValidadorDeAvistamientoTest {
    private ValidadorDeAvistamiento validador;
    private final LocalDate hoy = LocalDate.of(2026, 10, 21);
    private Avistamiento valido(String especie, String fecha) {
        return new Avistamiento(especie, "Parque", fecha, "Ana");
    }
    @BeforeEach void preparar() {
        // Arrange: el stub del reloj siempre informa el 21/10/2026.
        validador = new ValidadorDeAvistamiento(Clock.fixed(Instant.parse("2026-10-21T12:00:00Z"), ZoneOffset.UTC));
    }
    @Test void especieDeDosCaracteresEsRechazada() {
        // Arrange
        Avistamiento a = valido("AB", hoy.toString());
        // Act
        var errores = validador.validar(a);
        // Assert
        assertEquals("especie", errores.keySet().iterator().next());
        assertTrue(errores.containsKey("especie"));
    }
    @Test void especieDeTresCaracteresEsAceptada() {
        Avistamiento a = valido("Ave", hoy.toString());
        assertFalse(validador.validar(a).containsKey("especie"));
    }
    @Test void especieDeCincuentaCaracteresEsAceptada() {
        Avistamiento a = valido("A".repeat(50), hoy.toString());
        assertFalse(validador.validar(a).containsKey("especie"));
    }
    @Test void especieDeCincuentaYUnCaracteresEsRechazada() {
        Avistamiento a = valido("A".repeat(51), hoy.toString());
        assertTrue(validador.validar(a).containsKey("especie"));
    }
    @Test void fechaDeHoyEsAceptada() {
        Avistamiento a = valido("Colibrí", hoy.toString());
        assertFalse(validador.validar(a).containsKey("fecha"));
    }
    @Test void fechaDeMañanaEsRechazada() {
        Avistamiento a = valido("Colibrí", hoy.plusDays(1).toString());
        assertEquals("La fecha no puede ser posterior a hoy", validador.validar(a).get("fecha"));
    }
    @Test void fechaConFormatoIncorrectoEsRechazada() {
        Avistamiento a = valido("Colibrí", "21/10/2026");
        assertTrue(validador.validar(a).containsKey("fecha"));
    }
    @Test void fechaImposibleEsRechazada() {
        Avistamiento a = valido("Colibrí", "2026-02-30");
        assertTrue(validador.validar(a).containsKey("fecha"));
    }
    @Test void lugarVacioEsRechazado() {
        Avistamiento a = new Avistamiento("Colibrí", " ", hoy.toString(), "Ana");
        assertTrue(validador.validar(a).containsKey("lugar"));
    }
    @Test void observadorVacioEsRechazado() {
        Avistamiento a = new Avistamiento("Colibrí", "Parque", hoy.toString(), "");
        assertTrue(validador.validar(a).containsKey("observador"));
    }
    @Test void registroCompletoYValidoNoTieneErrores() {
        Avistamiento a = valido("Colibrí", hoy.toString());
        assertTrue(validador.validar(a).isEmpty());
    }
}
