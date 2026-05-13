# API Reference — Lumen Cinema Backend

**Base URL:** `http://localhost:8080`  
**Stack:** Spring Boot · Java 21 · MySQL 8 · Hibernate  
**Actualizado:** 2026-05-12 · Rama: `feat/springTwo`

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
9. [Pagos — Stripe](#9-pagos--stripe---apipayments)
10. [Tickets](#10-tickets---apitickets)
11. [Merchandise](#11-merchandise---apimerchandise)
12. [Ventas de Merchandise](#12-ventas-de-merchandise---apimerchandisesales)
13. [Empleados](#13-empleados---apiemployees)
14. [Turnos](#14-turnos---apishifts)
15. [Incidencias](#15-incidencias---apiincidents)
16. [Dashboard](#16-dashboard---apidashboard)
17. [Reportes](#17-reportes---apireports)
18. [Errores](#18-errores)
19. [Enums de referencia](#19-enums-de-referencia)
20. [Cambios recientes — fixes pendientes en front](#20-cambios-recientes--fixes-pendientes-en-front)

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
    "imageUrl": null
  }
}
```

> El token JWT debe enviarse en el header `Authorization: Bearer <token>` en las llamadas protegidas.

**Usuarios precargados en BD (seed_full_test.sql):**

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
      "visitsCurrentYear": 0,
      "discountActive": false,
      "role": "ADMIN",
      "imageUrl": null,
      "createdAt": "2026-01-01T10:00:00",
      "updatedAt": null
    }
  ]
}
```

### GET `/api/users/{id}`
Obtiene un usuario por ID. `404` si no existe.

### GET `/api/users/search?q={query}`
Busca usuarios por nombre o email.

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
Actualiza un usuario. Todos los campos son opcionales.

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
    "title": "Dune: Parte Dos",
    "description": "Paul Atreides se une a los Fremen.",
    "durationMin": 166,
    "genre": "Ciencia Ficcion",
    "ageRating": "12",
    "imageUrl": null,
    "active": true,
    "language": "VOSE",
    "schedule": "Tarde",
    "createdAt": "2026-05-12T10:00:00"
  }
]
```

**Valores posibles de `ageRating` en response:** `"ALL"`, `"7"`, `"12"`, `"16"`, `"18"`

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
  "language": "VOSE",
  "active": true
}
```
**`ageRating` en request usa el nombre del enum:** `ALL`, `SEVEN`, `TWELVE`, `SIXTEEN`, `EIGHTEEN`

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
      "name": "Sala 1 IMAX",
      "capacity": 6,
      "totalSeats": 6,
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

**Valores de `type`:** `STANDARD`, `VIP`

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
Lista **todas** las proyecciones (incluidas pasadas).

### GET `/api/screenings/upcoming`
⭐ **Solo proyecciones futuras** (`startTime > now()`). **Usar este endpoint en taquilla.**

### GET `/api/screenings/{id}`

**`ScreeningResponseDTO`:**
```json
{
  "id": 1,
  "movie": {
    "id": 1,
    "title": "Dune: Parte Dos",
    "durationMin": 166,
    "genre": "Ciencia Ficcion",
    "ageRating": "12",
    "imageUrl": null,
    "active": true,
    "language": "VOSE"
  },
  "theater": {
    "id": 1,
    "name": "Sala 1 IMAX",
    "capacity": 6
  },
  "startTime": "2026-05-20T18:00:00",
  "endDatetime": "2026-05-20T20:46:00",
  "basePrice": 12.50,
  "availableSeats": 4,
  "full": false
}
```

> ⚠️ **FRONT:** El campo de fecha/hora se llama **`startTime`**, no `dateTime`.  
> En `BoxOfficePage.jsx` hay usos de `selectedSession.dateTime` que deben cambiarse a `selectedSession.startTime`.

### GET `/api/screenings/movie/{movieId}`
Proyecciones de una película.

### GET `/api/screenings/{id}/seats`
⭐ Lista los asientos de una proyección con su estado de ocupación.

**Response `200`:**
```json
{
  "success": true,
  "data": [
    {
      "id": 5,
      "screeningId": 1,
      "seat": {
        "id": 1,
        "theaterId": 1,
        "row": "A",
        "number": 1,
        "type": "VIP"
      },
      "occupied": false
    },
    {
      "id": 6,
      "screeningId": 1,
      "seat": {
        "id": 2,
        "theaterId": 1,
        "row": "A",
        "number": 2,
        "type": "VIP"
      },
      "occupied": true
    }
  ]
}
```

> El `seatsService.getByScreening()` ya normaliza esta respuesta correctamente:  
> mapea `s.seat.id` → `id`, `s.seat.row` → `row`, etc.  
> El `id` devuelto por el servicio es el **ID real del asiento** (seat.id), que es lo que debe enviarse en `seatId` al crear una compra.

### GET `/api/screenings/{id}/purchases`
Compras de una proyección.

### POST `/api/screenings`
```json
{
  "movieId": 1,
  "theaterId": 1,
  "startTime": "2026-06-15T20:00:00",
  "basePrice": 9.50
}
```
La `startTime` debe ser futura (`400` si es pasada).

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
    "seat": { "id": 1, "row": "A", "number": 1, "type": "VIP" },
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
  "movieTitle": "Dune: Parte Dos",
  "theaterName": "Sala 1 IMAX",
  "dateTime": "2026-05-20T18:00:00",
  "tickets": [
    {
      "id": 101,
      "purchaseId": 42,
      "seatId": 1,
      "row": "A",
      "number": 1,
      "seatType": "VIP",
      "ticketType": "ADULT",
      "unitPrice": 12.50
    }
  ],
  "totalAmount": 12.50,
  "discountApplied": false,
  "discountAmount": 0.00,
  "status": "PENDING",
  "paymentIntentId": null,
  "paymentMethod": null,
  "createdAt": "2026-05-12T10:00:00"
}
```

**Valores de `status`:** `PENDING`, `PAID`, `CONFIRMED`, `CANCELLED`, `REFUNDED`

### GET `/api/purchases/user/{userId}`
Historial de compras de un usuario.

### GET `/api/purchases/screening/{screeningId}`
Compras de una proyección.

### POST `/api/purchases`
Crea una compra en estado `PENDING`. Los asientos quedan reservados.

```json
{
  "userId": 2,
  "screeningId": 1,
  "tickets": [
    { "seatId": 1, "ticketType": "ADULT" },
    { "seatId": 2, "ticketType": "CHILD" }
  ]
}
```

> ⚠️ **`userId` es obligatorio y no puede ser `null`.**  
> En taquilla, si no hay cliente seleccionado, enviar el ID del cajero logueado como `userId`.  
> En `salesService.js` → `createTicketSale`, la línea debe ser:
> ```js
> userId: data.userId ?? data.cashierId ?? null,
> ```

> ⚠️ **`seatId` debe ser un número entero** (el ID del asiento en BD), nunca un string como `"A01"`.  
> Se obtiene de `GET /api/screenings/{id}/seats` → campo `seat.id`.

**Tipos de ticket y precio resultante:**

| `ticketType` | Precio base | Nota |
|---|---|---|
| `ADULT` | `basePrice` de la proyección | ×1.5 si asiento VIP |
| `CHILD` | 6,00 € fijo | Requiere al menos un ADULT en la misma compra |
| `STUDENT` | 6,00 € fijo | |
| `SENIOR` | 2,00 € fijo | |

**Descuento de fidelidad:** si el usuario tiene más de 10 visitas en el año actual, se aplica 10% sobre el subtotal de entradas `ADULT`.

### POST `/api/purchases/{id}/confirm`
Cambia estado de `PENDING` a `PAID`. Solo aplica sobre compras `PENDING`.  
Incrementa `visitsCurrentYear` del usuario. Envía email de confirmación.

### POST `/api/purchases/{id}/cancel`
Cambia estado a `CANCELLED`. Libera los asientos reservados.

---

## 9. Pagos — Stripe — `/api/payments`

Flujo completo para pago online con tarjeta:

```
1. POST /api/purchases          → crea compra PENDING, reserva asientos
2. POST /api/payments/intent    → crea PaymentIntent en Stripe, devuelve clientSecret
3. [Frontend] Stripe Elements   → el usuario introduce tarjeta
4. Stripe → POST /api/payments/webhook (payment_intent.succeeded) → marca compra como PAID
5. (Opcional) POST /api/purchases/{id}/confirm → confirma manualmente si el webhook no llega
```

### POST `/api/payments/intent`  (también: `/api/payments/create-intent`)
Crea un PaymentIntent de Stripe para una compra existente.

**Request body:**
```json
{
  "purchaseId": 42,
  "amount": 12.50,
  "currency": "EUR"
}
```

> ⚠️ `amount` debe coincidir con `totalAmount` de la compra.  
> El backend lo convierte a céntimos internamente (12.50 → 1250).

**Response `201 Created`:**
```json
{
  "success": true,
  "message": "PaymentIntent creado correctamente",
  "data": {
    "clientSecret": "pi_3P...._secret_...",
    "paymentIntentId": "pi_3P....",
    "publishableKey": "pk_test_..."
  }
}
```

> El frontend debe usar `clientSecret` con Stripe.js / Stripe Elements para completar el pago.  
> `publishableKey` es la clave pública de Stripe que necesita Stripe.js.

**Ejemplo de uso en React:**
```js
// 1. Crear la compra
const purchase = await salesService.createPurchase({ userId, screeningId, tickets });

// 2. Crear el PaymentIntent
const { clientSecret, publishableKey } = await salesService.createPaymentIntent(purchase.id, purchase.totalAmount);

// 3. Mostrar StripePaymentModal con clientSecret y publishableKey
setStripeData({ clientSecret, publishableKey, purchaseId: purchase.id });
```

### POST `/api/payments/webhook`
Webhook de Stripe. **Solo lo llama Stripe, no el frontend.**

| Evento Stripe | Acción backend |
|---|---|
| `payment_intent.succeeded` | Cambia compra a `PAID`, descuenta stock de merchandising |
| `payment_intent.payment_failed` | Cambia compra a `CANCELLED` |

> Para pruebas locales usar Stripe CLI:
> ```bash
> stripe listen --forward-to localhost:8080/api/payments/webhook
> ```

### POST `/api/payments/refund`
Solicita un reembolso en Stripe y actualiza la compra a `REFUNDED`.

**Request body:**
```json
{
  "purchaseId": 42,
  "reason": "Solicitud del cliente"
}
```

**Response `200`:**
```json
{
  "success": true,
  "message": "Reembolso procesado correctamente",
  "data": {
    "refundId": "re_3P....",
    "amount": 12.50,
    "status": "succeeded"
  }
}
```

> Requiere que la compra tenga `paymentIntentId` (es decir, que haya pasado por Stripe).  
> No aplica a compras pagadas en efectivo.

### GET `/api/payments/history`
Historial de pagos con filtros opcionales.

| Query param | Tipo | Descripción |
|---|---|---|
| `from` | `YYYY-MM-DD` | Fecha inicio |
| `to` | `YYYY-MM-DD` | Fecha fin |
| `status` | string | `PENDING`, `PAID`, `CANCELLED`, `REFUNDED` |

**Ejemplo:** `GET /api/payments/history?from=2026-05-01&to=2026-05-31&status=PAID`

**Response `200`:**
```json
{
  "success": true,
  "data": [
    {
      "purchaseId": 1,
      "paymentIntentId": "pi_test_lumen_001",
      "amount": 25.00,
      "status": "PAID",
      "paymentMethod": "CARD",
      "type": "purchase",
      "createdAt": "2026-05-12T11:00:00",
      "userId": 2,
      "userName": "Cliente Lumen"
    }
  ]
}
```

**Variables de entorno necesarias en el backend:**
```
STRIPE_SECRET_KEY=sk_test_...
STRIPE_PUBLISHABLE_KEY=pk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
```

---

## 10. Tickets — `/api/tickets`

Los tickets se crean automáticamente al crear una compra.

### GET `/api/tickets`
Lista todos los tickets. Admite filtros opcionales:

| Query param | Descripción |
|---|---|
| `?purchaseId=42` | Tickets de una compra |
| `?screeningId=1` | Tickets de una proyección |

### GET `/api/tickets/{id}`

---

## 11. Merchandise — `/api/merchandise`

### GET `/api/merchandise`
### GET `/api/merchandise/{id}`

**`MerchandiseResponseDTO`:**
```json
{
  "id": 1,
  "name": "Palomitas Grandes",
  "description": "Cubo grande de palomitas.",
  "category": "FOOD",
  "price": 5.50,
  "stock": 100,
  "imageUrl": null,
  "active": true,
  "createdAt": "2026-05-12T10:00:00"
}
```

**Valores de `category`:** `CLOTHING`, `ACCESSORIES`, `POSTERS`, `COLLECTIBLES`, `FOOD`, `DRINK`, `MERCHANDISE`, `OTHER`

### POST `/api/merchandise`
```json
{
  "name": "Camiseta",
  "description": "Camiseta algodón",
  "category": "CLOTHING",
  "price": 19.99,
  "stock": 100
}
```

### PUT `/api/merchandise/{id}`
### DELETE `/api/merchandise/{id}`

---

## 12. Ventas de Merchandise — `/api/merchandisesales`

### GET `/api/merchandisesales`
### GET `/api/merchandisesales/{id}`

**`MerchandiseSaleResponseDTO`:**
```json
{
  "id": 1,
  "userId": 2,
  "merchandiseId": 1,
  "merchandiseName": "Palomitas Grandes",
  "quantity": 2,
  "total": 11.00,
  "saleDate": "2026-05-12T11:05:00"
}
```

### POST `/api/merchandisesales`
```json
{
  "userId": 2,
  "merchandiseId": 1,
  "quantity": 2
}
```

### PUT `/api/merchandisesales/{id}`
### DELETE `/api/merchandisesales/{id}`

---

## 13. Empleados — `/api/employees`

### GET `/api/employees`
### GET `/api/employees/{id}`

**`EmployeeResponseDTO`:**
```json
{
  "id": 1,
  "name": "Maria Fernandez",
  "email": "maria@lumen.com",
  "role": "CAJERO",
  "createdAt": "2026-05-12T10:00:00"
}
```

**Valores de `role`:** `CAJERO`, `GERENCIA`, `SEGURIDAD`, `LIMPIEZA`

### POST `/api/employees`
```json
{
  "name": "Carlos López",
  "email": "carlos@lumen.com",
  "role": "CAJERO"
}
```

### PUT `/api/employees/{id}`
### DELETE `/api/employees/{id}`

---

## 14. Turnos — `/api/shifts`

### GET `/api/shifts`
### GET `/api/shifts/{id}`

**`ShiftResponseDTO`:**
```json
{
  "id": 1,
  "employeeId": 1,
  "employeeName": "Maria Fernandez",
  "employeeEmail": "maria@lumen.com",
  "employeeRole": "CAJERO",
  "shiftDate": "2026-05-20",
  "startTime": "09:00:00",
  "endTime": "17:00:00",
  "notes": "Turno de taquilla.",
  "status": "SCHEDULED",
  "createdAt": "2026-05-12T10:00:00"
}
```

### GET `/api/shifts/date/{date}`
Turnos de un día concreto. Formato: `YYYY-MM-DD`

**Ejemplo:** `GET /api/shifts/date/2026-05-20`

### GET `/api/shifts/range?from=YYYY-MM-DD&to=YYYY-MM-DD`
Turnos en un rango de fechas.

### POST `/api/shifts`
```json
{
  "employeeId": 1,
  "shiftDate": "2026-05-20",
  "startTime": "09:00:00",
  "endTime": "17:00:00",
  "notes": "Turno apertura",
  "status": "SCHEDULED"
}
```

### PUT `/api/shifts/{id}`
### DELETE `/api/shifts/{id}`

---

## 15. Incidencias — `/api/incidents`

### GET `/api/incidents`
### GET `/api/incidents/{id}`

**`IncidentResponseDTO`:**
```json
{
  "id": 1,
  "title": "Proyector Sala 1",
  "description": "Parpadeo ocasional durante la sesion.",
  "severity": "MEDIA",
  "resolved": false,
  "createdAt": "2026-05-12T10:00:00",
  "updatedAt": "2026-05-12T10:00:00"
}
```

**Valores de `severity`:** `BAJA`, `MEDIA`, `ALTA`

### POST `/api/incidents`
```json
{
  "title": "Proyector averiado",
  "description": "El proyector de la Sala 2 no enciende",
  "severity": "ALTA"
}
```

### PUT `/api/incidents/{id}`
### DELETE `/api/incidents/{id}`

---

## 16. Dashboard — `/api/dashboard`

### GET `/api/dashboard`
Resumen general del negocio.

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

## 17. Reportes — `/api/reports`

### GET `/api/reports/sales-week`
Ventas de los últimos 7 días agrupadas por día.

**Response:**
```json
{
  "success": true,
  "data": [
    { "date": "2026-05-06", "totalPurchases": 12, "revenue": 114.00 },
    { "date": "2026-05-07", "totalPurchases": 8,  "revenue": 76.00 }
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
      "movieTitle": "Dune: Parte Dos",
      "theaterName": "Sala 1 IMAX",
      "dateTime": "2026-05-20T18:00:00",
      "totalSeats": 6,
      "occupiedSeats": 2,
      "occupancyPercentage": 33.3
    }
  ]
}
```

---

## 18. Errores

| HTTP | Caso |
|---|---|
| `400` | Validación fallida (campo requerido, tipo incorrecto) o fecha pasada en proyección |
| `401` | Credenciales inválidas o token expirado |
| `403` | Restricción de edad (película +18 con usuario menor) |
| `404` | Recurso no encontrado |
| `409` | Conflicto: email duplicado, asiento ya ocupado, proyección llena, compra ya cancelada |
| `422` | Estado de compra inválido (confirmar una compra que no está PENDING) · Menor sin adulto |

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

## 19. Enums de referencia

| Enum | Valores |
|---|---|
| `Role` (usuario app) | `ADMIN`, `CLIENTE` |
| `UserType` | `ADULT`, `CHILD`, `STUDENT`, `SENIOR` |
| `TicketType` | `ADULT`, `CHILD`, `STUDENT`, `SENIOR` |
| `SeatType` | `STANDARD`, `VIP` |
| `PurchaseStatus` | `PENDING`, `PAID`, `CONFIRMED`, `CANCELLED`, `REFUNDED` |
| `AgeRating` (JSON response) | `"ALL"`, `"7"`, `"12"`, `"16"`, `"18"` |
| `AgeRating` (request body) | `ALL`, `SEVEN`, `TWELVE`, `SIXTEEN`, `EIGHTEEN` |
| `EmployeeRole` | `CAJERO`, `GERENCIA`, `SEGURIDAD`, `LIMPIEZA` |
| `ShiftStatus` | `SCHEDULED`, `COMPLETED`, `ABSENT` |
| `MerchandiseCategory` | `CLOTHING`, `ACCESSORIES`, `POSTERS`, `COLLECTIBLES`, `FOOD`, `DRINK`, `MERCHANDISE`, `OTHER` |

---

## 20. Cambios recientes — fixes pendientes en front

### Fixes ya aplicados en Backend (2026-05-12)

| Archivo | Qué se corrigió |
|---|---|
| `ScreeningServiceImpl.java` | `getSeats()` devolvía `List.of()` vacío — ahora devuelve los asientos reales de la proyección |

### Fixes ya aplicados en Frontend (2026-05-12)

| Archivo | Qué se corrigió |
|---|---|
| `salesService.js` | `userId` usaba `null` cuando no había cliente → ahora usa `data.userId ?? data.cashierId` |
| `BoxOfficePage.jsx` | Cargaba sesiones con `getAll()` (devolvía pasadas también) → ahora usa `getUpcoming()` |

---

### Cambio pendiente en Frontend — CRÍTICO

**Archivo:** `BoxOfficePage.jsx`  
**Problema:** El campo de fecha/hora en `ScreeningResponseDTO` se llama **`startTime`**, pero el frontend accede a `selectedSession.dateTime` que siempre es `undefined`.

**Líneas afectadas:**
```js
// INCORRECTO (actual)
const time = selectedSession.dateTime?.split('T')[1]?.substring(0, 5) ?? '';
const date = selectedSession.dateTime?.split('T')[0] ?? '';

// CORRECTO
const time = selectedSession.startTime?.split('T')[1]?.substring(0, 5) ?? '';
const date = selectedSession.startTime?.split('T')[0] ?? '';
```

Esto afecta a la generación de QR codes y al texto de los tickets impresos.

---

### BD — Datos de prueba

Para tener sesiones futuras visibles en taquilla, ejecutar:
```bash
mysql -u root -p cinema < database/seed_full_test.sql
```

Si ya hay datos, limpiar primero:
```sql
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE refunds; TRUNCATE TABLE merchandise_sale;
TRUNCATE TABLE ticket; TRUNCATE TABLE purchase;
TRUNCATE TABLE screening_seat; TRUNCATE TABLE screening;
TRUNCATE TABLE shift; TRUNCATE TABLE room_booking; TRUNCATE TABLE room;
TRUNCATE TABLE incident; TRUNCATE TABLE seat; TRUNCATE TABLE theater;
TRUNCATE TABLE merchandise; TRUNCATE TABLE workers;
TRUNCATE TABLE movie; TRUNCATE TABLE clients;
SET FOREIGN_KEY_CHECKS = 1;
```

---

*Actualizado: 2026-05-12 · Rama: `feat/springTwo`*