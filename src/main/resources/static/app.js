// =========================================================
// Libreta de avistamientos: lógica de la página
// Solo usa fetch() para hablar con la API REST.
// =========================================================

const API = "/avistamientos";

const formulario = document.getElementById("formulario");
const tituloFormulario = document.getElementById("titulo-formulario");
const botonGuardar = document.getElementById("boton-guardar");
const botonCancelar = document.getElementById("boton-cancelar");
const mensaje = document.getElementById("mensaje");
const filas = document.getElementById("filas");
const vacio = document.getElementById("vacio");
const conteo = document.getElementById("conteo");

const CAMPOS = ["especie", "lugar", "fecha", "observador"];

// Si tiene un número, el formulario está editando ese avistamiento (PUT).
// Si es null, está registrando uno nuevo (POST).
let idEnEdicion = null;

// ---------- Cargar datos ----------

async function cargarTodo() {
  await Promise.all([cargarAvistamientos(), cargarResumen()]);
}

async function cargarAvistamientos() {
  const respuesta = await fetch(API); // GET /avistamientos
  const avistamientos = await respuesta.json();
  dibujarTabla(avistamientos);
}

async function cargarResumen() {
  const respuesta = await fetch(API + "/resumen"); // GET /avistamientos/resumen
  const resumen = await respuesta.json();
  dibujarConteo(resumen);
}

// ---------- Dibujar en pantalla ----------

// Crea un elemento con texto. Se usa textContent (no innerHTML)
// para que un texto como "<script>" no se ejecute.
function crear(etiqueta, texto, clase) {
  const elemento = document.createElement(etiqueta);
  if (texto !== undefined) elemento.textContent = texto;
  if (clase) elemento.className = clase;
  return elemento;
}

function dibujarTabla(avistamientos) {
  filas.replaceChildren();
  vacio.hidden = avistamientos.length > 0;

  for (const a of avistamientos) {
    const fila = document.createElement("tr");
    if (a.id === idEnEdicion) fila.classList.add("en-edicion");

    fila.append(
      crear("td", a.id),
      crear("td", a.fecha, "celda-fecha"),
      crear("td", a.especie, "celda-especie"),
      crear("td", a.lugar),
      crear("td", a.observador)
    );

    const acciones = crear("td", undefined, "celda-acciones");

    const editar = crear("button", "Editar", "boton-tabla");
    editar.type = "button";
    editar.addEventListener("click", () => empezarEdicion(a));

    const eliminar = crear("button", "Eliminar", "boton-tabla peligro");
    eliminar.type = "button";
    eliminar.addEventListener("click", () => eliminarAvistamiento(a));

    acciones.append(editar, eliminar);
    fila.append(acciones);
    filas.append(fila);
  }
}

function dibujarConteo(resumen) {
  conteo.replaceChildren();

  if (resumen.length === 0) {
    conteo.append(crear("li", "Aún no hay especies registradas.", "conteo-vacio"));
    return;
  }

  for (const { especie, cantidad } of resumen) {
    const item = document.createElement("li");
    item.append(
      crear("span", especie, "nombre-especie"),
      dibujarMarcas(cantidad),
      crear("span", cantidad, "cantidad")
    );
    conteo.append(item);
  }
}

// Dibuja las marcas de conteo: grupos de 5 (4 rayas + 1 que las cruza).
// Máximo 30 marcas para que no se desborde; el número exacto se ve al lado.
function dibujarMarcas(cantidad) {
  const contenedor = crear("span", undefined, "marcas");
  contenedor.setAttribute("aria-hidden", "true");

  let restantes = Math.min(cantidad, 30);
  while (restantes > 0) {
    const enEsteGrupo = Math.min(restantes, 5);
    const grupo = crear("span", undefined, "grupo");
    // En un grupo completo solo se dibujan 4 rayas; la quinta es la diagonal (CSS ::after)
    const rayasVerticales = enEsteGrupo === 5 ? 4 : enEsteGrupo;
    for (let i = 0; i < rayasVerticales; i++) {
      grupo.append(crear("span", undefined, "raya"));
    }
    if (enEsteGrupo === 5) grupo.classList.add("completo");
    contenedor.append(grupo);
    restantes -= enEsteGrupo;
  }
  return contenedor;
}

