# 🎬 Cine Backend - Estado del Proyecto

API REST para la gestión integral de un multicine: cartelera, proyecciones, reserva de asientos, compra de entradas, merchandising y dashboard de dirección.

---

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | — |
| Spring Validation | — |
| Spring Security Crypto | — |
| Spring Mail | — |
| MySQL | 8+ |
| Cloudinary | 1.39.0 |
| Lombok | — |
| Maven | — |

---

## Arquitectura

```
src/main/java/com/cine/demo/
├── config/          # Configuración (Cloudinary, BCrypt)
├── controller/      # Controladores REST
├── dto/
│   ├── request/     # DTOs de entrada
│   └── response/    # DTOs de salida
├── exception/       # Excepciones personalizadas + GlobalExceptionHandler
├── mapper/          # Conversión entidad ↔ DTO
├── model/
│   ├── enums/       # AgeRating, SeatType, TicketType, UserType, Role…
│   └── *.java       # Entidades JPA
├── repository/      # Interfaces Spring Data JPA
└── service/
    ├── impl/        # Implementaciones de los servicios
    └── *.java       # Interfaces de servicio
```

---

## Puesta en marcha

### Requisitos previos

- Java 25
- Maven 3.9+
- MySQL 8+
- Cuenta en [Cloudinary](https://cloudinary.com) (gratuita)
- Cuenta de Gmail con contraseña de aplicación habilitada

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd Backend
```

### 2. Crear la base de datos

```sql
CREATE DATABASE cine_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar `application.properties`

Edita `src/main/resources/application.properties` y descomenta y rellena la sección de base de datos:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/cine_db
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Cloudinary
cloudinary.cloud-name=TU_CLOUD_NAME
cloudinary.api-key=TU_API_KEY
cloudinary.api-secret=TU_API_SECRET

# Email (Gmail)
spring.mail.username=TU_EMAIL@gmail.com
spring.mail.password=TU_APP_PASSWORD
```

> Recuerda eliminar también la línea `spring.autoconfigure.exclude=...` que deshabilita el datasource.

### 4. Ejecutar

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

---

## Endpoints

### Usuarios `/api/users`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/users` | Listar todos los usuarios |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| POST | `/api/users` | Crear usuario |
| PUT | `/api/users/{id}` | Actualizar usuario |
| DELETE | `/api/users/{id}` | Eliminar usuario |
| POST | `/api/users/{id}/image` | Subir foto de perfil (Cloudinary) |

### Películas `/api/movies`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/movies` | Listar toda la cartelera |
| GET | `/api/movies/{id}` | Obtener película por ID |
| POST | `/api/movies` | Crear película |
| PUT | `/api/movies/{id}` | Actualizar película |
| DELETE | `/api/movies/{id}` | Eliminar película |
| POST | `/api/movies/{id}/poster` | Subir póster (Cloudinary) |

### Proyecciones `/api/screenings`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/screenings` | Listar todas las proyecciones |
| GET | `/api/screenings/upcoming` | Próximas proyecciones |
| GET | `/api/screenings/{id}` | Obtener proyección por ID |
| GET | `/api/screenings/movie/{movieId}` | Proyecciones de una película |
| POST | `/api/screenings` | Crear proyección |
| PUT | `/api/screenings/{id}` | Actualizar proyección |
| DELETE | `/api/screenings/{id}` | Eliminar proyección |
| POST | `/api/screenings/{id}/seats/{seatId}/reserve` | Reservar asiento |
| POST | `/api/screenings/{id}/seats/{seatId}/release` | Liberar reserva |
| GET | `/api/screenings/{id}/purchases` | Compras de una proyección |

### Salas `/api/theaters`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/theaters` | Listar salas |
| GET | `/api/theaters/{id}` | Obtener sala por ID |
| POST | `/api/theaters` | Crear sala |
| PUT | `/api/theaters/{id}` | Actualizar sala |
| DELETE | `/api/theaters/{id}` | Eliminar sala |
| GET | `/api/theaters/{id}/seats` | Asientos de una sala |

### Asientos `/api/seats`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/seats` | Listar asientos |
| GET | `/api/seats/{id}` | Obtener asiento por ID |
| POST | `/api/seats` | Crear asiento |
| PUT | `/api/seats/{id}` | Actualizar asiento |
| DELETE | `/api/seats/{id}` | Eliminar asiento |

### Compras `/api/purchases`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/purchases` | Realizar compra |
| POST | `/api/purchases/{id}/confirm` | Confirmar compra |
| POST | `/api/purchases/{id}/cancel` | Cancelar compra |
| GET | `/api/purchases/{id}` | Obtener compra por ID |
| GET | `/api/purchases/user/{userId}` | Historial de compras de un usuario |
| GET | `/api/purchases/screening/{screeningId}` | Compras de una proyección |

### Merchandising `/api/merchandises`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/merchandises` | Listar productos |
| GET | `/api/merchandises/{id}` | Obtener producto por ID |
| POST | `/api/merchandises` | Crear producto |
| PUT | `/api/merchandises/{id}` | Actualizar producto |
| DELETE | `/api/merchandises/{id}` | Eliminar producto |

### Tickets `/api/tickets`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/tickets` | Listar tickets |
| GET | `/api/tickets/{id}` | Obtener ticket por ID |
| POST | `/api/tickets` | Crear ticket |
| PUT | `/api/tickets/{id}` | Actualizar ticket |
| DELETE | `/api/tickets/{id}` | Eliminar ticket |

### Dashboard `/api/dashboard`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/dashboard` | Resumen estadístico para dirección |

---

## Enumerados del dominio

| Enum | Valores |
|---|---|
| `AgeRating` | `ALL`, `SEVEN`, `TWELVE`, `SIXTEEN`, `EIGHTEEN` |
| `SeatType` | `STANDARD`, `VIP` |
| `TicketType` | `CHILD`, `ADULT`, `STUDENT`, `SENIOR` |
| `UserType` | `ADULT`, `STUDENT`, `SENIOR` |
| `PurchaseStatus` | `PENDING`, `CONFIRMED`, `CANCELLED` |
| `MerchandiseCategory` | categorías de productos |
| `Role` | roles de usuario |

---

## Funcionalidades destacadas

- **Gestión de reservas**: los asientos se reservan y liberan por proyección con control de disponibilidad.
- **Validación de edad**: restricción automática según la clasificación de la película (`AgeRating`).
- **Menores sin adulto**: excepción dedicada si un menor intenta comprar sin acompañante adulto.
- **Subida de imágenes**: integración con Cloudinary para pósters de películas y fotos de perfil.
- **Envío de emails**: confirmaciones de compra vía Gmail SMTP.
- **Cifrado de contraseñas**: BCrypt mediante `spring-security-crypto`.
- **Respuesta unificada**: todos los endpoints devuelven `ApiResponse<T>` con `success`, `message` y `data`.
- **Manejo global de errores**: `GlobalExceptionHandler` centraliza todas las excepciones de negocio.

---

## Ramas

| Rama | Propósito |
|---|---|
| `main` | Producción — código estable |
| `develop` | Integración — rama base para PRs |
| `feature/*` | Desarrollo de cada funcionalidad |

---

## Autoras

- Ana Morandeira
- Maria Regueiro
