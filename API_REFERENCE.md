# Lumen Cinema — Referencia API & Tablas

**Base URL:** `http://localhost:8080`  
**Autenticación:** Bearer Token en header `Authorization: Bearer <token>`  
**Formato:** JSON (salvo subida de imágenes)

---

## Autenticación

### POST `/api/auth/login`
Pública. Devuelve el JWT.

**Request:**
```json
{
  "email": "admin@lumen.com",
  "password": "1234"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Inicio de sesión correcto",
  "data": {
    "token": "eyJ...",
    "tokenType": "Bearer",
    "expiresInSeconds": 1800,
    "userId": 1,
    "email": "admin@lumen.com",
    "role": "ADMIN"
  }
}
```

### POST `/api/auth/register`
Pública. Crea usuario con rol CLIENT por defecto.

**Request:**
```json
{
  "name": "Juan García",
  "username": "juangarcia",
  "email": "juan@email.com",
  "password": "segura123",
  "dateOfBirth": "1995-03-15",
  "student": false,
  "visitsPerYear": 0,
  "role": "CLIENT",
  "status": "active"
}
```
**Response:** igual que `/login`

---

## Roles disponibles

| Rol | Descripción |
|-----|-------------|
| `ADMIN` | Acceso total |
| `SUPERVISOR` | Gestión y reportes |
| `OPERATOR` | Gestión de contenido |
| `TICKET` | Taquilla |
| `MAINTENANCE` | Incidencias |
| `READONLY` | Solo lectura |
| `CLIENT` | Cliente final |

---

## Tabla `users`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `name` | VARCHAR | Mín 2 chars, NOT NULL |
| `username` | VARCHAR | Único |
| `email` | VARCHAR | Único, NOT NULL |
| `password` | VARCHAR | BCrypt, NOT NULL |
| `date_of_birth` | DATE | |
| `student` | BOOLEAN | Default false |
| `visits_per_year` | INT | Default 0 |
| `role` | ENUM | ADMIN/SUPERVISOR/OPERATOR/TICKET/MAINTENANCE/READONLY/CLIENT |
| `status` | VARCHAR | Default "active" |
| `image_url` | VARCHAR | |
| `last_login` | DATETIME | |
| `created_at` | DATETIME | Auto |
| `updated_at` | DATETIME | Auto |

### Endpoints `/api/users` — Requiere rol ADMIN o SUPERVISOR

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/users` | Listar todos los usuarios |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| POST | `/api/users` | Crear usuario |
| PUT | `/api/users/{id}` | Actualizar usuario |
| DELETE | `/api/users/{id}` | Eliminar usuario |
| POST | `/api/users/{id}/image` | Subir foto de perfil (multipart) |

**Request POST/PUT:**
```json
{
  "name": "Ana López",
  "username": "analopez",
  "email": "ana@lumen.com",
  "password": "pass123",
  "dateOfBirth": "1990-06-20",
  "student": false,
  "visitsPerYear": 3,
  "role": "OPERATOR",
  "status": "active"
}
```
**Response:**
```json
{
  "id": 5,
  "name": "Ana López",
  "username": "analopez",
  "email": "ana@lumen.com",
  "dateOfBirth": "1990-06-20",
  "student": false,
  "visitsPerYear": 3,
  "role": "OPERATOR",
  "status": "active",
  "imageUrl": null,
  "lastLogin": null,
  "createdAt": "2026-05-07T10:00:00",
  "updatedAt": "2026-05-07T10:00:00"
}
```

---

## Tabla `movie`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `title` | VARCHAR | NOT NULL |
| `description` | VARCHAR | |
| `director` | VARCHAR | |
| `year` | INT | |
| `genre` | VARCHAR | |
| `language` | VARCHAR | |
| `format` | VARCHAR | Ej: "2D", "3D", "IMAX" |
| `duration_min` | INT | NOT NULL |
| `age_rating` | VARCHAR | ALL / 7 / 12 / 16 / 18 |
| `image_url` | VARCHAR | |
| `active` | BOOLEAN | Default true |
| `created_at` | DATETIME | Auto |

### Endpoints `/api/movies`

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/movies` | Pública | Listar todas |
| GET | `/api/movies/active` | Pública | Solo activas |
| GET | `/api/movies/{id}` | Pública | Obtener por ID |
| POST | `/api/movies` | ADMIN/SUPERVISOR/OPERATOR | Crear (JSON) |
| POST | `/api/movies` | ADMIN/SUPERVISOR/OPERATOR | Crear con imagen (multipart: `movie` + `image`) |
| PUT | `/api/movies/{id}` | ADMIN/SUPERVISOR/OPERATOR | Actualizar |
| DELETE | `/api/movies/{id}` | ADMIN/SUPERVISOR | Eliminar |

