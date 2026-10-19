# Respuestas a la corrección: segunda parte

Este documento recorre cada punto de la devolución y explica **qué cambiamos, dónde está y por qué**. Pueden usarlo para preparar la defensa. Antes de entregar, lean cada clase que se menciona: el profesor pidió explícitamente que puedan explicar el código.

---

## 1. "No se observa una implementación clara de SOLID, interfaces, delegación de dependencias ni separación de responsabilidades"

### Antes

`SistemaMundoMarino` (317 líneas) guardaba las colecciones, validaba permisos, aplicaba reglas de negocio y buscaba usuarios. El menú de consola (541 líneas) mezclaba la lectura de teclado con la lógica.

### Ahora: capas con una sola responsabilidad cada una

```text
web / consola      ← cómo se muestra (no tiene reglas de negocio)
      │
servicio           ← reglas de negocio (qué se puede hacer)
      │   usa interfaces
dao (interfaces)   ← contrato: guardar, buscar, eliminar
      │   implementadas por
dao.json / dao.jdbc← cómo se guarda (archivo JSON o base de datos)
      │
modelo             ← entidades + validaciones propias
```

### SOLID, principio por principio (con la clase donde se ve)

| Principio | Dónde se aplica | Explicación corta |
|---|---|---|
| **S**: responsabilidad única | `ZonaServicio`, `EspecieServicio`, `AsignacionGuiaServicio`… | Cada servicio se ocupa de una sola cosa. `VerificadorHorarios` tiene **una** regla (no superponer recorridos) y la usan todos los que la necesitan. `Consola` solo lee y escribe; los menús solo deciden qué mostrar. `Validaciones` salió de `Usuario`, porque una especie no tiene por qué depender de Usuario para validar un texto. |
| **O**: abierto/cerrado | `ConPermisos` + `ControlAcceso`, `Controlador`, `MenuRol`, `DaoJson<T>` | Para agregar un rol nuevo (por ejemplo `Veterinario`) se crea su clase con sus permisos y **no se toca** `ControlAcceso`. Para agregar una funcionalidad web se crea otro `Controlador` y se suma a la lista de `ServidorWeb`. Para agregar una entidad nueva se extiende `DaoJson<T>` sin reescribir el CRUD. |
| **L**: sustitución de Liskov | `Usuario` → `Administrador` / `Guia` / `Cuidador`; `EspecieDao` → `EspecieDaoJson` / `EspecieDaoJdbc` | Donde se espera un `Usuario` sirve cualquier subclase, y donde se espera un `EspecieDao` sirve cualquier implementación. Los servicios funcionan igual con las dos. |
| **I**: segregación de interfaces | `Dao<T,ID>` + interfaces por entidad | Cada DAO expone solo lo que su entidad necesita: `UsuarioDao` tiene `buscarPorNombreUsuario` y `AsignacionGuiaDao` tiene `buscarPorGuia`. Nadie depende de métodos que no usa. |
| **D**: inversión de dependencias | Constructores de todos los servicios, `Servicios`, `FabricaDao` | Los servicios reciben **interfaces** por constructor (`ZonaServicio(ZonaDao zonas, EspecieDao especies)`). No hacen `new ZonaDaoJson()`. Quien decide qué implementación usar es `GrupoCTema12.main` (la "raíz de composición"). |

### Delegación de dependencias (inyección por constructor)

Está todo en **`servicio/Servicios.java`**: es el único lugar donde se hace `new` de los servicios y se les pasa lo que necesitan. Por ejemplo:

```java
itinerarios = new ItinerarioServicio(dao.itinerarios(), dao.especies(), dao.zonas(),
        dao.asignacionesGuia(), horarios);
```

`ItinerarioServicio` no sabe si esos DAO leen un archivo o una base de datos.

### Interfaces que usamos y para qué

| Interfaz | Para qué |
|---|---|
| `Identificable` | Todo lo que se guarda tiene `getId()`. Permite el DAO genérico `DaoJson<T extends Identificable>`. |
| `ConPermisos` | Cada usuario dice qué permisos tiene. Reemplaza al `instanceof`. |
| `Dao<T, ID>` y sus 7 subinterfaces | Contrato de persistencia. |
| `FabricaDao` | Permite cambiar de JSON a MySQL en una sola línea. |
| `Controlador` | Cada grupo de rutas web se registra solo. |
| `MenuRol` | Cada menú de consola dice a qué usuario le corresponde. |
| `Manejador` | Lo que hace cada ruta web (se escribe con lambdas). |

---

## 2. "Gran parte de la lógica está unificada; cuesta agregar funcionalidades sin modificar código existente"

Tres ejemplos concretos de cómo se agrega algo **sin modificar** lo que ya existe:

