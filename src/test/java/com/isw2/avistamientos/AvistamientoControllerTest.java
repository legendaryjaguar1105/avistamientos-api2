package com.isw2.avistamientos;

import com.isw2.avistamientos.repository.AvistamientoRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración: levantan la aplicación completa con una base H2
 * en memoria (separada de la real) y hacen peticiones HTTP simuladas con MockMvc.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:pruebas;DB_CLOSE_DELAY=-1",
        "app.datos-ejemplo=false"
})
@AutoConfigureMockMvc
class AvistamientoControllerTest {

    private static final String COLIBRI = """
            {"especie": "colibrí", "lugar": "Jardín Botánico", "fecha": "2026-09-29", "observador": "Ana"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AvistamientoRepository repositorio;

    @BeforeEach
    void limpiarBaseDeDatos() {
        repositorio.deleteAll();
    }

    /** Crea un avistamiento con POST y devuelve el id que asignó la API. */
    private long crear(String json) throws Exception {
        String respuesta = mockMvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(respuesta, "$.id")).longValue();
    }

    // ---------- GET /avistamientos ----------

    @Test
    void listarSinDatosDevuelve200ConListaVacia() throws Exception {
        mockMvc.perform(get("/avistamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listarDevuelveLosAvistamientosCreados() throws Exception {
        crear(COLIBRI);
        mockMvc.perform(get("/avistamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].especie").value("colibrí"));
    }

    // ---------- GET /avistamientos/{id} ----------

    @Test
    void obtenerExistenteDevuelve200() throws Exception {
        long id = crear(COLIBRI);
        mockMvc.perform(get("/avistamientos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.lugar").value("Jardín Botánico"));
    }

    @Test
    void obtenerInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/avistamientos/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void obtenerConIdNoNumericoDevuelve400() throws Exception {
        mockMvc.perform(get("/avistamientos/abc"))
                .andExpect(status().isBadRequest());
    }

    // ---------- POST /avistamientos ----------

    @Test
    void crearDevuelve201ConIdYLocation() throws Exception {
        mockMvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COLIBRI))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.especie").value("colibrí"))
                .andExpect(jsonPath("$.observador").value("Ana"));
    }

    @Test
    void crearIgnoraElIdQueEnviaElCliente() throws Exception {
        String conId = """
                {"id": 500, "especie": "copetón", "lugar": "Usaquén", "fecha": "2026-09-29", "observador": "Luis"}
                """;
        long id = crear(conId);
        org.junit.jupiter.api.Assertions.assertNotEquals(500L, id);
    }

    @Test
    void crearConDatosFaltantesDevuelve400() throws Exception {
        mockMvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"especie": "colibrí"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.lugar").exists())
                .andExpect(jsonPath("$.campos.fecha").exists())
                .andExpect(jsonPath("$.campos.observador").exists());
    }

    @Test
    void crearConFechaMalFormadaDevuelve400() throws Exception {
        mockMvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"especie": "colibrí", "lugar": "Chía", "fecha": "29/09/2026", "observador": "Ana"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.fecha").exists());
    }

    @Test
    void crearConFechaPosteriorAHoyDevuelve400YExplicaElCampo() throws Exception {
        String futuro = java.time.LocalDate.now().plusDays(1).toString();
        mockMvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"especie\":\"colibrí\",\"lugar\":\"Chía\",\"fecha\":\"" + futuro + "\",\"observador\":\"Ana\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.fecha").value("La fecha no puede ser posterior a hoy"));
    }

    @Test
    void crearConJsonInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{esto no es json"))
                .andExpect(status().isBadRequest());
    }

    // ---------- PUT /avistamientos/{id} ----------

    @Test
    void actualizarExistenteDevuelve200() throws Exception {
        long id = crear(COLIBRI);
        mockMvc.perform(put("/avistamientos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"especie": "colibrí", "lugar": "Parque Simón Bolívar", "fecha": "2026-09-30", "observador": "Ana"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.lugar").value("Parque Simón Bolívar"))
                .andExpect(jsonPath("$.fecha").value("2026-09-30"));
    }

    @Test
    void actualizarInexistenteDevuelve404() throws Exception {
        mockMvc.perform(put("/avistamientos/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COLIBRI))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarConFechaPosteriorAHoyDevuelve400() throws Exception {
        long id = crear(COLIBRI);
        String futuro = java.time.LocalDate.now().plusDays(1).toString();
        mockMvc.perform(put("/avistamientos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"especie\":\"colibrí\",\"lugar\":\"Chía\",\"fecha\":\"" + futuro + "\",\"observador\":\"Ana\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.fecha").exists());
    }

    @Test
    void actualizarConDatosFaltantesDevuelve400() throws Exception {
        long id = crear(COLIBRI);
        mockMvc.perform(put("/avistamientos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"especie": ""}
                                """))
                .andExpect(status().isBadRequest());
    }

    // ---------- DELETE /avistamientos/{id} ----------

    @Test
    void eliminarExistenteDevuelve204YYaNoExiste() throws Exception {
        long id = crear(COLIBRI);
        mockMvc.perform(delete("/avistamientos/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/avistamientos/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarInexistenteDevuelve404() throws Exception {
        mockMvc.perform(delete("/avistamientos/9999"))
                .andExpect(status().isNotFound());
    }

    // ---------- GET /avistamientos/resumen (bonus) ----------

    @Test
    void resumenCuentaPorEspecie() throws Exception {
        crear(COLIBRI);
        crear(COLIBRI);
        crear("""
                {"especie": "barranquero", "lugar": "La Florida", "fecha": "2026-09-28", "observador": "Luis"}
                """);
        mockMvc.perform(get("/avistamientos/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].especie").value("colibrí"))
                .andExpect(jsonPath("$[0].cantidad").value(2))
                .andExpect(jsonPath("$[1].especie").value("barranquero"))
                .andExpect(jsonPath("$[1].cantidad").value(1));
    }
}