**Request POST (JSON):**
```json
{
  "title": "Dune: Parte Dos",
  "description": "La épica continúa...",
  "director": "Denis Villeneuve",
  "year": 2024,
  "genre": "Ciencia Ficción",
  "language": "Español",
  "format": "IMAX",
  "durationMin": 166,
  "ageRating": "12",
  "imageUrl": "https://...",
  "active": true
}
```
**Response:**
```json
{
  "id": 3,
  "title": "Dune: Parte Dos",
  "description": "La épica continúa...",
  "director": "Denis Villeneuve",
  "year": 2024,
  "genre": "Ciencia Ficción",
  "language": "Español",
  "format": "IMAX",
  "durationMin": 166,
  "ageRating": "12",
  "imageUrl": "https://...",
  "active": true,
  "createdAt": "2026-05-07T10:00:00"
}
```

> **ageRating** acepta: `"ALL"`, `"7"`, `"12"`, `"16"`, `"18"`

---

## Tabla `theaters`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `name` | VARCHAR | NOT NULL |
| `capacity` | INT | Mín 1 |
| `status` | VARCHAR | Default "active" |
| `created_at` | DATETIME | Auto |
| `updated_at` | DATETIME | Auto |

### Endpoints `/api/theaters`

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/theaters` | Pública | Listar todas |
| GET | `/api/theaters/{id}` | Pública | Obtener por ID |
| GET | `/api/theaters/{id}/seats` | Pública | Asientos de la sala |
| POST | `/api/theaters` | ADMIN/SUPERVISOR | Crear sala |
| PUT | `/api/theaters/{id}` | ADMIN/SUPERVISOR | Actualizar sala |
| DELETE | `/api/theaters/{id}` | ADMIN | Eliminar sala |

**Request POST:**
```json
{
  "name": "Sala 1",
  "capacity": 120,
  "status": "active"
}
```
**Response:**
```json
{
  "id": 1,
  "name": "Sala 1",
  "capacity": 120,
  "totalSeats": 0,
  "status": "active",
  "createdAt": "2026-05-07T10:00:00",
  "updatedAt": "2026-05-07T10:00:00"
}
```

---

## Tabla `seats`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `theater_id` | BIGINT FK | → theaters |
| `seat_row` | VARCHAR | Ej: "A", "B" |
| `number` | INT | Mín 1 |
| `type` | ENUM | STANDARD / VIP |
| `created_at` | DATETIME | Auto |
| `updated_at` | DATETIME | Auto |

> Restricción única: (theater_id, seat_row, number)

### Endpoints `/api/seats`

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/seats` | Pública | Listar todos |
| GET | `/api/seats/{id}` | Pública | Obtener por ID |
| POST | `/api/seats` | Autenticado | Crear asiento |
| PUT | `/api/seats/{id}` | Autenticado | Actualizar |
| DELETE | `/api/seats/{id}` | Autenticado | Eliminar |

**Request POST:**
```json
{
  "theaterId": 1,
  "row": "A",
  "number": 5,
  "type": "VIP"
}
```
**Response:**
```json
{
  "id": 10,
  "theaterId": 1,
  "row": "A",
  "number": 5,
  "type": "VIP",
  "status": null
}
```

---