1. **Nuevo rol "Veterinario":** crear `class Veterinario extends Empleado` con `getPermisos()` y un `MenuVeterinario implements MenuRol`. `ControlAcceso`, `MenuPrincipal` y los servicios existentes no cambian.
2. **Guardar en MySQL:** escribir las clases `...DaoJdbc` (ya hay un ejemplo completo: `dao/jdbc/EspecieDaoJdbc.java`) y una `FabricaDaoJdbc`. En `main` se cambia `new FabricaDaoJson(...)` por `new FabricaDaoJdbc(...)`. Ningún servicio cambia.
3. **Nueva pantalla web:** crear `XControlador implements Controlador` y agregarlo a la lista de `ServidorWeb`. En el frontend, cada pestaña del administrador es solo una configuración de `crearSeccion()` (`src/web/js/vistas/admin.js`).

---

## 3. "¿Comprenden la implementación de `UUID` en la clase `Usuario`?"

- **Qué es:** `java.util.UUID` representa un identificador de 128 bits. `UUID.randomUUID()` genera uno de versión 4: 122 bits son aleatorios (de un generador criptográficamente seguro). Hay 2¹²² combinaciones, así que la probabilidad de que dos se repitan es despreciable en la práctica.
- **Por qué lo usamos:** cada objeto obtiene un id único **sin depender** de un contador global ni de la base de datos. Eso permite crear objetos en memoria, en la consola o en la web y guardarlos después sin choques. Además, en la API los ids no son correlativos (1, 2, 3…), así que no se pueden adivinar recorriendo números.
- **Qué cambiamos y por qué:** antes el id se generaba en la declaración del atributo, `private final UUID id = UUID.randomUUID();`. Con serialización Java funcionaba porque al leer el `.dat` se restauraban los campos. Con archivos JSON o con una base de datos, en cambio, **cada vez que se reconstruyera el objeto se generaría un id nuevo**, y se romperían todas las relaciones. Por eso ahora hay dos constructores:

  ```java
  public Administrador(String nombreUsuario, String hash, String nombre) {      // alta: id nuevo
      this(UUID.randomUUID(), nombreUsuario, hash, nombre);
  }
  public Administrador(UUID id, String nombreUsuario, String hash, String nombre) { // el DAO lo reconstruye con su id
      super(id, nombreUsuario, hash, nombre);
  }
  ```

- **Cómo se guarda:** como texto (`"3589334b-8533-47fc-..."`) en el JSON y como `CHAR(36)` en la base (`docs/schema.sql`). Se vuelve a convertir con `UUID.fromString(...)`.

---

## 4. "¿Por qué `Empleado` sigue siendo abstracta si ya hereda de otra clase abstracta?"

Heredar de una clase abstracta **no hace abstracta** a la subclase: en Java una clase es abstracta solo si se la declara `abstract`. Analizamos si hacía falta y concluimos que **sí**, por dos razones:

1. **De diseño (la razón importante):** en Mundo Marino no existe "un empleado" a secas; siempre es un Guía o un Cuidador. Si `Empleado` fuera concreta se podría escribir `new Empleado(...)`: un usuario sin rol definido y sin permisos coherentes. `abstract` hace que eso no compile.
2. **Del compilador:** `Empleado` no implementa `getRol()` ni `getPermisos()`, que son abstractos en `Usuario` / `ConPermisos`. Una clase que no implementa todos los métodos abstractos que hereda **tiene** que ser abstracta; si no, no compila.

`Empleado` existe para **no repetir** dirección, teléfono, fecha de ingreso y su validación en `Guia` y `Cuidador`. Además, `PersonalServicio` trabaja con `Empleado` para listar o actualizar a cualquiera de los dos (polimorfismo).

> Si alguna vez quisieran que fuera concreta, tendrían que implementar `getRol()` y `getPermisos()` en Empleado, y no habría un rol ni unos permisos razonables para devolver.

---

## 5. "¿Por qué `Administrador` es `final`? ¿Cuál es el beneficio concreto?"

`final` impide que alguien escriba `class X extends Administrador`. El beneficio concreto en este sistema es la **seguridad del control de acceso**:

- `Administrador` es la clase con todos los permisos (`ADMINISTRAR_PARQUE`).
- Si se pudiera heredar, alguien podría crear `class AdminFalso extends Administrador` y redefinir `getPermisos()` o `getRol()`. Ese objeto "es un" Administrador para el compilador, pero se comporta distinto, y se rompería la garantía de que "Administrador = estos permisos exactos".
- Con `final`, el comportamiento del administrador es **exactamente** el de esta clase y nadie lo puede alterar por herencia.

Por el mismo motivo, `Guia` y `Cuidador` también son `final`: no está previsto que existan subtipos. Si en el futuro hiciera falta un "Guía senior", se decide conscientemente sacar el `final` o crear una clase nueva que extienda `Empleado`.

---

## 6. "¿Dónde están los DAO con su contrato de DAO genérico? ¿Por qué se sugiere esta estructura?"

### Dónde están

