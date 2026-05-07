# API Reference — Lumen Cinema Backend

**Base URL:** `http://localhost:8080`  
**Stack:** Spring Boot 4.0.6 · Java 25 · MySQL 8 · Hibernate 7

---

## Índice

1. [Formato de respuesta](#1-formato-de-respuesta)
2. [Autenticación](#2-autenticación)
3. [Usuarios](#3-usuarios---apiusers)
4. [Películas](#4-películas---apimovies)
5. [Salas](#5-salas---apitheaters)
6. [Asientos](#6-asientos---apiseats)
7. [Proyecciones](#7-proyecciones---apiscreenings)
8. [Compras](#8-compras---apipurchases)
9. [Tickets](#9-tickets---apitickets)
10. [Merchandise](#10-merchandise---apimerchandise)
11. [Ventas de Merchandise](#11-ventas-de-merchandise---apimerchandisesales)
12. [Empleados](#12-empleados---apiemployees)
13. [Turnos](#13-turnos---apishifts)
14. [Incidencias](#14-incidencias---apiincidents)
15. [Dashboard](#15-dashboard---apidashboard)
16. [Reportes](#16-reportes---apireports)
17. [Errores](#17-errores)
18. [Enums de referencia](#18-enums-de-referencia)

---

## 1. Formato de respuesta

La mayoría de endpoints devuelven el wrapper estándar:

```json
{
  "success": true,
  "message": "...",
  "data": { ... },
  "errors": null
}
```

**Excepciones** que devuelven la entidad directamente (sin wrapper):
- `POST/PUT/DELETE /api/movies` → devuelve `MovieResponseDTO` plano
- `POST /api/auth/login` → devuelve `LoginResponseDTO` plano

En caso de error, `success` es `false` y `data` es `null`:
```json
{
  "success": false,
  "message": "Descripción del error",
  "data": null,
  "errors": ["detalle1", "detalle2"]
}
```

---

## 2. Autenticación

### POST `/api/auth/login`

Login de usuario. **Respuesta plana (sin wrapper ApiResponse).**

**Request body:**
```json
{
  "email": "admin@lumen.com",
  "password": "lumen2024"
}
```

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "name": "Admin",
    "email": "admin@lumen.com",
    "role": "ADMIN",
    "imageUrl": null,
    "status": null
  }
}
```

> El token JWT debe enviarse en el header `Authorization: Bearer <token>` en las llamadas protegidas.

**Usuarios precargados en BD:**

| Email | Password | Rol |
|---|---|---|
| `admin@lumen.com` | `lumen2024` | `ADMIN` |
| `cliente@lumen.com` | `lumen2024` | `CLIENTE` |

---

## 3. Usuarios — `/api/users`

### GET `/api/users`
Lista todos los usuarios.

**Response `200`:**
```json
{
  "success": true,
  "message": "Usuarios obtenidos correctamente",
  "data": [
    {
      "id": 1,
      "name": "Admin",
      "lastName": "Lumen",
      "email": "admin@lumen.com",
      "birthDate": "1990-01-01",
      "userType": "ADULT",
      "student": false,
      "visitsCurrentYear": 0,
      "discountActive": false,
      "role": "ADMIN",
      "imageUrl": null,
      "createdAt": "2025-01-01T10:00:00",
      "updatedAt": null
    }
  ]
}
```

### GET `/api/users/{id}`
Obtiene un usuario por ID. `404` si no existe.

### POST `/api/users`
Crea un usuario (registro).

**Request body:**
```json
{
  "name": "Ana",
  "lastName": "García",
  "email": "ana@ejemplo.com",
  "password": "password123",
  "birthDate": "1995-06-15",
  "userType": "ADULT"
}
```
Response `201 Created`.

### PUT `/api/users/{id}`
Actualiza un usuario. Todos los campos son opcionales (solo se actualizan los enviados).

**Request body (campos opcionales):**
```json
{
  "name": "Ana María",
  "lastName": "García",
  "email": "ana@nuevo.com",
  "birthDate": "1995-06-15",
  "userType": "ADULT",
  "role": "ADMIN"
}
```

### DELETE `/api/users/{id}`
Elimina un usuario. `404` si no existe.

### POST `/api/users/{id}/image`
Sube foto de perfil. `multipart/form-data` con campo `file`.

---

## 4. Películas — `/api/movies`

> **Nota:** estos endpoints devuelven la entidad directamente (sin wrapper `ApiResponse`).

### GET `/api/movies`
Lista todas las películas.

**Response `200`** — array de `MovieResponseDTO`:
```json
[
  {
    "id": 1,
    "title": "Inception",
    "description": "Un ladrón de sueños...",
    "durationMin": 148,
    "genre": "Sci-Fi",
    "ageRating": "12",
    "imageUrl": "https://res.cloudinary.com/.../inception.jpg",
    "active": true,
    "language": "English",
    "schedule": null,
    "createdAt": "2025-01-01T10:00:00"
  }
]
```

**Valores posibles de `ageRating`:** `"ALL"`, `"7"`, `"12"`, `"16"`, `"18"`

### GET `/api/movies/active`
Lista solo películas con `active = true`.

### GET `/api/movies/{id}`
Obtiene película por ID.

### POST `/api/movies` — JSON puro
```json
{
  "title": "Dune",
  "description": "Épica espacial",
  "durationMin": 155,
  "genre": "Sci-Fi",
  "ageRating": "TWELVE",
  "language": "English",
  "active": true
}
```
**`ageRating` acepta el nombre del enum:** `ALL`, `SEVEN`, `TWELVE`, `SIXTEEN`, `EIGHTEEN`

### POST `/api/movies` — Multipart (con imagen)
`Content-Type: multipart/form-data`

| Campo | Tipo | Descripción |
|---|---|---|
| `movie` | JSON blob | `MovieRequestDTO` serializado |
| `image` | file | Imagen del póster (opcional) |

Response `200` con `imageUrl` de Cloudinary.

### PUT `/api/movies/{id}`
Actualiza película. Mismos campos que POST.

### DELETE `/api/movies/{id}`
Response `204 No Content`.

---

## 5. Salas — `/api/theaters`

### GET `/api/theaters`
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Sala IMAX",
      "capacity": 100,
      "totalSeats": 100,
      "seats": []
    }
  ]
}
```

### GET `/api/theaters/{id}`
Obtiene sala por ID.

### GET `/api/theaters/{id}/seats`
Lista los asientos de una sala.

### POST `/api/theaters`
Crea una sala. **Genera los asientos automáticamente** (filas A, B, C... — 10 asientos por fila).

```json
{
  "name": "Sala 3",
  "capacity": 50
}
```

### PUT `/api/theaters/{id}`
```json
{
  "name": "Sala Premium",
  "capacity": 80
}
```

### DELETE `/api/theaters/{id}`

---

## 6. Asientos — `/api/seats`

### GET `/api/seats`
### GET `/api/seats/{id}`

**`SeatResponseDTO`:**
```json
{
  "id": 11,
  "theaterId": 1,
  "row": "A",
  "number": 1,
  "type": "STANDARD"
}
```

**Valores de `type`:** `STANDARD`, `VIP`, `ACCESIBILIDAD`

### POST `/api/seats`
```json
{
  "theaterId": 1,
  "row": "Z",
  "number": 1,
  "type": "VIP"
}
```

### PUT `/api/seats/{id}`
Todos los campos opcionales.

### DELETE `/api/seats/{id}`

---

## 7. Proyecciones — `/api/screenings`

### GET `/api/screenings`
### GET `/api/screenings/upcoming`
Solo proyecciones futuras.

### GET `/api/screenings/{id}`

**`ScreeningResponseDTO`:**
```json
{
  "id": 1,
  "movie": { "id": 1, "title": "Inception", ... },
  "theater": { "id": 1, "name": "Sala IMAX", "capacity": 100 },
  "dateTime": "2026-06-15T20:00:00",
  "endDatetime": "2026-06-15T22:28:00",
  "basePrice": 9.50,
  "availableSeats": 87,
  "full": false
}
```

### GET `/api/screenings/movie/{movieId}`
Proyecciones de una película.

### GET `/api/screenings/{id}/purchases`
Compras de una proyección.

### POST `/api/screenings`
```json
{
  "movieId": 1,
  "theaterId": 1,
  "dateTime": "2026-06-15T20:00:00",
  "basePrice": 9.50
}
```
La `dateTime` debe ser futura (`400` si es pasada).

### PUT `/api/screenings/{id}`
```json
{ "basePrice": 11.00 }
```

### DELETE `/api/screenings/{id}`

### POST `/api/screenings/{id}/seats/{seatId}/reserve`
Marca un asiento como ocupado y decrementa `availableSeats`.

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 5,
    "screeningId": 1,
    "seatId": 11,
    "occupied": true
  }
}
```

### POST `/api/screenings/{id}/seats/{seatId}/release`
Libera un asiento.

---

## 8. Compras — `/api/purchases`

### GET `/api/purchases`
Lista todas las compras.

### GET `/api/purchases/{id}`

**`PurchaseResponseDTO`:**
```json
{
  "id": 42,
  "userId": 3,
  "userName": "Ana García",
  "screeningId": 1,
  "movieTitle": "Inception",
  "theaterName": "Sala IMAX",
  "dateTime": "2026-06-15T20:00:00",
  "tickets": [
    {
      "id": 101,
      "purchaseId": 42,
      "seatId": 11,
      "row": "A",
      "number": 1,
      "seatType": "STANDARD",
      "ticketType": "ADULT",
      "unitPrice": 9.50
    }
  ],
  "totalAmount": 9.50,
  "discountApplied": false,
  "discountAmount": 0.00,
  "status": "PENDING",
  "createdAt": "2026-05-07T10:00:00"
}
```

**Valores de `status`:** `PENDING`, `PAID`, `CANCELLED`

### GET `/api/purchases/user/{userId}`
Historial de compras de un usuario.

### GET `/api/purchases/screening/{screeningId}`
Compras de una proyección.

### POST `/api/purchases`
Crea una compra en estado `PENDING`.

```json
{
  "userId": 3,
  "screeningId": 1,
  "tickets": [
    { "seatId": 11, "ticketType": "ADULT" },
    { "seatId": 12, "ticketType": "CHILD" }
  ]
}
```

**Tipos de ticket y precio resultante:**

| `ticketType` | Precio |
|---|---|
| `ADULT` | `basePrice` de la proyección (×1.5 si asiento VIP) |
| `CHILD` | 6,00 € (fijo) |
| `STUDENT` | 6,00 € (fijo) |
| `SENIOR` | 2,00 € (fijo) |

**Descuento de fidelidad:** si el usuario tiene más de 10 visitas en el año, se aplica 10% sobre el subtotal de entradas ADULT.

### POST `/api/purchases/{id}/confirm`
Cambia estado a `PAID`. Solo aplica sobre compras `PENDING`.

### POST `/api/purchases/{id}/cancel`
Cambia estado a `CANCELLED`.

---

## 9. Tickets — `/api/tickets`

Los tickets se crean automáticamente al crear una compra.

### GET `/api/tickets`
Lista todos los tickets. Admite filtros opcionales:

| Query param | Descripción |
|---|---|
| `?purchaseId=42` | Tickets de una compra |
| `?screeningId=1` | Tickets de una proyección |

### GET `/api/tickets/{id}`

---

## 10. Merchandise — `/api/merchandise`

### GET `/api/merchandise`
### GET `/api/merchandise/{id}`

**`MerchandiseResponseDTO`:**
```json
{
  "id": 1,
  "name": "Taza Lumen",
  "description": "Taza de cerámica",
  "category": "Hogar",
  "price": 12.99,
  "stock": 50,
  "imageUrl": null,
  "active": true,
  "createdAt": "2025-01-01T10:00:00"
}
```

### POST `/api/merchandise`
```json
{
  "name": "Camiseta",
  "description": "Camiseta algodón",
  "category": "Ropa",
  "price": 19.99,
  "stock": 100
}
```

### PUT `/api/merchandise/{id}`
### DELETE `/api/merchandise/{id}`

---

## 11. Ventas de Merchandise — `/api/merchandisesales`

### GET `/api/merchandisesales`
### GET `/api/merchandisesales/{id}`

**`MerchandiseSaleResponseDTO`:**
```json
{
  "id": 1,
  "userId": 3,
  "merchandiseId": 1,
  "merchandiseName": "Taza Lumen",
  "quantity": 2,
  "total": 25.98,
  "saleDate": "2026-05-07T15:30:00"
}
```

### POST `/api/merchandisesales`
```json
{
  "userId": 3,
  "merchandiseId": 1,
  "quantity": 2
}
```

### PUT `/api/merchandisesales/{id}`
### DELETE `/api/merchandisesales/{id}`

---

## 12. Empleados — `/api/employees`

### GET `/api/employees`
### GET `/api/employees/{id}`

**`EmployeeResponseDTO`:**
```json
{
  "id": 1,
  "name": "Carlos López",
  "email": "carlos@lumen.com",
  "role": "CASHIER",
  "createdAt": "2025-01-01T10:00:00"
}
```

**Valores de `role` (EmployeeRole):** `MANAGER`, `CASHIER`, `PROJECTIONIST`, `SECURITY`, `CLEANING`

### POST `/api/employees`
```json
{
  "name": "Carlos López",
  "email": "carlos@lumen.com",
  "role": "CASHIER"
}
```

### PUT `/api/employees/{id}`
```json
{
  "name": "Carlos L.",
  "role": "MANAGER"
}
```

### DELETE `/api/employees/{id}`

---

## 13. Turnos — `/api/shifts`

### GET `/api/shifts`
### GET `/api/shifts/{id}`

**`ShiftResponseDTO`:**
```json
{
  "id": 1,
  "employeeId": 1,
  "employeeName": "Carlos López",
  "employeeEmail": "carlos@lumen.com",
  "employeeRole": "CASHIER",
  "shiftDate": "2026-05-10",
  "startTime": "09:00:00",
  "endTime": "17:00:00",
  "notes": "Apertura",
  "status": "SCHEDULED",
  "createdAt": "2026-05-07T10:00:00"
}
```

### GET `/api/shifts/date/{date}`
Turnos de un día concreto. Formato: `YYYY-MM-DD`

**Ejemplo:** `GET /api/shifts/date/2026-05-10`

### GET `/api/shifts/range?from=YYYY-MM-DD&to=YYYY-MM-DD`
Turnos en un rango de fechas.

**Ejemplo:** `GET /api/shifts/range?from=2026-05-01&to=2026-05-31`

### POST `/api/shifts`
```json
{
  "employeeId": 1,
  "shiftDate": "2026-05-10",
  "startTime": "09:00:00",
  "endTime": "17:00:00",
  "notes": "Apertura",
  "status": "SCHEDULED"
}
```

### PUT `/api/shifts/{id}`
### DELETE `/api/shifts/{id}`

---

## 14. Incidencias — `/api/incidents`

### GET `/api/incidents`
### GET `/api/incidents/{id}`

**`IncidentResponseDTO`:**
```json
{
  "id": 1,
  "title": "Proyector averiado",
  "description": "El proyector de la Sala 2 no enciende",
  "severity": "HIGH",
  "resolved": false,
  "createdAt": "2026-05-07T11:00:00",
  "updatedAt": null
}
```

### POST `/api/incidents`
```json
{
  "title": "Proyector averiado",
  "description": "El proyector de la Sala 2 no enciende",
  "severity": "HIGH"
}
```

### PUT `/api/incidents/{id}`
### DELETE `/api/incidents/{id}`

---

## 15. Dashboard — `/api/dashboard`

### GET `/api/dashboard`
Resumen general del negocio (para la pantalla de inicio de admin).

**Response:**
```json
{
  "success": true,
  "data": {
    "totalRevenue": 15420.50,
    "weeklyRevenue": 1230.00,
    "totalPurchases": 320,
    "paidPurchases": 285,
    "activeScreenings": 12,
    "confirmedRoomBookings": 8,
    "totalUsers": 95,
    "activeMovies": 6,
    "unresolvedIncidents": 2
  }
}
```

---

## 16. Reportes — `/api/reports`

### GET `/api/reports/sales-week`
Ventas de los últimos 7 días agrupadas por día.

**Response:**
```json
{
  "success": true,
  "data": [
    { "date": "2026-05-01", "totalPurchases": 12, "revenue": 114.00 },
    { "date": "2026-05-02", "totalPurchases": 8,  "revenue": 76.00 },
    ...
  ]
}
```

### GET `/api/reports/occupancy`
Porcentaje de ocupación por proyección.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "screeningId": 1,
      "movieTitle": "Inception",
      "theaterName": "Sala IMAX",
      "dateTime": "2026-06-15T20:00:00",
      "totalSeats": 100,
      "occupiedSeats": 87,
      "occupancyPercentage": 87.0
    }
  ]
}
```

---

## 17. Errores

| HTTP | Caso |
|---|---|
| `400` | Validación fallida (campo requerido, formato incorrecto) o fecha pasada en proyección |
| `401` | Credenciales inválidas |
| `403` | Restricción de edad (película +18 con usuario menor) |
| `404` | Recurso no encontrado |
| `409` | Conflicto: nombre/email duplicado, asiento ya ocupado, proyección llena, compra ya cancelada |
| `422` | Estado de compra inválido (confirmar una compra que no está PENDING) · Menor sin adulto en la compra |

**Formato de error:**
```json
{
  "success": false,
  "message": "Ya existe una sala con el nombre: Sala IMAX",
  "data": null,
  "errors": null
}
```

---

## 18. Enums de referencia

| Enum | Valores |
|---|---|
| `Role` (usuario app) | `ADMIN`, `CLIENTE` |
| `UserType` | `ADULT`, `CHILD`, `STUDENT`, `SENIOR` |
| `TicketType` | `ADULT`, `CHILD`, `STUDENT`, `SENIOR` |
| `SeatType` | `STANDARD`, `VIP`, `ACCESIBILIDAD` |
| `PurchaseStatus` | `PENDING`, `PAID`, `CANCELLED` |
| `AgeRating` (JSON) | `"ALL"`, `"7"`, `"12"`, `"16"`, `"18"` |
| `AgeRating` (request) | `ALL`, `SEVEN`, `TWELVE`, `SIXTEEN`, `EIGHTEEN` |
| `EmployeeRole` | `MANAGER`, `CASHIER`, `PROJECTIONIST`, `SECURITY`, `CLEANING` |

---

*Generado: 2026-05-07 · Rama: `feat/springTwo` · Spring Boot 4.0.6*