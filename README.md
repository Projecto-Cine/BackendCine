# 🎬 Cine Backend - Estado del Proyecto

## 📌 Descripción General
API REST desarrollada en **Java 17 + Spring Boot** para la gestión de un cine. Incluye gestión de películas, merchandising, usuarios, entradas, salas y más.

---

## 🌿 Ramas del Repositorio

| Rama | Descripción | Estado |
|------|--------------|--------|
| **main** | Configuración inicial del proyecto | ✅ Estable |
| **feature/project-setup** | Estructura base (DTOs, Services, Controllers, Enums) | 📁 Estructura completa |
| **feature/cloudinary-setup** | Integración con Cloudinary para imágenes | ☁️ Configurado |
| **feature/movie** | CRUD Películas + Merchandising + Correcciones | 🎬 Completo |

---

## ✅ Módulos Completos (CRUD Funcional)

### 1. 🎬 Películas (Movie)
- **Entidad:** `Movie.java` (id, title, description, genre, durationMin, ageRating, imageUrl, active, createdAt)
- **Enum:** `AgeRating` (ALL, SEVEN, TWELVE, SIXTEEN, EIGHTEEN) con conversor para Base de Datos
- **Endpoints:**
  - `GET /api/movies` - Listar todas
  - `GET /api/movies/active` - Listar activas
  - `GET /api/movies/{id}` - Obtener por ID
  - `POST /api/movies` - Crear (JSON o multipart/form-data)
  - `PUT /api/movies/{id}` - Actualizar
  - `DELETE /api/movies/{id}` - Eliminar (soft delete)

### 2. 🛍️ Merchandising (Merchandise)
- **Entidad:** `Merchandise.java` (id, name, description, category, price, stock, imageUrl, active, createdAt)
- **Enum:** `MerchandiseCategory` (CLOTHING, ACCESSORIES, POSTERS, COLLECTIBLES, OTHER, FOOD, DRINK, MERCHANDISE)
- **Endpoints:**
  - `GET /api/merchandise` - Listar todos
  - `GET /api/merchandise/active` - Listar activos
  - `GET /api/merchandise/{id}` - Obtener por ID
  - `POST /api/merchandise` - Crear
  - `PUT /api/merchandise/{id}` - Actualizar
  - `DELETE /api/merchandise/{id}` - Eliminar (soft delete)

### 3. ☁️ Cloudinary (Imágenes)
- **Configuración:** `CloudinaryConfig.java`
- **Servicio:** `CloudinaryService.java` para subida de imágenes desde el backend.

---

## 📁 Módulos con Estructura (Pendientes de Implementación CRUD)

| Módulo | Controlador | Service | Repository | Modelo | DTOs |
|--------|-------------|---------|------------|--------|-------|
| 🎟️ **Ticket** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 🪑 **Seat** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 🎭 **Theater** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 📅 **Screening** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 👤 **User** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 💰 **Purchase** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 🛒 **MerchandiseSale** | ✅ | ✅ | ✅ | ✅ | ✅ |
| 📊 **Dashboard** | ✅ | ✅ | - | - | ✅ |
| 📧 **Email** | - | ✅ | - | - | - |

---

## 🗂️ Estructura del Proyecto

```
src/main/java/com/cine/demo/
├── config/              # Configuraciones (Cloudinary)
├── controller/          # Controladores REST (9 módulos)
├── dto/
│   ├── request/        # DTOs para peticiones (9 request DTOs)
│   └── response/       # DTOs para respuestas (9 response DTOs)
├── model/              # Entidades JPA (9 modelos)
│   └── enums/         # Enums (AgeRating, MerchandiseCategory, SeatType, TicketType, etc.)
├── repository/         # Repositorios JPA (8 repositorios)
├── service/            # Interfaces de servicios
│   └── impl/          # Implementaciones de servicios
└── DemoApplication.java
```

---

## 🔧 Tecnologías Utilizadas
- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Hibernate**
- **MySQL** (Base de datos)
- **Cloudinary** (Almacenamiento de imágenes)
- **Lombok**
- **Maven**

---

## 🚀 Cómo Probar los Endpoints (Postman)

### Películas
- **GET** `http://localhost:8080/api/movies`
- **POST** `http://localhost:8080/api/movies` (Body JSON o form-data)
  ```json
  {
    "title": "Inception",
    "description": "Un ladrón...",
    "genre": "SCI_FI",
    "durationMin": 148,
    "ageRating": "16",
    "imageUrl": "https://example.com/inception.jpg"
  }
  ```

### Merchandising
- **GET** `http://localhost:8080/api/merchandise`
- **POST** `http://localhost:8080/api/merchandise` (Body JSON)
  ```json
  {
    "name": "Star Wars T-Shirt",
    "description": "Official t-shirt",
    "category": "CLOTHING",
    "price": 29.99,
    "stock": 100,
    "imageUrl": "https://example.com/tshirt.jpg"
  }
  ```

---

## 📝 Notas Importantes
- Se utilizan **soft deletes** (campo `active`) para no eliminar registros de la BD.
- Los enums tienen **conversores personalizados** para manejar datos legacy en la base de datos.
- El módulo **Movie** soporta subida de imágenes tanto por JSON como por `multipart/form-data`.
- La rama **feature/movie** contiene los últimos commits con correcciones de mapeo de enums.

---

**¡Proyecto en desarrollo!** Los módulos pendientes solo necesitan implementar la lógica CRUD en los servicios e implementaciones correspondientes.