| Archivo | Qué es |
|---|---|
| `dao/Dao.java` | **Contrato genérico** `Dao<T, ID>`: `buscarPorId`, `listarTodos`, `guardar`, `actualizar`, `eliminar`, `existe`. |
| `dao/ZonaDao.java`, `EspecieDao.java`, `HabitatDao.java`, `ItinerarioDao.java`, `UsuarioDao.java`, `AsignacionGuiaDao.java`, `AsignacionCuidadoDao.java` | Una interfaz por entidad: `extends Dao<Entidad, UUID>` y agrega sus consultas propias. |
| `dao/json/DaoJson.java` | Implementación **genérica** del CRUD con archivos JSON. Lo hereda cada DAO concreto (patrón *Template Method*: la subclase solo dice cómo convertir su entidad a JSON y de vuelta). |
| `dao/json/*DaoJson.java` | Las 7 implementaciones que usa el programa. |
| `dao/FabricaDao.java` + `dao/json/FabricaDaoJson.java` | Crea todos los DAO juntos. |
| `dao/jdbc/EspecieDaoJdbc.java` | Ejemplo completo del mismo contrato contra MySQL con `PreparedStatement` y transacciones. |

### Por qué se sugiere

1. **Separar "qué hago" de "dónde lo guardo".** El servicio dice `especies.guardar(e)` y no le importa si va a un archivo o a una tabla.
2. **Poder cambiar la persistencia sin tocar la lógica.** Lo demostramos: pasamos de serialización (`.dat`) a JSON sin cambiar ninguna regla de negocio, y la clase JDBC implementa la misma interfaz.
3. **No repetir código.** El CRUD genérico se escribe una vez (`DaoJson<T>`). Antes, `SistemaMundoMarino` tenía un `Map` y métodos casi iguales por cada tipo.
4. **Probar más fácil.** Un servicio se puede probar pasándole un DAO en memoria falso.
5. **Es el contrato que después respeta la base de datos** (`docs/schema.sql` tiene una tabla por DAO).

---

## 7. Otros cambios que hicimos (y por qué)

| Cambio | Motivo |
|---|---|
| Las entidades se relacionan **por id** (`Especie.zonaId`, `Itinerario.especieIds`) y no con referencias cruzadas | Antes había que mantener sincronizados los dos lados (`registrarEspecieDesdeEspecie`, etc.). Con ids, cada DAO guarda una sola entidad, sin ciclos, igual que una base de datos con claves foráneas. |
| Las **zonas del itinerario se calculan** a partir de las especies (`ItinerarioServicio.zonasDe`) | Antes, si una especie cambiaba de zona, el itinerario quedaba con la zona vieja. |
| Una sola regla de horarios (`VerificadorHorarios`) que considera la **duración** | Antes, al asignar se permitía que un guía tuviera dos recorridos a la misma hora en itinerarios distintos, pero al modificar no. Ahora un guía no puede tener recorridos superpuestos, ni al asignar ni al cambiar la hora ni al alargar un itinerario. |
| **Contraseñas** con hash PBKDF2 (`seguridad/HashContrasena.java`) | Antes se ingresaba solo con el nombre de usuario. |
| Permisos con `ConPermisos` en lugar de `instanceof` | Pedido de la corrección y principio abierto/cerrado. |
| Usuario único validado al registrar | Antes se podían crear dos guías con el mismo usuario. |
| Archivos JSON en lugar de `ObjectOutputStream` | Se pueden leer a mano, no se rompen si cambia una clase y no permiten ejecutar código al leer un archivo manipulado. |
| Las modificaciones crean un objeto nuevo y validado y recién después lo guardan | Si un dato es inválido, el objeto guardado no queda modificado a medias. |
| No se puede borrar algo que está en uso (409 Conflicto) | Evita, por ejemplo, un itinerario apuntando a una especie inexistente. |

---

## 8. Sobre el uso de IA

Usamos IA como apoyo. Para cumplir con lo que pide el profesor, cada integrante debería poder explicar sin leer:

- [ ] Qué hace cada capa y por qué un servicio recibe interfaces en el constructor.
- [ ] Qué es un `record` (`Datos.Zona`, `MisRecorridosServicio.RecorridoAsignado`) y por qué es inmutable.
- [ ] Qué es `Optional` y por qué `Especie.getZonaId()` lo devuelve en lugar de `null`.
- [ ] Cómo funciona un genérico acotado: `DaoJson<T extends Identificable>`.
- [ ] Qué es el patrón *Template Method* (`aJson` / `desdeJson` en `DaoJson`).
- [ ] Por qué la contraseña se guarda como hash con sal y no se puede "desencriptar".
- [ ] Qué es una cookie `HttpOnly` y `SameSite=Strict` (ver `web/GestorSesiones.java`).
- [ ] Por qué `MisRecorridosServicio` usa el id del usuario de la sesión y no uno que mande el navegador.

Si algún punto no está claro, conviene investigarlo antes de la entrega. Son preguntas probables en la defensa.
