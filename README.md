# 🎬 Cine Backend - Estado del Proyecto

## 🌿 Ramas y Estructura

| Rama | Estado | Descripción |
|------|--------|--------------|
| **main** | ✅ Estable | Configuración inicial del proyecto Spring Boot |
| **feature/project-setup** | 📁 Estructura | Estructura base (DTOs, Services, Controllers, etc.) |
| **feature/cloudinary-setup** | ☁️ Configurado | Integración con Cloudinary para imágenes |
| **feature/movie** | 🎬 Completo | CRUD películas + Merchandise + Correcciones |

---

## 📦 Módulos Realizados

#### **1. 🎬 Películas (Movie)**
- **Entidad:** `Movie.java` (id, title, description, genre, durationMin, ageRating, imageUrl, active, createdAt)
- **Enum:** `AgeRating` (ALL, SEVEN, TWELVE, SIXTEEN, EIGHTEEN) con conversor para BD
- **Endpoints:**
  - `GET /api/movies` - Listar todas
  - `GET /api/movies/active` - Listar activas
  - `GET /api/movies/{id}` - Obtener por ID
  - `POST /api/movies` - Crear (JSON o multipart)
  - `PUT /api/movies/{id}` - Actualizar
  - `DELETE /api/movies/{id}` - Eliminar (soft delete)

#### **2. 🛍️ Merchandise (Tienda)**
- **Entidad:** `Merchandise.java` (id, name, description, category, price, stock, imageUrl, active, createdAt)
- **Enum:** `MerchandiseCategory` (CLOTHING, ACCESSORIES, POSTERS, COLLECTIBLES, OTHER, FOOD, DRINK, MERCHANDISE)
- **Endpoints:**
  - `GET /api/merchandise` - Listar todos
  - `GET /api/merchandise/active` - Listar activos
  - `GET /api/merchandise/{id}` - Obtener por ID
  - `POST /api/merchandise` - Crear
  - `PUT /api/merchandise/{id}` - Actualizar
  - `DELETE /api/merchandise/{id}` - Eliminar (soft delete)

#### **3. ☁️ Cloudinary**
- **Configuración:** `CloudinaryConfig.java`
- **Servicio:** `CloudinaryService.java` para subida de imágenes

---

### **🔧 Estructura Técnica**

```
src/main/java/com/cine/demo/
├── config/          # CloudinaryConfig
├── controller/      # MovieController, MerchandiseController, TheaterController, etc.
├── dto/
│   ├── request/     # MovieRequestDTO, MerchandiseRequestDTO, etc.
│   └── response/    # MovieResponseDTO, MerchandiseResponseDTO, etc.
├── model/           # Movie, Merchandise, User, Theater, Ticket, etc.
│   └── enums/      # AgeRating, MerchandiseCategory, SeatType, etc.
├── repository/      # MovieRepository, MerchandiseRepository, etc.
├── service/         # Interfaces de servicios
│   └── impl/       # Implementaciones
└── DemoApplication.java
```

---

### **📋 Pendiente (Detectado en ramas)**

| Módulo | Estado |
|--------|--------|
| **User** | Modelo creado, falta CRUD completo |
| **Theater** | Modelo creado, falta CRUD completo |
| **Screening** | Modelo creado, falta CRUD completo |
| **Ticket** | Modelo creado, falta CRUD completo |
| **Seat** | Modelo creado, falta CRUD completo |
| **Purchase** | Modelo creado, falta CRUD completo |
| **MerchandiseSale** | Modelo creado, falta CRUD completo |
| **Dashboard** | Controller y Service creados |
| **Email** | Service creado |

---

## 📌 Commit History por Rama

### **main**
- `17ebd42` Merge branch 'main'
- `bba2524` chore: initial project setup
- `97e2439` initial commit

### **feature/project-setup**
- `5a9b158` chore: fix dto annotations to compile correctly
- `e30c40b` chore: add model enums
- `4220c85` chore: add README
- `29b30e6` chore: add application.properties.example
- `3f9cbce` chore: protect credentials from git tracking
- `0aa339e` chore: add config structure
- `37bb466` chore: add controller structure
- `8643db5` chore: add service structure
- `d2329d0` chore: add exception structure
- `6297898` chore: add dto structure
- `fe6b59d` chore: add repository structure
- `538950c` chore: add model structure

### **feature/cloudinary-setup**
- `eaf93bb` chore: verify cloudinary setup compiles correctly
- `519696b` feat: verify and complete CloudinaryConfig
- `734ac12` feat: add CloudinaryService interface and implementation
- `a27ac98` chore: add cloudinary credentials to properties

### **feature/movie**
- `3c19d3e` Add Merchandise module with CRUD, categories enum and converter
- `554df40` Fix AgeRating enum mapping and add JSON support for movie creation
- `52c5d62` Feat: movie CRUD with Cloudinary complete

---

**Nota:** Los cambios actuales están en `feature/movie`. Para recuperar cambios stasheados: `git stash pop`
