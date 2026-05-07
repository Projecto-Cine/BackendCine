# Informe Final — feature/screening-seat-crud

---

## 1. Resumen Ejecutivo

Se ha implementado el CRUD completo de **Theater**, **Seat**, **Movie** y **Screening** (con la entidad intermedia `ScreeningSeat`) en la rama `feature/screening-seat-crud`, mergeada a `dev` y publicada en el repositorio remoto.

La implementación cubre entidades JPA, repositorios, DTOs, mappers, servicios con lógica de negocio y controladores REST, acompañada de 54 tests (unitarios de servicio y tests de controlador con MockMvc) que pasan al 100%.

Tecnologías: **Spring Boot 4.0.6**, **Java 25**, **JPA/Hibernate**, **Lombok**, **MySQL**, **Cloudinary** (para pósters).

---

## 2. Entidades Creadas

| Entidad | Tabla | Descripción |
|---|---|---|
| `Theater` | `theaters` | Sala de cine con capacidad |
| `Seat` | `seats` | Asiento físico de una sala (`fila`, `numero`, `tipo`) |
| `Movie` | `movies` | Película con metadatos y URL de póster |
| `Screening` | `screenings` | Proyección: película + sala + fecha/hora + precio |
| `ScreeningSeat` | `screening_seats` | Tabla intermedia que registra si un asiento está ocupado en una proyección concreta |

### Relaciones

```
Theater (1) ──── (*) Seat
Movie   (1) ──── (*) Screening
Theater (1) ──── (*) Screening
Screening (1) ── (*) ScreeningSeat (*) ── (1) Seat
```

### Campos destacados

- `Screening.asientosDisponibles` — contador denormalizado, se decrementa/incrementa al reservar/liberar.
- `ScreeningSeat.ocupado` — flag booleano por asiento por proyección.
- `Seat.tipo` — enum `SeatType` (STANDARD, VIP, ACCESIBILIDAD).
- `Movie.posterUrl` — URL de Cloudinary.

---

## 3. Endpoints Disponibles

### Theaters — `/api/theaters`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/theaters` | Listar todas las salas |
| GET | `/api/theaters/{id}` | Obtener sala por ID |
| POST | `/api/theaters` | Crear sala (genera asientos automáticamente) |
| PUT | `/api/theaters/{id}` | Actualizar sala |
| DELETE | `/api/theaters/{id}` | Eliminar sala |
| GET | `/api/theaters/{id}/seats` | Listar asientos de una sala |

### Seats — `/api/seats`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/seats` | Listar todos los asientos |
| GET | `/api/seats/{id}` | Obtener asiento por ID |
| POST | `/api/seats` | Crear asiento manual |
| PUT | `/api/seats/{id}` | Actualizar asiento |
| DELETE | `/api/seats/{id}` | Eliminar asiento |

### Movies — `/api/movies`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/movies` | Listar todas las películas |
| GET | `/api/movies/{id}` | Obtener película por ID |
| POST | `/api/movies` | Crear película |
| PUT | `/api/movies/{id}` | Actualizar película |
| DELETE | `/api/movies/{id}` | Eliminar película |
| POST | `/api/movies/{id}/poster` | Subir póster a Cloudinary |

### Screenings — `/api/screenings`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/screenings` | Listar todas las proyecciones |
| GET | `/api/screenings/upcoming` | Proyecciones futuras |
| GET | `/api/screenings/{id}` | Obtener proyección por ID |
| GET | `/api/screenings/movie/{movieId}` | Proyecciones de una película |
| POST | `/api/screenings` | Crear proyección |
| PUT | `/api/screenings/{id}` | Actualizar proyección |
| DELETE | `/api/screenings/{id}` | Eliminar proyección |
| POST | `/api/screenings/{id}/seats/{seatId}/reserve` | Reservar asiento |
| POST | `/api/screenings/{id}/seats/{seatId}/release` | Liberar asiento |

Todas las respuestas siguen el wrapper estándar:
```json
{ "success": true, "message": "...", "data": { ... }, "errors": null }
```

---

## 4. Lógica de Negocio

### Generación automática de asientos
Al crear una `Theater`, `TheaterServiceImpl.generateSeats()` genera automáticamente los asientos: filas de A en adelante, 10 asientos por fila. La capacidad determina el número total de asientos.

### Creación de proyección
Al crear una `Screening`, se verifica que `fechaHora` sea futura (`ScreeningAlreadyPassedException` si no lo es) y se generan registros `ScreeningSeat` para cada `Seat` de la sala, todos con `ocupado = false`. `asientosDisponibles` se inicializa con la capacidad de la sala.

