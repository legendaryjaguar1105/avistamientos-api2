# API REST de avistamientos de aves

Trabajo en clase de Ingeniería de Software 2 (ISW-2), sesión 17.

API REST para registrar avistamientos de aves observadas en el campo. Está hecha en **Java 17 con Spring Boot** y guarda los datos en una base de datos embebida **H2**. Incluye además una página web sencilla para usar la API desde el navegador.

**Autor:** _Tu nombre aquí_

## Tecnologías

- Java 17
- Spring Boot 3.5 (Spring Web, Spring Data JPA, Validation)
- H2 (base de datos embebida, guardada en archivo)
- Maven (incluido mediante Maven Wrapper, no hay que instalarlo)
- JUnit 5 y MockMvc para las pruebas
- HTML, CSS y JavaScript para la página web

## Requisitos

Solo se necesita tener instalado el **JDK 17 o superior**. Para comprobarlo:

```bash
java -version
```

No hace falta instalar Maven: el proyecto trae el Maven Wrapper (`mvnw` y `mvnw.cmd`), que descarga la versión correcta la primera vez que se ejecuta.

## Instalación y ejecución

1. Clonar el repositorio y entrar a la carpeta:

   ```bash
   git clone https://github.com/legendaryjaguar1105/avistamientos-api.git
   cd avistamientos-api
   ```

2. Levantar la API:

   | Sistema | Comando |
   |---|---|
   | Linux / macOS | `./mvnw spring-boot:run` |
   | Windows (PowerShell) | `.\mvnw.cmd spring-boot:run` |
   | Windows (CMD) | `mvnw.cmd spring-boot:run` |

   La primera vez tarda un poco porque descarga Maven y las dependencias. Cuando aparezca en la consola `Started AvistamientosApplication`, la API está lista.

