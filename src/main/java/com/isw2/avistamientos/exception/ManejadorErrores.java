package com.isw2.avistamientos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convierte los errores en respuestas JSON con el código de estado correcto,
 * para que el cliente siempre reciba algo como:
 * {"status": 404, "error": "No encontrado", "mensaje": "No existe un avistamiento con id 7"}
 */
@RestControllerAdvice
public class ManejadorErrores {

    /** 400: incumplimiento de reglas de negocio propias del avistamiento. */
    @ExceptionHandler(AvistamientoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> avistamientoInvalido(AvistamientoInvalidoException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "Datos inválidos", ex.getMessage(), ex.getCampos());
    }

    /** 404: el id no existe. */
    @ExceptionHandler(AvistamientoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(AvistamientoNoEncontradoException ex) {
        return respuesta(HttpStatus.NOT_FOUND, "No encontrado", ex.getMessage(), null);
    }

    /** 400: faltan datos o no cumplen las validaciones del modelo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> datosInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return respuesta(HttpStatus.BAD_REQUEST, "Datos inválidos",
                "Revisa los campos marcados", campos);
    }

    /** 400: el cuerpo no es un JSON válido o viene vacío. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonMalFormado(HttpMessageNotReadableException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "JSON inválido",
                "El cuerpo de la petición debe ser un JSON válido", null);
    }

    /** 400: el id de la URL no es un número (por ejemplo /avistamientos/abc). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> idInvalido(MethodArgumentTypeMismatchException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "Parámetro inválido",
                "El id debe ser un número entero", null);
    }

    /** 415: enviaron algo que no es JSON (falta el header Content-Type: application/json). */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> tipoNoSoportado(HttpMediaTypeNotSupportedException ex) {
        return respuesta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Formato no soportado",
                "Envía los datos como JSON con el header Content-Type: application/json", null);
    }

    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus status, String error,
                                                          String mensaje, Map<String, String> campos) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("status", status.value());
        cuerpo.put("error", error);
        cuerpo.put("mensaje", mensaje);
        if (campos != null) {
            cuerpo.put("campos", campos);
        }
        return ResponseEntity.status(status).body(cuerpo);
    }
}
