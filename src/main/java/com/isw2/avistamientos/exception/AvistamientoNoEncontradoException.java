package com.isw2.avistamientos.exception;

/** Se lanza cuando piden un id que no existe. El manejador global la convierte en un 404. */
public class AvistamientoNoEncontradoException extends RuntimeException {

    public AvistamientoNoEncontradoException(Long id) {
        super("No existe un avistamiento con id " + id);
    }
}