## Tabla `screenings`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `movie_id` | BIGINT FK | → movie |
| `theater_id` | BIGINT FK | → theaters |
| `date_time` | DATETIME | NOT NULL |
| `base_price` | DECIMAL | NOT NULL, ≥ 0 |
| `available_seats` | INT | |
| `status` | ENUM | SCHEDULED / ACTIVE / CANCELLED / FULL |
| `created_at` | DATETIME | Auto |
| `updated_at` | DATETIME | Auto |

### Tabla `screening_seats` (estado de cada asiento por función)

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `screening_id` | BIGINT FK | → screenings |
| `seat_id` | BIGINT FK | → seats |
| `occupied` | BOOLEAN | Default false |

### Endpoints `/api/screenings`

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/screenings` | Pública | Listar todas (opcional `?date=2026-05-10`) |
| GET | `/api/screenings/upcoming` | Pública | Solo futuras |
| GET | `/api/screenings/{id}` | Pública | Obtener por ID |
| GET | `/api/screenings/movie/{movieId}` | Pública | Por película |
| GET | `/api/screenings/{id}/seats` | Pública | Asientos con disponibilidad |
| GET | `/api/screenings/{id}/purchases` | Autenticado | Compras de esa función |
| POST | `/api/screenings` | ADMIN/SUPERVISOR/OPERATOR | Crear función |
| PUT | `/api/screenings/{id}` | ADMIN/SUPERVISOR/OPERATOR | Actualizar |
| DELETE | `/api/screenings/{id}` | ADMIN/SUPERVISOR | Eliminar |
| POST | `/api/screenings/{id}/seats/{seatId}/reserve` | Autenticado | Reservar asiento |
| POST | `/api/screenings/{id}/seats/{seatId}/release` | Autenticado | Liberar asiento |

**Request POST:**
```json
{
  "movieId": 3,
  "theaterId": 1,
  "dateTime": "2026-05-15T20:30:00",
  "basePrice": 9.50
}
```
**Response:**
```json
{
  "id": 7,
  "movie": { "id": 3, "title": "Dune: Parte Dos", ... },
  "theater": { "id": 1, "name": "Sala 1", ... },
  "dateTime": "2026-05-15T20:30:00",
  "basePrice": 9.50,
  "price": 9.50,
  "availableSeats": 120,
  "status": "SCHEDULED",
  "full": false,
  "createdAt": "2026-05-07T10:00:00",
  "updatedAt": "2026-05-07T10:00:00"
}
```

---

## Tabla `purchases`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `user_id` | BIGINT FK | → users |
| `screening_id` | BIGINT FK | → screenings |
| `status` | ENUM | PENDING / CONFIRMED / CANCELLED / REFUNDED |
| `payment_method` | ENUM | CARD / QR / CASH / ONLINE |
| `total_amount` | DECIMAL | NOT NULL |
| `discount_applied` | BOOLEAN | Default false |
| `discount_amount` | DECIMAL | Default 0 |
| `created_at` | DATETIME | Auto |
| `updated_at` | DATETIME | Auto |

### Tabla `tickets`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `purchase_id` | BIGINT FK | → purchases |
| `seat_id` | BIGINT FK | → seats |
| `screening_id` | BIGINT FK | → screenings |
| `ticket_type` | ENUM | CHILD / ADULT / STUDENT / SENIOR |
| `unit_price` | DECIMAL | NOT NULL |
| `created_at` | DATETIME | Auto |
| `updated_at` | DATETIME | Auto |

> Restricción única en tickets: (screening_id, seat_id)

### Endpoints `/api/purchases`

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/purchases` | Autenticado | Listar todas |
| GET | `/api/purchases/{id}` | Autenticado | Obtener por ID |
| GET | `/api/purchases/user/{userId}` | Autenticado | Por usuario |
| GET | `/api/purchases/screening/{screeningId}` | Autenticado | Por función |
| POST | `/api/purchases` | Autenticado | Crear reserva (panel admin) |
| PUT | `/api/purchases/{id}` | Autenticado | Actualizar reserva (panel admin) |
| POST | `/api/purchases/online` | Autenticado | Compra online con asientos |
| POST | `/api/purchases/ticket-office` | TICKET/ADMIN | Venta en taquilla |
| POST | `/api/purchases/{id}/pay` | Autenticado | Pagar compra pendiente |
| POST | `/api/purchases/{id}/confirm` | Autenticado | Confirmar compra |
| POST | `/api/purchases/{id}/cancel` | Autenticado | Cancelar compra |
| POST | `/api/purchases/{id}/email` | Autenticado | Reenviar email confirmación |

