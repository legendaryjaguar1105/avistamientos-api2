package com.isw2.avistamientos.exception;

import java.util.Map;

/** Error de regla de negocio que se traduce a HTTP 400. */
public class AvistamientoInvalidoException extends RuntimeException {
    private final Map<String, String> campos;
    public AvistamientoInvalidoException(Map<String, String> campos) {
        super("Uno o más campos no cumplen las reglas del avistamiento");
        this.campos = campos;
    }
    public Map<String, String> getCampos() { return campos; }
}