3. Abrir en el navegador:
   - Página web: http://localhost:8080
   - API: http://localhost:8080/avistamientos
   - Consola de la base de datos H2: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./data/avistamientos`, usuario `sa`, sin contraseña)

Para detener la API: `Ctrl + C` en la terminal.

La primera vez que arranca, si la base de datos está vacía, se cargan 5 avistamientos de ejemplo. Los datos quedan guardados en la carpeta `data/`; para empezar de cero basta con borrarla con la API detenida.

## El recurso: avistamiento

```json
{
  "id": 1,
  "especie": "colibrí",
  "lugar": "Jardín Botánico de Bogotá",
  "fecha": "2026-09-12",
  "observador": "Ana Pérez"
}
```

| Campo | Tipo | Reglas |
|---|---|---|
| `id` | número | Lo asigna el sistema. Si el cliente lo envía, se ignora. |
| `especie` | texto | Entre 3 y 50 caracteres. |
| `lugar` | texto | Obligatorio, máximo 150 caracteres. |
| `fecha` | texto | Obligatorio, formato `AAAA-MM-DD`. |
| `observador` | texto | Entre 3 y 50 caracteres. |

## Endpoints

| Acción | Método y ruta | Respuestas |
|---|---|---|
| Listar todos | `GET /avistamientos` | `200` con la lista |
| Ver uno | `GET /avistamientos/{id}` | `200` con el avistamiento, `404` si no existe |
| Registrar | `POST /avistamientos` | `201` con el avistamiento creado, `400` si faltan datos |
| Actualizar | `PUT /avistamientos/{id}` | `200` con el avistamiento, `404` si no existe, `400` si faltan datos |
| Eliminar | `DELETE /avistamientos/{id}` | `204` sin contenido, `404` si no existe |
| Resumen por especie (bonus) | `GET /avistamientos/resumen` | `200` con la cantidad de avistamientos por especie |

Otros casos que también responden con error en JSON: un id que no es número (`/avistamientos/abc`) devuelve `400`, un cuerpo que no es JSON válido devuelve `400` y una petición sin `Content-Type: application/json` devuelve `415`.

Todos los errores tienen esta forma:

```json
{
  "status": 400,
  "error": "Datos inválidos",
  "mensaje": "Revisa los campos marcados",
  "campos": {
    "lugar": "El lugar es obligatorio",
    "fecha": "La fecha es obligatoria",
    "observador": "El observador es obligatorio"
  }
}
```

## Ejemplos con curl

Los ejemplos que envían datos usan los archivos JSON de la carpeta `ejemplos/`, así funcionan igual en Linux, macOS y Windows. Se ejecutan desde la carpeta del proyecto.

> **Windows (PowerShell):** escribir `curl.exe` en lugar de `curl`, porque en PowerShell `curl` puede ser un alias de otro comando. En CMD, `curl` funciona normal.

La opción `-i` muestra el código de estado y los headers de la respuesta.

### Listar todos los avistamientos

```bash
curl -i http://localhost:8080/avistamientos
```

Respuesta: `200 OK` con un arreglo JSON.

### Ver un avistamiento

```bash
curl -i http://localhost:8080/avistamientos/1
```

Respuesta: `200 OK` con el avistamiento. Si el id no existe (por ejemplo `/avistamientos/999`): `404 Not Found`.

### Registrar un avistamiento

```bash
curl -i -X POST http://localhost:8080/avistamientos -H "Content-Type: application/json" -d "@ejemplos/nuevo.json"
```

Respuesta: `201 Created`, con el avistamiento creado (incluido su `id`) y el header `Location` con su URL.

Ejemplo de datos incompletos, que responde `400 Bad Request`:

```bash
curl -i -X POST http://localhost:8080/avistamientos -H "Content-Type: application/json" -d "@ejemplos/incompleto.json"
```

En Linux o macOS también se puede enviar el JSON directamente:

```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"barranquero","lugar":"Parque La Florida","fecha":"2026-09-29","observador":"Ana"}'
```

### Actualizar un avistamiento

```bash
curl -i -X PUT http://localhost:8080/avistamientos/1 -H "Content-Type: application/json" -d "@ejemplos/actualizado.json"
```

Respuesta: `200 OK` con el avistamiento actualizado, o `404 Not Found` si el id no existe.

### Eliminar un avistamiento

```bash
curl -i -X DELETE http://localhost:8080/avistamientos/1
```

Respuesta: `204 No Content`. Si se repite la misma petición: `404 Not Found`.

### Resumen por especie (bonus)

```bash
curl -i http://localhost:8080/avistamientos/resumen
```

Respuesta: `200 OK`, ordenado de la especie más vista a la menos vista:

```json
[
  { "especie": "colibrí", "cantidad": 2 },
  { "especie": "barranquero", "cantidad": 1 }
]
```

## Pruebas (bonus)

El proyecto tiene pruebas de integración en `src/test/java` que levantan la aplicación con una base H2 en memoria (separada de los datos reales) y verifican cada endpoint con sus códigos de estado: 200, 201, 204, 400 y 404.

| Sistema | Comando |
|---|---|
| Linux / macOS | `./mvnw test` |
| Windows (PowerShell) | `.\mvnw.cmd test` |
| Windows (CMD) | `mvnw.cmd test` |

## Estructura del proyecto

```
src/main/java/com/isw2/avistamientos/
├── AvistamientosApplication.java      Punto de entrada (main)
├── DatosDeEjemplo.java                Carga datos de ejemplo si la base está vacía
├── model/Avistamiento.java            El recurso (entidad JPA + validaciones)
├── repository/AvistamientoRepository  Acceso a la base de datos H2
├── controller/AvistamientoController  Los endpoints REST
├── dto/ResumenEspecie.java            Fila del resumen por especie
└── exception/                         Manejo de errores 400, 404 y 415 en JSON
src/main/resources/
├── application.properties             Configuración (puerto, H2, JPA)
└── static/                            Página web (index.html, styles.css, app.js)
src/test/java/                         Pruebas de integración
ejemplos/                              JSON de ejemplo para los curl
```

## Uso de IA

Para este trabajo usé **Claude** (de Anthropic), modelo **Claude Opus 5.5**, como apoyo para estructurar el proyecto, escribir el código, las pruebas y este README. Revisé y entiendo el código publicado.

## Código reutilizado

- Los scripts `mvnw`, `mvnw.cmd` y `.mvn/wrapper/maven-wrapper.properties` son el [Apache Maven Wrapper](https://maven.apache.org/tools/wrapper/) (licencia Apache 2.0), sin modificaciones de lógica.
- Las tipografías Alegreya y Alegreya Sans se cargan desde Google Fonts (licencia SIL Open Font License).

## Pruebas unitarias, integración y cobertura (taller sesión 22)

La validación de reglas de negocio está aislada en `ValidadorDeAvistamiento`, que puede probarse sin iniciar Spring ni conectarse a H2. El validador recibe un `Clock`; las pruebas unitarias usan un reloj fijo para que el caso de “hoy” y “mañana” no cambie al ejecutar la suite otro día.

Comandos (Windows CMD):

```bat
mvnw.cmd test
mvnw.cmd verify
```

JaCoCo está configurado con las ejecuciones `prepare-agent` y `report`. Ejecute `mvnw.cmd verify`; el informe HTML debe quedar en `target/site/jacoco/index.html`. No se debe reportar un porcentaje hasta ejecutar la herramienta.