**Request POST `/api/purchases` (reserva admin):**
```json
{
  "clientName": "María Pérez",
  "clientEmail": "maria@email.com",
  "screeningId": 7,
  "paymentMethod": "CARD",
  "status": "PENDING",
  "totalAmount": 19.00
}
```

**Request POST `/api/purchases/online` (compra con asientos):**
```json
{
  "userId": 5,
  "screeningId": 7,
  "tickets": [
    { "seatId": 10, "ticketType": "ADULT" },
    { "seatId": 11, "ticketType": "CHILD" }
  ]
}
```

**Request POST `/api/purchases/ticket-office`:**
```json
{
  "screeningId": 7,
  "seats": ["A5", "A6"],
  "ticketType": "ADULT",
  "unitPrice": 9.50,
  "surcharge": 0.50,
  "total": 20.00,
  "paymentMethod": "CASH",
  "cashierId": 2
}
```
**Response taquilla:**
```json
{
  "saleId": 42,
  "qrCodes": [
    "LUMEN:TKT-101|Dune: Parte Dos|Sala 1|2026-05-15|20:30|A5|ADULT",
    "LUMEN:TKT-102|Dune: Parte Dos|Sala 1|2026-05-15|20:30|A6|ADULT"
  ]
}
```

**Request POST `/api/purchases/{id}/pay`:**
```json
{
  "paymentMethod": "CARD",
  "cardLastFour": "1234"
}
```

**Response compra genérica:**
```json
{
  "id": 42,
  "userId": 5,
  "clientName": "María Pérez",
  "clientEmail": "maria@email.com",
  "screeningId": 7,
  "movieTitle": "Dune: Parte Dos",
  "theaterName": "Sala 1",
  "dateTime": "2026-05-15T20:30:00",
  "screening": {
    "id": 7,
    "dateTime": "2026-05-15T20:30:00",
    "movie": { "title": "Dune: Parte Dos" },
    "theater": { "name": "Sala 1" }
  },
  "seats": ["A5", "A6"],
  "tickets": [
    {
      "id": 101,
      "purchaseId": 42,
      "seatId": 10,
      "row": "A",
      "number": 5,
      "seatType": "STANDARD",
      "ticketType": "ADULT",
      "unitPrice": 9.50,
      "qrCode": "LUMEN:TKT-101|..."
    }
  ],
  "totalAmount": 19.00,
  "discountApplied": false,
  "discountAmount": 0.00,
  "status": "PENDING",
  "paymentMethod": "CARD",
  "createdAt": "2026-05-07T10:00:00"
}
```

> **Descuento de fidelidad:** se aplica automáticamente a entradas ADULT si el usuario tiene ≥ 10 visitas/año o es estudiante.

---

## Clientes (socios)

Los clientes son usuarios con rol `CLIENT`. El endpoint `/api/clients` es un acceso simplificado para staff de taquilla.

