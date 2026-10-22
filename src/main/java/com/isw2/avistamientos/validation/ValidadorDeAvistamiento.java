package com.isw2.avistamientos.validation;

import com.isw2.avistamientos.model.Avistamiento;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Valida las reglas de negocio sin depender del controlador ni de la base de datos. */
public class ValidadorDeAvistamiento {
    private final Clock reloj;

    /** En producción usa el reloj del sistema; en pruebas se puede inyectar un reloj fijo. */
    public ValidadorDeAvistamiento() { this(Clock.systemDefaultZone()); }
    public ValidadorDeAvistamiento(Clock reloj) { this.reloj = reloj; }

    /** Devuelve los errores por campo; un mapa vacío significa que el registro es válido. */
    public Map<String, String> validar(Avistamiento a) {
        Map<String, String> errores = new LinkedHashMap<>();
        if (a == null) { errores.put("avistamiento", "El avistamiento es obligatorio"); return errores; }
        String especie = a.getEspecie();
        if (especie == null || especie.trim().length() < 3 || especie.trim().length() > 50)
            errores.put("especie", "La especie debe tener entre 3 y 50 caracteres");
        if (a.getLugar() == null || a.getLugar().isBlank())
            errores.put("lugar", "El lugar no puede estar vacío");
        if (a.getObservador() == null || a.getObservador().isBlank())
            errores.put("observador", "El observador no puede estar vacío");
        String fecha = a.getFecha();
        if (fecha == null || !fecha.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            errores.put("fecha", "La fecha debe tener el formato AAAA-MM-DD");
        } else {
            try {
                LocalDate fechaParseada = LocalDate.parse(fecha);
                if (fechaParseada.isAfter(LocalDate.now(reloj)))
                    errores.put("fecha", "La fecha no puede ser posterior a hoy");
            } catch (DateTimeParseException ex) {
                errores.put("fecha", "La fecha no es una fecha válida del calendario");
            }
        }
        return errores;
    }
}
