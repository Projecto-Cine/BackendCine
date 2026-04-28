# CineBackend

Backend REST para gestión de multicine.

## Stack
- Java 25
- Spring Boot 4.x
- MySQL
- Lombok
- Cloudinary
- Maven

## Setup
1. Clona el repositorio
2. Copia application.properties.example y renómbralo a application.properties
3. Rellena tus credenciales
4. Crea la base de datos en MySQL: CREATE DATABASE cine_db;
5. Ejecuta la aplicación desde DemoApplication.java

## Endpoints principales
| Método | Ruta                       | Descripción          |
|--------|----------------------------|----------------------|
| GET    | /api/users                 | Listar usuarios      |
| GET    | /api/movies                | Cartelera activa     |
| GET    | /api/screenings            | Listar sesiones      |
| GET    | /api/screenings/{id}/seats | Mapa de asientos     |
| POST   | /api/purchases             | Realizar compra      |
| GET    | /api/merchandise           | Listar productos     |
| GET    | /api/dashboard             | Dashboard dirección  |

## Ramas
- main → producción
- develop → integración
- feature/* → desarrollo de cada funcionalidad

## Autoras
- Ana Morandeira
- Maria Regueiro