### Endpoints `/api/clients` — Requiere ADMIN/SUPERVISOR/OPERATOR/TICKET

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/clients` | Listar todos los clientes |
| GET | `/api/clients/{id}` | Obtener cliente por ID |
| GET | `/api/clients/search?q=nombre` | Buscar por nombre, email o username |

**Response:**
```json
{
  "id": 5,
  "name": "Juan García",
  "email": "juan@email.com",
  "username": "juangarcia",
  "student": false,
  "visitsPerYear": 12,
  "fidelityDiscountEligible": true,
  "status": "active",
  "dateOfBirth": "1995-03-15"
}
```

> `fidelityDiscountEligible` = true si `visitsPerYear >= 10` o `student = true`

---

## Tabla `merchandise`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `name` | VARCHAR | NOT NULL |
| `description` | VARCHAR | |
| `category` | VARCHAR | CLOTHING/ACCESSORIES/POSTERS/COLLECTIBLES/FOOD/DRINK/MERCHANDISE/OTHER |
| `price` | DOUBLE | NOT NULL |
| `stock` | INT | NOT NULL |
| `image_url` | VARCHAR | |
| `active` | BOOLEAN | Default true |
| `created_at` | DATETIME | Auto |

### Tabla `merchandise_sale`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `cashier_id` | BIGINT FK | → users |
| `total` | DOUBLE | |
| `payment_method` | VARCHAR | |
| `cash_given` | DOUBLE | |
| `change_amount` | DOUBLE | |
| `created_at` | DATETIME | Auto |

### Tabla `merchandise_sale_item`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `sale_id` | BIGINT FK | → merchandise_sale |
| `product_id` | BIGINT | |
| `name` | VARCHAR | |
| `qty` | INT | |
| `unit_price` | DOUBLE | |

### Endpoints `/api/merchandise`

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/merchandise` | Pública | Listar todo |
| GET | `/api/merchandise/active` | Pública | Solo activos |
| GET | `/api/merchandise/{id}` | Pública | Por ID |
| POST | `/api/merchandise` | Autenticado | Crear producto |
| PUT | `/api/merchandise/{id}` | Autenticado | Actualizar |
| DELETE | `/api/merchandise/{id}` | Autenticado | Eliminar |

**Request POST:**
```json
{
  "name": "Camiseta Lumen",
  "description": "Camiseta oficial del cine",
  "category": "CLOTHING",
  "price": 24.99,
  "stock": 50,
  "imageUrl": "https://..."
}
```

### Endpoints `/api/merchandise/sales`

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/merchandise/sales` | Autenticado | Listar ventas |
| GET | `/api/merchandise/sales/{id}` | Autenticado | Venta por ID |
| POST | `/api/merchandise/sales` | Autenticado | Crear venta |
| PUT | `/api/merchandise/sales/{id}` | Autenticado | Actualizar venta |
| DELETE | `/api/merchandise/sales/{id}` | Autenticado | Eliminar venta |

**Request POST venta:**
```json
{
  "items": [
    { "product_id": 1, "name": "Camiseta Lumen", "qty": 2, "unit_price": 24.99 }
  ],
  "total": 49.98,
  "payment_method": "CARD",
  "cash_given": null,
  "change": null,
  "cashier_id": 2
}
```

---

## Tabla `incidents`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `title` | VARCHAR | NOT NULL |
| `category` | VARCHAR | |
| `priority` | VARCHAR | Ej: "low", "medium", "high", "critical" |
| `status` | VARCHAR | Default "open" |
| `room` | VARCHAR | |
| `description` | TEXT | |
| `assigned_to` | VARCHAR | |
| `reported_by` | VARCHAR | NOT NULL |
| `created_at` | DATETIME | Auto |
| `updated_at` | DATETIME | Auto |

### Endpoints `/api/incidents` — Requiere ADMIN/SUPERVISOR/MAINTENANCE

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/incidents` | Listar todas |
| GET | `/api/incidents/{id}` | Por ID |
| POST | `/api/incidents` | Crear incidencia |
| PUT | `/api/incidents/{id}` | Actualizar |
| DELETE | `/api/incidents/{id}` | Eliminar |

**Request POST:**
```json
{
  "title": "Proyector averiado",
  "category": "equipment",
  "priority": "high",
  "status": "open",
  "room": "Sala 2",
  "description": "El proyector no enciende desde las 18:00",
  "assignedTo": "técnico1",
  "reportedBy": "operador@lumen.com"
}
```

---

## Tabla `audit_logs`

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | BIGINT PK | Auto |
| `timestamp` | DATETIME | Auto |
| `user` | VARCHAR | NOT NULL |
| `action` | VARCHAR | NOT NULL |
| `resource` | VARCHAR | |
| `detail` | TEXT | |
| `ip` | VARCHAR | |
| `severity` | VARCHAR | Default "info" |

