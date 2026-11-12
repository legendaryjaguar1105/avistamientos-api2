package com.isw2.avistamientos.adaptador.salida.persistencia;

import com.isw2.avistamientos.dominio.Avistamiento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * La fila de la tabla "avistamientos". Es un detalle del adaptador de persistencia:
 * el dominio nunca la ve. Conserva la misma tabla y columnas que antes del refactor,
 * así la base H2 que ya existe sigue funcionando.
 */
@Entity
@Table(name = "avistamientos")
public class AvistamientoEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String especie;

    @Column(nullable = false, length = 150)
    private String lugar;

    @Column(nullable = false, length = 10)
    private String fecha;

    @Column(nullable = false, length = 100)
    private String observador;

    /** JPA necesita un constructor vacío. */
    protected AvistamientoEntidad() {
    }

    static AvistamientoEntidad desdeDominio(Avistamiento a) {
        AvistamientoEntidad e = new AvistamientoEntidad();
        e.id = a.getId();
        e.especie = a.getEspecie();
        e.lugar = a.getLugar();
        e.fecha = a.getFecha();
        e.observador = a.getObservador();
        return e;
    }

    Avistamiento aDominio() {
        return new Avistamiento(id, especie, lugar, fecha, observador);
    }
}
