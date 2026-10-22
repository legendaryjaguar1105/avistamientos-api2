# Informe de justificación de pruebas — API de avistamientos

**Asignatura:** Ingeniería de Software 2 (ISW-2)  
**Proyecto:** API REST de avistamientos de aves  
**Tecnologías:** Java 17, Spring Boot, JUnit 5, MockMvc y H2

## 1. Cambios realizados en el código

Se creó `ValidadorDeAvistamiento`, una clase independiente del controlador y de la base de datos. Su método `validar` devuelve un mapa con los errores por campo, o un mapa vacío cuando el registro es válido. La clase comprueba que la especie tenga entre 3 y 50 caracteres, que la fecha respete `AAAA-MM-DD`, corresponda a una fecha real y no sea posterior al día actual, y que lugar y observador no estén vacíos. El controlador ejecuta esta regla en POST y PUT. `AvistamientoInvalidoException` y `ManejadorErrores` traducen los errores de negocio a HTTP 400 con el campo y el mensaje correspondiente.

También se ajustó la restricción declarativa de `especie` para coincidir con el rango exigido. La clase permite recibir un `Clock`; en las pruebas unitarias se usa `Clock.fixed`, un **stub** que mantiene constante la fecha y evita que el caso “mañana” se vuelva válido al ejecutar las pruebas en otra fecha. Esto mejora la testeabilidad porque la lógica puede ejecutarse de forma aislada y repetible.

## 2. Tabla de casos unitarios: partición de equivalencia y valores límite

| Campo | Valor de prueba | Tipo de caso | Resultado esperado | Justificación |
|---|---|---|---|---|
| Especie | `AB` (2 caracteres) | Límite inválido | Rechazado | Un carácter por debajo del mínimo de 3. |
| Especie | `Ave` (3 caracteres) | Límite válido | Aceptado | Comprueba el extremo inferior permitido. |
| Especie | 50 caracteres | Límite válido | Aceptado | Comprueba el extremo superior permitido. |
| Especie | 51 caracteres | Límite inválido | Rechazado | Un carácter por encima del máximo. |
| Fecha | Día actual fijado: `2026-10-21` | Límite válido | Aceptada | El día actual está permitido. |
| Fecha | Día siguiente: `2026-10-22` | Límite inválido | Rechazada | La regla prohíbe fechas futuras. |
| Fecha | `21/10/2026` | Partición inválida | Rechazada | No utiliza el formato ISO requerido. |
| Fecha | `2026-02-30` | Partición inválida | Rechazada | Tiene forma de fecha, pero no existe en el calendario. |
| Lugar | Espacios en blanco | Partición inválida | Rechazado | Un lugar vacío no identifica dónde ocurrió el avistamiento. |
| Observador | Cadena vacía | Partición inválida | Rechazado | Debe identificarse al observador. |
| Registro | Especie, fecha, lugar y observador válidos | Representativo | Aceptado | Comprueba la combinación válida común. |

La partición separa valores válidos de inválidos; los valores límite se concentran alrededor de 3 y 50 caracteres y alrededor del día actual. Estos valores son más informativos que elegir longitudes o fechas arbitrarias.

## 3. Por qué se eligió cada nivel de prueba

- **Unitarias:** `especieDeDosCaracteresEsRechazada` y `fechaDeMañanaEsRechazada` invocan directamente el validador. Comprueban reglas de negocio sin levantar Spring ni acceder a la base de datos, por lo que los fallos se localizan rápidamente.
- **Integración:** `crearDevuelve201ConIdYLocation` recorre la petición HTTP, la deserialización JSON, la validación, el controlador, JPA/H2 y la serialización de la respuesta. Una prueba unitaria del validador no detectaría, por ejemplo, un `Location` incorrecto, un error al persistir o un JSON de respuesta con campos ausentes.
- **Integración de ciclo de vida:** `eliminarExistenteDevuelve204YYaNoExiste` verifica tanto el 204 del DELETE como el 404 al consultar luego el mismo identificador. Así se comprueba el efecto real, no solamente el código HTTP inicial.

MockMvc realiza peticiones HTTP simuladas dentro del contexto completo de Spring; es una prueba de integración de la aplicación, no una prueba unitaria del controlador.

## 4. Dobles de prueba

En las pruebas unitarias se utiliza un **stub** de `Clock`, configurado para devolver siempre el instante `2026-10-21T12:00:00Z`. El doble controla una dependencia variable del entorno —la fecha actual— sin modificar el reloj del equipo. Sin este stub, la prueba de “hoy” y “mañana” dependería del día de ejecución y podría fallar o cambiar de significado con el tiempo.

En las pruebas de integración, H2 en memoria es una base aislada para la suite, no un mock del repositorio. Cada prueba elimina sus registros en `@BeforeEach` y prepara los datos que necesita. No se usa un mock del repositorio en integración porque el objetivo es comprobar que la persistencia JPA funciona de verdad en una base de prueba.

## 5. Pruebas que no se incluyeron y por qué

1. **Prueba de carga con miles de peticiones concurrentes:** no se incluyó porque el objetivo del taller es validar reglas, endpoints y efectos funcionales; una prueba de rendimiento requiere objetivos de concurrencia y tiempos de respuesta que no define el enunciado.
2. **Prueba contra una base de datos de producción o un servicio externo:** no se incluyó porque la aplicación usa H2 y no requiere servicios externos para los endpoints descritos. Usar datos reales haría la suite menos aislada y potencialmente destructiva.
3. **Prueba de cada posible texto de especie:** no se probó cada combinación de caracteres, porque los casos de partición y los límites de longitud verifican la regla relevante sin repetir casos equivalentes. Se podrían añadir pruebas de caracteres Unicode si el sistema incorpora una política específica sobre ellos.

## 6. Cobertura y lectura del reporte

El taller solicita generar cobertura con JaCoCo y revisar al menos una línea o rama no cubierta. Se configuró el plugin JaCoCo en el `pom.xml` con las ejecuciones `prepare-agent` y `report`; el comando `mvnw.cmd verify` debe generar `target/site/jacoco/index.html`. Sin embargo, **no se informa un porcentaje de cobertura ni se afirma que la suite pasó**, porque en el entorno de preparación el Maven Wrapper no pudo descargar Maven desde Maven Central por un error de conexión.

Para completar esta parte en un equipo con conexión, se debe configurar `jacoco-maven-plugin` con `prepare-agent` y `report`, ejecutar `mvnw.cmd verify` y revisar `target/site/jacoco/index.html`. Una rama candidata para revisar es el caso de fecha imposible (`2026-02-30`), que ya cuenta con prueba unitaria; después de ejecutar JaCoCo debe comprobarse el reporte real y escoger una línea o rama que efectivamente aparezca sin cubrir. No sería correcto inventar el porcentaje o afirmar que una rama está descubierta sin consultar ese reporte.

## Comandos de ejecución

Desde la carpeta raíz del proyecto, en Windows CMD:

```bat
mvnw.cmd test
mvnw.cmd verify
```

En Linux/macOS:

```bash
./mvnw test
./mvnw verify
```