// ---------- Guardar (POST o PUT) ----------

formulario.addEventListener("submit", async (evento) => {
  evento.preventDefault();
  limpiarErrores();

  // Arma el JSON a partir de los campos del formulario
  const datos = {};
  for (const campo of CAMPOS) {
    datos[campo] = formulario.elements[campo].value.trim();
  }

  const editando = idEnEdicion !== null;
  const url = editando ? `${API}/${idEnEdicion}` : API;

  const respuesta = await fetch(url, {
    method: editando ? "PUT" : "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(datos),
  });

  if (respuesta.status === 201 || respuesta.status === 200) {
    const guardado = await respuesta.json();
    mostrarMensaje(editando
      ? `Avistamiento #${guardado.id} actualizado.`
      : `Avistamiento #${guardado.id} registrado.`);
    terminarEdicion();
    await cargarTodo();
  } else if (respuesta.status === 400) {
    const error = await respuesta.json();
    mostrarErrores(error.campos || {});
    mostrarMensaje(error.mensaje || "Revisa los datos del formulario.", true);
  } else if (respuesta.status === 404) {
    mostrarMensaje("Ese avistamiento ya no existe. Se recargó la lista.", true);
    terminarEdicion();
    await cargarTodo();
  } else {
    mostrarMensaje(`Error inesperado (${respuesta.status}). Intenta de nuevo.`, true);
  }
});

// ---------- Eliminar (DELETE) ----------

async function eliminarAvistamiento(avistamiento) {
  const confirmado = confirm(
    `¿Eliminar el avistamiento #${avistamiento.id} (${avistamiento.especie})?`
  );
  if (!confirmado) return;

  const respuesta = await fetch(`${API}/${avistamiento.id}`, { method: "DELETE" });

  if (respuesta.status === 204) {
    mostrarMensaje(`Avistamiento #${avistamiento.id} eliminado.`);
  } else if (respuesta.status === 404) {
    mostrarMensaje("Ese avistamiento ya había sido eliminado.", true);
  } else {
    mostrarMensaje(`Error inesperado (${respuesta.status}).`, true);
  }

  if (avistamiento.id === idEnEdicion) terminarEdicion();
  await cargarTodo();
}

// ---------- Modo edición ----------

function empezarEdicion(avistamiento) {
  idEnEdicion = avistamiento.id;
  limpiarErrores();
  for (const campo of CAMPOS) {
    formulario.elements[campo].value = avistamiento[campo];
  }
  tituloFormulario.textContent = `Editar avistamiento #${avistamiento.id}`;
  botonGuardar.textContent = "Guardar cambios";
  botonCancelar.hidden = false;
  mostrarMensaje("");
  formulario.elements.especie.focus();
  cargarAvistamientos(); // para resaltar la fila que se está editando
}

function terminarEdicion() {
  idEnEdicion = null;
  formulario.reset();
  tituloFormulario.textContent = "Nuevo avistamiento";
  botonGuardar.textContent = "Registrar avistamiento";
  botonCancelar.hidden = true;
}

botonCancelar.addEventListener("click", () => {
  terminarEdicion();
  limpiarErrores();
  mostrarMensaje("");
  cargarAvistamientos();
});

// ---------- Mensajes y errores ----------

function mostrarMensaje(texto, esError = false) {
  mensaje.textContent = texto;
  mensaje.classList.toggle("es-error", esError);
}

function mostrarErrores(campos) {
  for (const [campo, texto] of Object.entries(campos)) {
    const input = formulario.elements[campo];
    const parrafo = document.querySelector(`[data-error="${campo}"]`);
    if (input) input.setAttribute("aria-invalid", "true");
    if (parrafo) parrafo.textContent = texto;
  }
}

function limpiarErrores() {
  for (const campo of CAMPOS) {
    formulario.elements[campo].removeAttribute("aria-invalid");
    document.querySelector(`[data-error="${campo}"]`).textContent = "";
  }
}

// Al abrir la página, cargar la lista y el conteo
cargarTodo().catch(() =>
  mostrarMensaje("No se pudo conectar con la API. ¿Está corriendo en el puerto 8080?", true)
);
