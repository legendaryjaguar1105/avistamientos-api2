package com.isw2.avistamientos.aplicacion;

import com.isw2.avistamientos.aplicacion.puerto.entrada.GestionarAvistamientos;
import com.isw2.avistamientos.aplicacion.puerto.salida.RepositorioDeAvistamientos;
import com.isw2.avistamientos.dominio.Avistamiento;
import com.isw2.avistamientos.dominio.AvistamientoInvalidoException;
import com.isw2.avistamientos.dominio.AvistamientoNoEncontradoException;
import com.isw2.avistamientos.dominio.ResumenEspecie;
import com.isw2.avistamientos.dominio.ValidadorDeAvistamiento;

import java.util.List;
import java.util.Map;

/**
 * El interior del hexágono: implementa el puerto de entrada usando el puerto de salida.
 * No conoce Spring, ni HTTP, ni JPA; recibe sus dependencias por el constructor.
 */
public class ServicioDeAvistamientos implements GestionarAvistamientos {

    private final RepositorioDeAvistamientos repositorio;
    private final ValidadorDeAvistamiento validador;

    public ServicioDeAvistamientos(RepositorioDeAvistamientos repositorio, ValidadorDeAvistamiento validador) {
        this.repositorio = repositorio;
        this.validador = validador;
    }

    @Override
    public List<Avistamiento> listar() {
        return repositorio.listarTodos();
    }

    @Override
    public List<ResumenEspecie> resumirPorEspecie() {
        return repositorio.contarPorEspecie();
    }

    @Override
    public Avistamiento obtener(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new AvistamientoNoEncontradoException(id));
    }

    @Override
    public Avistamiento registrar(Avistamiento nuevo) {
        validarReglasDeNegocio(nuevo);
        nuevo.setId(null); // el id siempre lo asigna quien guarda
        return repositorio.guardar(nuevo);
    }

    @Override
    public Avistamiento actualizar(Long id, Avistamiento datos) {
        validarReglasDeNegocio(datos);
        Avistamiento existente = obtener(id);
        existente.setEspecie(datos.getEspecie());
        existente.setLugar(datos.getLugar());
        existente.setFecha(datos.getFecha());
        existente.setObservador(datos.getObservador());
        return repositorio.guardar(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!repositorio.existePorId(id)) {
            throw new AvistamientoNoEncontradoException(id);
        }
        repositorio.eliminarPorId(id);
    }

    private void validarReglasDeNegocio(Avistamiento avistamiento) {
        Map<String, String> errores = validador.validar(avistamiento);
        if (!errores.isEmpty()) {
            throw new AvistamientoInvalidoException(errores);
        }
    }
}
