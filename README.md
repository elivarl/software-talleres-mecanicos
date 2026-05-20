# Taller360 Backend

Backend MVP para la gestión operativa de talleres mecánicos. Esta primera fase implementa autenticación JWT, administración de usuarios internos y la base técnica para continuar con los módulos del flujo operativo.

## Stack

- Java 21
- Spring Boot 3.5
- Gradle
- Spring Web
- Spring Data JPA
- Spring Security
- Flyway
- MySQL
- JUnit 5

## Alcance actual de la fase 1

Incluye:

- Configuración base del proyecto con Gradle.
- Estructura modular inicial bajo `com.taller360.app`.
- Configuración de MySQL.
- Migraciones con Flyway.
- Manejo global de errores.
- Seguridad JWT stateless.
- Módulo `auth`.
- Módulo `users`.
- Seed inicial de usuario ADMIN.

Todavía no incluye:

- Customers
- Vehicles
- Work orders
- Inspections
- Quotations
- Inventory
- Labor
- Dashboard

## Estructura base

```text
com.taller360.app
├── auth
├── security
├── shared
└── users
```

Cada módulo sigue una estructura pragmática:

- `application`: casos de uso y DTOs.
- `domain`: entidades y enums.
- `infrastructure`: repositorios.
- `web`: controladores.

## Requisitos

- Java 21
- Gradle 8+
- MySQL 8+

## Configuración

Variables soportadas por `application.yaml`:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_SECONDS`

Valores por defecto locales:

```yaml
DB_URL=jdbc:mysql://localhost:3306/taller360?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Guayaquil
DB_USERNAME=root
DB_PASSWORD=root
JWT_EXPIRATION_SECONDS=3600
```

## Base de datos

Crear la base si prefieres no usar `createDatabaseIfNotExist=true`:

```sql
CREATE DATABASE taller360 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Migraciones Flyway

Las migraciones viven en:

```text
src/main/resources/db/migration
```

Se ejecutan automáticamente al iniciar la aplicación.

Migraciones actuales:

- `V1__create_users_table.sql`
- `V2__insert_seed_admin_user.sql`

## Ejecución

Iniciar la aplicación:

```bash
./gradlew bootRun
```

Ejecutar pruebas:

```bash
./gradlew test
```

## Usuario seed

- Email: `admin@taller360.com`
- Password: `Admin12345*`
- Role: `ADMIN`

## Endpoints disponibles en esta fase

Autenticación:

- `POST /api/auth/login`

Usuarios:

- `GET /api/users`
- `POST /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `PATCH /api/users/{id}/deactivate`

## Flujo de prueba básico

1. Iniciar la aplicación.
2. Hacer login con el usuario ADMIN seed.
3. Usar el token JWT en `Authorization: Bearer <token>`.
4. Crear, listar, consultar, actualizar y desactivar usuarios internos.

## Reglas de negocio implementadas en esta fase

- Solo `ADMIN` puede administrar usuarios.
- `users.email` es único.
- Las contraseñas se almacenan con BCrypt.
- Los usuarios inactivos no deben autenticarse.
- Los endpoints privados requieren JWT.

## Fuera de alcance por ahora

- Frontend.
- Módulos operativos del taller.
- Integraciones externas.
- Multitenancy.
- Subida real de imágenes.
- Facturación SRI.