### Reserva de asiento
`reserveSeat(screeningId, seatId)`:
1. Verifica que la proyección sea futura.
2. Verifica que `asientosDisponibles > 0` → `ScreeningFullException`.
3. Verifica que `ScreeningSeat.ocupado == false` → `SeatAlreadyTakenException`.
4. Marca `ocupado = true` y decrementa `asientosDisponibles`.

### Liberación de asiento
`releaseSeat(screeningId, seatId)`: verifica que el asiento esté ocupado, lo libera e incrementa `asientosDisponibles`.

### Excepciones de negocio

| Excepción | HTTP | Caso |
|---|---|---|
| `ResourceNotFoundException` | 404 | Entidad no encontrada |
| `ConflictException` | 409 | Nombre/título duplicado |
| `ScreeningAlreadyPassedException` | 400 | Fecha de proyección en el pasado |
| `ScreeningFullException` | 409 | Sin asientos disponibles |
| `SeatAlreadyTakenException` | 409 | Asiento ya ocupado |

---

## 5. Componentes Frontend

No aplica en esta iteración — el proyecto es una API REST backend. Los endpoints están listos para ser consumidos por cualquier cliente frontend (React, Angular, etc.) o herramienta como Postman.

---

## 6. Tests

**Total: 54 tests — 0 fallos**

| Clase de test | Tipo | Nº tests |
|---|---|---|
| `UserControllerTest` | MockMvc (WebMvcTest) | 7 |
| `UserServiceTest` | Unitario (Mockito) | 3 |
| `MovieControllerTest` | MockMvc (WebMvcTest) | 8 |
| `MovieServiceTest` | Unitario (Mockito) | 3 |
| `TheaterControllerTest` | MockMvc (WebMvcTest) | 9 |
| `TheaterServiceTest` | Unitario (Mockito) | 3 |
| `ScreeningControllerTest` | MockMvc (WebMvcTest) | 13 |
| `ScreeningServiceTest` | Unitario (Mockito) | 4 |
| `DemoApplicationTests` | Spring context load | 1 |
| **Total** | | **54** |

Casos cubiertos en `ScreeningControllerTest`: GET all, GET upcoming, GET by ID (200/404), GET by movie, POST (201/400), DELETE (200/404), reserve seat (200/409 full/409 taken), release seat (200).

Casos cubiertos en `ScreeningServiceTest`: fecha pasada lanza excepción, sala llena lanza excepción, asiento ya ocupado lanza excepción, reserva exitosa decrementa contador.

---

## 7. Decisiones Técnicas

| Decisión | Justificación |
|---|---|
| `ScreeningSeat` como entidad intermedia | Permite rastrear el estado por asiento por proyección sin lógica adicional en memoria |
| `asientosDisponibles` denormalizado | Evita `COUNT(*)` en cada consulta; se mantiene consistente con operaciones atómicas JPA |
| `@ToString.Exclude @EqualsAndHashCode.Exclude` en relaciones `@ManyToOne` | Previene `LazyInitializationException` y ciclos infinitos con `@Data` de Lombok |
| `@NoArgsConstructor @AllArgsConstructor` en todos los DTOs de request | Jackson 3.x (en Spring Boot 4) requiere constructor sin argumentos para deserializar JSON |
| `@MockitoBean` en lugar de `@MockBean` | API correcta para Spring Boot 4; `@MockBean` fue eliminado |
| `tools.jackson.databind.ObjectMapper` | Jackson 3.x cambió el namespace de `com.fasterxml.jackson` a `tools.jackson` |
| `spring-boot-starter-webmvc-test` como dependencia de test separada | Spring Boot 4 extrajo los starters de test por tecnología; sin él, `@WebMvcTest` no resuelve |
| Generación automática de asientos al crear sala | Garantiza consistencia entre capacidad declarada y asientos reales sin pasos manuales |

---

## 8. Pendiente / Deuda Técnica

- **Autenticación y autorización**: los endpoints no están protegidos. Pendiente implementar Spring Security con JWT para rutas de admin vs cliente.
- **Paginación**: los endpoints GET all devuelven listas completas. Convendría añadir `Pageable` para colecciones grandes.
- **Validación de solapamiento de proyecciones**: no se verifica si una sala ya tiene proyección en el mismo horario.
- **Notificaciones**: no hay mecanismo de notificación al usuario cuando se libera un asiento en proyección llena.
- **Tests de integración con base de datos real**: los tests actuales son unitarios con Mockito o MockMvc sin contexto de base de datos. Quedan pendientes tests con `@DataJpaTest` o Testcontainers.
- **Gestión de usuarios en reservas**: la reserva de asiento no está asociada a ningún usuario. Pendiente vincular `ScreeningSeat` con `User` para el flujo completo de compra de entradas.