### Endpoints `/api/audit-logs` — Requiere ADMIN/SUPERVISOR

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/audit-logs` | Listar (filtros opcionales) |

**Query params opcionales:** `severity`, `user`, `action`, `from` (fecha), `to` (fecha)

**Response:**
```json
[
  {
    "id": 1,
    "timestamp": "2026-05-07T09:30:00",
    "user": "admin@lumen.com",
    "action": "LOGIN",
    "resource": "auth",
    "detail": "Inicio de sesión exitoso",
    "ip": "192.168.1.10",
    "severity": "info"
  }
]
```

---

## Dashboard

### GET `/api/dashboard` — Requiere ADMIN/SUPERVISOR

**Response:** (estructura en desarrollo)
```json
{}
```

---

## Reportes

### Endpoints `/api/reports` — Requiere ADMIN/SUPERVISOR

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/reports/kpis` | KPIs principales |
| GET | `/api/reports/sales-week` | Ventas de los últimos 7 días |
| GET | `/api/reports/occupancy` | Ocupación por sala |
| GET | `/api/reports/incidents-by-category` | Incidencias agrupadas por categoría |
| GET | `/api/reports/format-performance` | Rendimiento por formato de película |

**Response `/kpis`:**
```json
{
  "revenueToday": 1250.00,
  "ticketsToday": 87,
  "occupancyAvg": 74,
  "incidentsOpen": 3,
  "activeSessions": 12,
  "reservationsToday": 5,
  "operationalRooms": 4,
  "totalClients": 340
}
```

**Response `/sales-week`:**
```json
[
  { "day": "Lun", "ventas": 850.0, "entradas": 60 },
  { "day": "Mar", "ventas": 920.0, "entradas": 65 }
]
```

**Response `/occupancy`:**
```json
[
  { "sala": "Sala 1", "pct": 82, "sold": 98, "capacity": 120 }
]
```

---

## Resumen de URLs públicas (sin token)

```
GET  /api/movies
GET  /api/movies/active
GET  /api/movies/{id}
GET  /api/screenings
GET  /api/screenings/upcoming
GET  /api/screenings/{id}
GET  /api/screenings/movie/{movieId}
GET  /api/screenings/{id}/seats
GET  /api/theaters
GET  /api/theaters/{id}
GET  /api/seats
GET  /api/seats/{id}
GET  /api/merchandise
GET  /api/merchandise/active
GET  /api/merchandise/{id}
POST /api/auth/login
POST /api/auth/register
```

---

## Enumerados

| Enum | Valores |
|------|---------|
| `Role` | ADMIN, SUPERVISOR, OPERATOR, TICKET, MAINTENANCE, READONLY, CLIENT |
| `TicketType` | CHILD, ADULT, STUDENT, SENIOR |
| `SeatType` | STANDARD, VIP |
| `PurchaseStatus` | PENDING, CONFIRMED, CANCELLED, REFUNDED |
| `PaymentMethod` | CARD, QR, CASH, ONLINE |
| `ScreeningStatus` | SCHEDULED, ACTIVE, CANCELLED, FULL |
| `AgeRating` | ALL, 7, 12, 16, 18 |
| `MerchandiseCategory` | CLOTHING, ACCESSORIES, POSTERS, COLLECTIBLES, FOOD, DRINK, MERCHANDISE, OTHER |

---

## Notas importantes para el Frontend

1. **Token JWT** dura 30 minutos. Renovar con nuevo login cuando expire (el servidor devuelve 401).
2. **Descuento de fidelidad** se calcula automáticamente en backend — no mandarlo desde front.
3. **Menores (CHILD) sin adulto** → el backend rechaza la compra con error 422.
4. **Subida de imágenes** (películas y usuarios): usar `multipart/form-data`. Las imágenes se almacenan en Cloudinary.
5. **El cuadrante de asientos** se construye llamando a `GET /api/screenings/{id}/seats` que devuelve todos los asientos con su estado (`occupied`).
6. **Los socios (clientes)** se guardan en la tabla `users` con `role = CLIENT`. Para verificar si tiene descuento usar el campo `fidelityDiscountEligible` de `/api/clients/{id}`.
7. **Wrapper de respuestas**: Auth devuelve `{ success, message, data }`. El resto de endpoints devuelve el objeto directamente o lista.