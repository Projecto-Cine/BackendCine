# Lumen Cinema API — Documentación Completa

## Índice

1. [Descripción general](#descripción-general)
2. [Stack tecnológico](#stack-tecnológico)
3. [Arquitectura](#arquitectura)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Base de datos — Entidades](#base-de-datos--entidades)
6. [Seguridad y autenticación](#seguridad-y-autenticación)
7. [Endpoints de la API](#endpoints-de-la-api)
8. [Lógica de negocio](#lógica-de-negocio)
9. [Integración con servicios externos](#integración-con-servicios-externos)
10. [Manejo de errores](#manejo-de-errores)
11. [DTOs](#dtos)
12. [Tests](#tests)
13. [Configuración](#configuración)

---

## Descripción general

**Lumen Cinema API** es una plataforma backend profesional para la gestión integral de un cine. Proporciona una API REST completa construida con Spring Boot 4.0.6 y Java 25, con base de datos MySQL.

**Características principales:**
- CRUD completo de películas, proyecciones y salas
- Sistema de reserva de butacas con control de disponibilidad
- Múltiples tipos de entradas: CHILD / STUDENT / ADULT / SENIOR
- Sistema de descuentos por fidelidad (10+ visitas anuales → 10% descuento)
- Almacenamiento de imágenes en la nube (Cloudinary)
- Notificaciones por email (Gmail SMTP)
- Autenticación JWT
- Gestión de turnos de empleados
- Venta de merchandising y concesiones
- Dashboard de analíticas y reportes
- Seguimiento de incidencias
- 264 tests unitarios pasando
- Documentación Swagger / OpenAPI

---

## Stack tecnológico

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| Lenguaje | Java | 25 |
| Framework | Spring Boot | 4.0.6 |
| Base de datos | MySQL | — |
| ORM | Spring Data JPA / Hibernate | — |
| Seguridad | JWT (JJWT) | 0.12.6 |
| Imágenes | Cloudinary | 1.39.0 |
| Email | Spring Mail / Gmail SMTP | — |
| Documentación | SpringDoc OpenAPI | 3.0.3 |
| Mapeo de DTOs | MapStruct | — |
| Reducción de boilerplate | Lombok | — |
| Tests | JUnit 5 + Mockito + Spring Boot Test | — |

---

## Arquitectura

El proyecto sigue una arquitectura en capas clásica de Spring Boot:

```
Request
  └─► JwtAuthenticationFilter
        └─► Controller  (recibe HTTP, delega al servicio)
              └─► Service  (lógica de negocio)
                    └─► Repository  (acceso a base de datos)
                          └─► MySQL
```

**Patrones aplicados:**
- **DTO pattern** — objetos separados para request y response
- **Service/Impl pattern** — interfaz + implementación para cada servicio
- **ApiResponse wrapper** — todas las respuestas siguen el mismo formato
- **GlobalExceptionHandler** — manejo centralizado de errores con `@RestControllerAdvice`
- **JWT stateless auth** — sin sesiones de servidor

---

## Estructura del proyecto

```
cinema/
├── src/
│   ├── main/
│   │   ├── java/com/cine/demo/
│   │   │   ├── config/               # CorsConfig, CloudinaryConfig, SwaggerConfig, PasswordConfig
│   │   │   ├── controller/           # 22 controladores REST
│   │   │   ├── dto/
│   │   │   │   ├── request/          # DTOs de entrada
│   │   │   │   └── response/         # DTOs de salida (incluye ApiResponse, ApiError)
│   │   │   ├── exception/            # Excepciones personalizadas + GlobalExceptionHandler
│   │   │   ├── mapper/               # Mappers MapStruct
│   │   │   ├── model/
│   │   │   │   ├── enums/            # Enumeraciones
│   │   │   │   ├── converter/        # Conversores JPA
│   │   │   │   └── *.java            # 15 entidades JPA
│   │   │   ├── repository/           # 13 repositorios Spring Data JPA
│   │   │   ├── security/             # JwtAuthenticationFilter, JwtUtil, JwtService, AuthContext
│   │   │   ├── service/              # Interfaces de servicio
│   │   │   │   └── impl/             # Implementaciones
│   │   │   ├── util/                 # PriceCalculator
│   │   │   └── DemoApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/cine/demo/       # 264 tests organizados por feature
├── pom.xml
└── DOCU-BACKEND.md
```

---

## Base de datos — Entidades

La base de datos cuenta con **15 entidades** relacionales.

### User (`clients`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| name | String | Nombre completo |
| email | String | Email único |
| password | String | Contraseña (BCrypt) |
| birthDate | LocalDate | Fecha de nacimiento |
| userType | Enum | Tipo de cliente |
| annualVisits | Integer | Visitas en el año en curso |
| discountActive | Boolean | Si tiene descuento por fidelidad activo |
| role | Enum | Rol del usuario |
| imageUrl | String | URL de foto de perfil (Cloudinary) |
| createdAt / updatedAt | LocalDateTime | Auditoría |

Relaciones: 1-to-many con Purchase, MerchandiseSale, RoomBooking.

---

### Movie (`movie`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| title | String | Título |
| description | String | Sinopsis |
| durationMin | Integer | Duración en minutos |
| genre | String | Género |
| ageRating | Integer | Clasificación por edad |
| posterUrl | String | URL del cartel (Cloudinary) |
| active | Boolean | Si está en cartelera |
| language | String | Idioma |
| schedule | String | Horario genérico |
| createdAt | LocalDateTime | Auditoría |

Relaciones: 1-to-many con Screening.

---

### Theater (`theater`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| name | String | Nombre de la sala |
| capacity | Integer | Capacidad total |
| numRows | Integer | Número de filas |
| numColumns | Integer | Número de columnas |

Relaciones: 1-to-many con Seat y Screening.

---

### Seat (`seat`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| theater_id | Long | FK a Theater |
| row | String | Fila (ej. A, B, C…) |
| number | Integer | Número de butaca |
| type | Enum | STANDARD / VIP |

Restricción única: `(theater_id, row, number)`.

---

### Screening (`screening`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| movie_id | Long | FK a Movie |
| theater_id | Long | FK a Theater |
| startTime | LocalDateTime | Inicio de la sesión |
| endTime | LocalDateTime | Fin de la sesión |
| occupiedSeats | Integer | Butacas ocupadas |
| full | Boolean | Si la sesión está completa |
| basePrice | BigDecimal | Precio base |

Relaciones: many-to-one con Movie y Theater; 1-to-many con ScreeningSeat.

---

### ScreeningSeat (`screening_seat`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| screening_id | Long | FK a Screening |
| seat_id | Long | FK a Seat |
| occupied | Boolean | Si está ocupada |

Restricción única: `(screening_id, seat_id)`.

---

### Purchase (`purchase`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| user_id | Long | FK a User |
| screening_id | Long | FK a Screening |
| status | Enum | Estado de la compra |
| totalAmount | BigDecimal | Total |
| discountApplied | Boolean | Si se aplicó descuento |
| discountAmount | BigDecimal | Importe del descuento |
| emailSent | Boolean | Si se envió email de confirmación |
| createdAt | LocalDateTime | Auditoría |

Relaciones: many-to-one con User y Screening; 1-to-many con Ticket.

---

### Ticket (`ticket`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| purchase_id | Long | FK a Purchase |
| seat_id | Long | FK a Seat |
| screening_id | Long | FK a Screening |
| ticketType | Enum | CHILD / STUDENT / ADULT / SENIOR |
| unitPrice | BigDecimal | Precio unitario calculado |

Restricción única: `(screening_id, seat_id)`.

---

### Employee (`workers`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| name | String | Nombre |
| email | String | Email |
| role | String | Puesto |
| createdAt | LocalDateTime | Auditoría |

Relaciones: 1-to-many con Shift.

---

### Shift (`shift`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| employee_id | Long | FK a Employee |
| shiftDate | LocalDate | Fecha del turno |
| startTime | LocalTime | Hora de inicio |
| endTime | LocalTime | Hora de fin |
| notes | String | Notas |
| status | Enum | Estado del turno |
| createdAt | LocalDateTime | Auditoría |

---

### Merchandise (`merchandise`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| name | String | Nombre |
| description | String | Descripción |
| category | String | Categoría |
| price | BigDecimal | Precio |
| stock | Integer | Stock disponible |
| imageUrl | String | URL de imagen |
| active | Boolean | Si está disponible |
| createdAt | LocalDateTime | Auditoría |

Relaciones: 1-to-many con MerchandiseSale.

---

### MerchandiseSale (`merchandise_sale`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| user_id | Long | FK a User |
| merchandise_id | Long | FK a Merchandise |
| quantity | Integer | Cantidad |
| total | BigDecimal | Total de la venta |
| saleDate | LocalDateTime | Fecha de la venta |

---

### Room (`room`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| name | String | Nombre |
| capacity | Integer | Capacidad |
| roomType | String | Tipo de sala |
| description | String | Descripción |
| pricePerHour | BigDecimal | Precio por hora |
| active | Boolean | Disponible |
| createdAt | LocalDateTime | Auditoría |

Relaciones: 1-to-many con RoomBooking.

---

### RoomBooking (`room_booking`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| room_id | Long | FK a Room |
| user_id | Long | FK a User |
| bookingDate | LocalDate | Fecha de la reserva |
| startTime | LocalTime | Hora de inicio |
| endTime | LocalTime | Hora de fin |
| totalPrice | BigDecimal | Precio total |
| status | Enum | Estado |
| notes | String | Notas |
| createdAt | LocalDateTime | Auditoría |

---

### Incident (`incident`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK |
| title | String | Título |
| description | String | Descripción |
| severity | Enum | Severidad |
| resolved | Boolean | Si está resuelta |
| createdAt / updatedAt | LocalDateTime | Auditoría |

---

## Seguridad y autenticación

### Autenticación JWT

El sistema usa autenticación **stateless basada en JWT** (JSON Web Tokens).

**Flujo:**
1. El cliente hace `POST /api/auth/login` con email y contraseña.
2. El servidor valida las credenciales y devuelve un token JWT.
3. El cliente incluye el token en todas las peticiones: `Authorization: Bearer <token>`.
4. El `JwtAuthenticationFilter` intercepta la petición, valida el token y establece el contexto de autenticación.

**Configuración del token:**
- Algoritmo: HS256 (HMAC-SHA256)
- Expiración: 24 horas (86400000 ms)
- Payload: userId, email, role, iat, exp

**Rutas públicas** (no requieren token):
- `POST /api/auth/login`
- `POST /api/auth/register`

Todas las demás rutas bajo `/api/*` requieren token válido.

**Contraseñas:** BCrypt, con soporte de migración desde contraseñas en texto plano.

---

## Endpoints de la API

La documentación interactiva completa está disponible en Swagger UI:
`http://localhost:8080/swagger-ui.html`

### Auth — `/api/auth`

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/login` | Iniciar sesión, devuelve JWT | No |
| POST | `/register` | Registrar nuevo usuario | No |

### Users — `/api/users`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar todos los usuarios |
| GET | `/{id}` | Obtener usuario por ID |
| POST | `/` | Crear usuario |
| PUT | `/{id}` | Actualizar usuario |
| DELETE | `/{id}` | Eliminar usuario |
| POST | `/{id}/image` | Subir foto de perfil (Cloudinary) |

### Movies — `/api/movies`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar todas las películas |
| GET | `/active` | Listar solo películas en cartelera |
| GET | `/{id}` | Obtener película por ID |
| POST | `/` | Crear película |
| PUT | `/{id}` | Actualizar película |
| DELETE | `/{id}` | Eliminar película |

### Screenings — `/api/screenings`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar todas las proyecciones |
| GET | `/upcoming` | Proyecciones próximas |
| GET | `/{id}` | Obtener proyección por ID |
| GET | `/movie/{movieId}` | Proyecciones de una película |
| POST | `/` | Crear proyección |
| PUT | `/{id}` | Actualizar proyección |
| DELETE | `/{id}` | Eliminar proyección |
| POST | `/{id}/seats/{seatId}/reserve` | Reservar una butaca |
| POST | `/{id}/seats/{seatId}/release` | Liberar una butaca |
| GET | `/{id}/purchases` | Compras de una proyección |

### Purchases — `/api/purchases`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar todas las compras |
| GET | `/{id}` | Obtener compra por ID |
| GET | `/user/{userId}` | Compras de un usuario |
| GET | `/screening/{screeningId}` | Compras de una proyección |
| POST | `/` | Crear compra |
| POST | `/{id}/confirm` | Confirmar pago (envía email) |
| POST | `/{id}/cancel` | Cancelar compra |

### Tickets — `/api/tickets`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar todas las entradas |
| GET | `/{id}` | Obtener entrada por ID |
| POST | `/` | Crear entrada |
| PUT | `/{id}` | Actualizar entrada |
| DELETE | `/{id}` | Eliminar entrada |

### Theaters — `/api/theaters`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar salas |
| GET | `/{id}` | Obtener sala por ID |
| GET | `/{id}/seats` | Butacas de una sala |
| POST | `/` | Crear sala |
| PUT | `/{id}` | Actualizar sala |
| DELETE | `/{id}` | Eliminar sala |

### Seats — `/api/seats`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar butacas |
| GET | `/{id}` | Obtener butaca por ID |
| POST | `/` | Crear butaca |
| PUT | `/{id}` | Actualizar butaca |
| DELETE | `/{id}` | Eliminar butaca |

### Employees — `/api/employees`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar empleados |
| GET | `/{id}` | Obtener empleado por ID |
| POST | `/` | Crear empleado |
| PUT | `/{id}` | Actualizar empleado |
| DELETE | `/{id}` | Eliminar empleado |

### Shifts — `/api/shifts`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar turnos |
| GET | `/{id}` | Obtener turno por ID |
| GET | `/date/{date}` | Turnos en una fecha |
| GET | `/range` | Turnos en un rango de fechas |
| POST | `/` | Crear turno |
| PUT | `/{id}` | Actualizar turno |
| DELETE | `/{id}` | Eliminar turno |

### Merchandise — `/api/merchandise`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar productos |
| GET | `/{id}` | Obtener producto por ID |
| POST | `/` | Crear producto |
| PUT | `/{id}` | Actualizar producto |
| DELETE | `/{id}` | Eliminar producto |

### Merchandise Sales — `/api/merchandise-sales`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar ventas |
| GET | `/{id}` | Obtener venta por ID |
| POST | `/` | Registrar venta |
| DELETE | `/{id}` | Eliminar venta |

### Incidents — `/api/incidents`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar incidencias |
| GET | `/{id}` | Obtener incidencia por ID |
| POST | `/` | Crear incidencia |
| PUT | `/{id}` | Actualizar incidencia |
| DELETE | `/{id}` | Eliminar incidencia |

### Dashboard — `/api/dashboard`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Métricas globales del cine |
| GET | `/yearly?year={year}` | Estadísticas anuales |

### Reports — `/api/reports`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/sales-week` | Ventas de la semana |
| GET | `/occupancy` | Tasa de ocupación |

### Formato de respuesta

Todas las respuestas siguen el wrapper `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Descripción del resultado",
  "data": {
    
    
  }
}
```

En caso de error, se devuelve `ApiError`:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found with id: 5"
}
```

---

## Lógica de negocio

### Precios de entradas

Los precios son fijos por tipo de entrada, con un multiplicador para butacas VIP.

| Tipo | Precio base |
|------|-------------|
| CHILD | 6 € |
| STUDENT | 6 € |
| ADULT | 9 € |
| SENIOR | 2 € |

- **Butaca VIP:** precio × 1.5 (solo aplica a entradas ADULT)
- Implementado en `PriceCalculator.java`

### Sistema de descuento por fidelidad

- Los clientes con **10 o más visitas anuales** reciben un **10% de descuento** en su próxima compra.
- El contador `annualVisits` se incrementa en cada compra confirmada.
- El campo `discountActive` se activa automáticamente al llegar al umbral.

### Validación de edad

- Las películas tienen un campo `ageRating` (clasificación por edad).
- Al comprar entradas, se valida que la edad del usuario sea compatible.
- Un menor sin acompañante adulto en la misma compra lanza `MinorWithoutAdultException`.

### Flujo de compra

1. `POST /api/purchases` — Se crea la compra en estado pendiente y se reservan las butacas.
2. `POST /api/purchases/{id}/confirm` — Se confirma el pago, se aplican descuentos, se incrementan las visitas y se envía el email de confirmación.
3. `POST /api/purchases/{id}/cancel` — Se cancela la compra y se liberan las butacas.

### Gestión de butacas en proyecciones

- Cada proyección tiene su propio mapa de `ScreeningSeat` (estado por butaca).
- Al reservar: `occupied = true`, se incrementa `occupiedSeats`.
- Si `occupiedSeats == capacity`, la proyección se marca como `full = true`.
- Al liberar: proceso inverso.

---

## Integración con servicios externos

### Cloudinary (imágenes)

- **Uso:** subida de fotos de perfil de usuarios y carteles de películas.
- **Servicio:** `CloudinaryService.uploadImage()`
- **Configuración:** `cloudinary.cloud-name`, `cloudinary.api-key`, `cloudinary.api-secret` en `application.properties`.

### Gmail SMTP (email)

- **Uso:** envío de emails de confirmación de compra.
- **Servicio:** `EmailService.sendPurchaseConfirmation(Purchase)`
- **Cuenta:** equipo2lumencinema@gmail.com
- **Configuración:** SMTP Gmail puerto 587 con TLS.
- El email contiene: número de compra, película, sala, fecha, detalle de entradas, descuento aplicado y total.
- Si el envío falla, la compra **no se revierte** (el campo `emailSent` queda en `false`).

---

## Manejo de errores

El `GlobalExceptionHandler` captura todas las excepciones y devuelve respuestas HTTP estandarizadas.

| Excepción | HTTP Status | Descripción |
|-----------|-------------|-------------|
| `ResourceNotFoundException` | 404 | Recurso no encontrado |
| `BusinessRuleException` | 400 | Violación de regla de negocio |
| `ConflictException` | 409 | Conflicto de datos |
| `SeatAlreadyTakenException` | 409 | La butaca ya está ocupada |
| `ScreeningFullException` | 409 | La sesión está completa |
| `ScreeningAlreadyPassedException` | 400 | La sesión ya ha pasado |
| `PurchaseAlreadyCancelledException` | 409 | La compra ya fue cancelada |
| `InvalidPurchaseStatusException` | 422 | Estado de compra no válido para la operación |
| `AgeRestrictionException` | 403 | Restricción de edad |
| `MinorWithoutAdultException` | 422 | Menor sin acompañante adulto |
| `UnauthorizedException` | 401 | No autenticado |
| `InvalidTokenException` | 401 | Token JWT inválido |
| `ForbiddenException` | 403 | Sin permisos |
| `MethodArgumentNotValidException` | 400 | Errores de validación de campos |

---

## DTOs

### Request DTOs

| DTO | Uso |
|-----|-----|
| `LoginRequestDTO` | Login (email, password) |
| `RegisterRequestDTO` | Registro de usuario |
| `MovieRequestDTO` | Crear película |
| `UpdateMovieRequestDTO` | Actualizar película |
| `ScreeningRequestDTO` | Crear proyección |
| `UpdateScreeningRequestDTO` | Actualizar proyección |
| `PurchaseRequestDTO` | Crear compra |
| `PayPurchaseRequestDTO` | Confirmar pago |
| `UserRequestDTO` | Crear usuario |
| `UpdateUserRequestDTO` | Actualizar usuario |
| `ClientUpdateRequestDTO` | Actualización de perfil de cliente |
| `EmployeeRequestDTO` | Crear empleado |
| `UpdateEmployeeRequestDTO` | Actualizar empleado |
| `ShiftRequestDTO` | Crear turno |
| `UpdateShiftRequestDTO` | Actualizar turno |
| `TheaterRequestDTO` | Crear sala |
| `UpdateTheaterRequestDTO` | Actualizar sala |
| `SeatRequestDTO` | Crear butaca |
| `UpdateSeatRequestDTO` | Actualizar butaca |
| `MerchandiseRequestDTO` | Crear producto |
| `MerchandiseSaleRequestDTO` | Registrar venta |
| `IncidentRequestDTO` | Crear incidencia |
| `TicketRequestDTO` | Crear entrada |
| `TicketOfficeRequestDTO` | Venta en taquilla |

### Response DTOs

| DTO | Uso |
|-----|-----|
| `ApiResponse<T>` | Wrapper genérico de todas las respuestas |
| `ApiError` | Respuesta de error estandarizada |
| `LoginResponseDTO` | Token JWT tras el login |
| `MovieResponseDTO` | Datos de película |
| `ScreeningResponseDTO` | Datos de proyección |
| `TicketResponseDTO` | Datos de entrada |
| `PurchaseResponseDTO` | Datos de compra |
| `UserResponseDTO` | Datos de usuario |
| `EmployeeResponseDTO` | Datos de empleado |
| `ShiftResponseDTO` | Datos de turno |
| `TheaterResponseDTO` | Datos de sala |
| `SeatResponseDTO` | Datos de butaca |
| `ScreeningSeatResponseDTO` | Estado de butaca en proyección |
| `MerchandiseResponseDTO` | Datos de producto |
| `MerchandiseSaleResponseDTO` | Datos de venta |
| `IncidentResponseDTO` | Datos de incidencia |
| `DashboardResponseDTO` | Métricas globales del dashboard |
| `YearlyDashboardResponseDTO` | Estadísticas anuales |
| `KpiResponseDTO` | KPIs del negocio |
| `OccupancyResponseDTO` | Datos de ocupación |
| `SalesWeekResponseDTO` | Ventas semanales |

---

## Tests

El proyecto cuenta con **264 tests unitarios** organizados por feature, todos pasando.

| Módulo | Clases de test |
|--------|---------------|
| Users | `UserControllerTest`, `UserServiceTest` |
| Movies | `MovieControllerTest`, `MovieServiceTest` |
| Screenings | `ScreeningControllerTest`, `ScreeningServiceTest` |
| Theaters | `TheaterControllerTest`, `TheaterServiceTest` |
| Purchases | `PurchaseControllerTest`, `PurchaseServiceTest` |
| Tickets | `TicketControllerTest` |
| Employees | `EmployeeControllerTest`, `EmployeeServiceTest` |
| Dashboard | `DashboardControllerTest`, `DashboardServiceTest` |
| Reports | `ReportControllerTest` |
| Auth | `AuthControllerTest`, `AuthServiceTest` |
| Exceptions | `ExceptionsTest`, `GlobalExceptionHandlerTest` |
| Mappers | `MovieMapperTest`, `PurchaseMapperTest` |
| Utilities | `PriceCalculatorTest` |
| Smoke | `DemoApplicationTests` |

Para ejecutar los tests:
```bash
./mvnw test
```

Los reportes individuales se generan en `target/surefire-reports/`.

---

## Configuración

### Requisitos previos

- Java 25
- MySQL corriendo en `localhost:3306`
- Base de datos `cinema` creada (`spring.jpa.hibernate.ddl-auto=none`, las tablas deben existir previamente)

### `application.properties`

```properties
# Servidor
server.port=8080

# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/cinema
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# JWT
jwt.secret=cinema-app-jwt-secret-key-2024-super-long-string-minimum-256-bits
jwt.expiration=86400000

# Cloudinary
cloudinary.cloud-name=<cloud-name>
cloudinary.api-key=<api-key>
cloudinary.api-secret=<api-secret>

# Email (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=equipo2lumencinema@gmail.com
spring.mail.password=<app-password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### CORS

Configurado para permitir peticiones desde el frontend Vue.js en `http://localhost:5173`.

Métodos permitidos: GET, POST, PUT, DELETE, OPTIONS, PATCH.

### Swagger UI

Disponible en: `http://localhost:8080/swagger-ui.html`

Incluye esquema de autenticación Bearer JWT para probar endpoints protegidos directamente desde la UI.

### Arranque

```bash
./mvnw spring-boot:run
```
