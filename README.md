# Taller360 Backend

Backend MVP para la gestion operativa de talleres mecanicos. En el estado actual ya incluye autenticacion JWT, administracion de usuarios internos, clientes y vehiculos.

## Stack

- Java 21
- Spring Boot 3.5
- Gradle
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- Flyway
- MySQL
- JUnit 5

## Alcance actual

Implementado:

- Configuracion base del proyecto con Gradle.
- Estructura modular bajo `com.taller360.app`.
- Configuracion de MySQL.
- Migraciones con Flyway.
- Manejo global de errores.
- Seguridad JWT stateless.
- Modulo `auth`.
- Modulo `users`.
- Modulo `customers`.
- Modulo `vehicles`.
- Seed inicial de usuario ADMIN.
- Endpoints base de historial del vehiculo.

Todavia no implementado:

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
├── customers
├── security
├── shared
├── users
└── vehicles
```

Cada modulo sigue esta estructura:

- `application`: casos de uso y DTOs.
- `domain`: entidades y enums.
- `infrastructure`: repositorios.
- `web`: controladores.

## Requisitos

- Java 21
- Gradle 8+
- MySQL 8+

## Configuracion

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

Si prefieres crear la base manualmente:

```sql
CREATE DATABASE taller360 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Migraciones Flyway

Las migraciones viven en:

```text
src/main/resources/db/migration
```

Migraciones actuales:

- `V1__create_users_table.sql`
- `V2__insert_seed_admin_user.sql`
- `V3__create_customers_table.sql`
- `V4__create_vehicles_table.sql`

Flyway se ejecuta automaticamente al iniciar la aplicacion.

## Ejecucion

Iniciar la aplicacion:

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

## Endpoints disponibles

Autenticacion:

- `POST /api/auth/login`

Usuarios:

- `GET /api/users`
- `POST /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `PATCH /api/users/{id}/deactivate`

Clientes:

- `GET /api/customers`
- `GET /api/customers?search={value}`
- `POST /api/customers`
- `GET /api/customers/{id}`
- `PUT /api/customers/{id}`

Vehiculos:

- `GET /api/vehicles`
- `GET /api/vehicles?plate={value}`
- `POST /api/vehicles`
- `GET /api/vehicles/{id}`
- `PUT /api/vehicles/{id}`
- `GET /api/vehicles/{id}/history`
- `GET /api/vehicles/by-plate/{plate}/history`

## Reglas implementadas hasta ahora

- Solo `ADMIN` puede administrar usuarios.
- `ADMIN` y `RECEPTIONIST` pueden gestionar clientes y vehiculos.
- `users.email` es unico.
- `customers.identification` es unico si se registra.
- `vehicles.plate` es unico.
- Las contrasenas se almacenan con BCrypt.
- Los usuarios inactivos no pueden autenticarse.
- `customerId` es obligatorio al registrar un vehiculo.
- `mileage` debe ser mayor o igual a 0.
- `year` del vehiculo debe estar entre 1900 y el siguiente anio calendario.
- Los endpoints privados requieren JWT, salvo `POST /api/auth/login`.

## Historial base del vehiculo

Los endpoints de historial del vehiculo ya existen en esta fase y devuelven:

- Datos del vehiculo.
- Datos del cliente actual.
- Coleccion `workOrders`.

Por ahora `workOrders` retorna vacio hasta que se implemente el modulo `workorders`.

## Flujo basico de prueba

1. Iniciar la aplicacion.
2. Hacer login con el usuario ADMIN seed.
3. Usar el token JWT en `Authorization: Bearer <token>`.
4. Crear y consultar clientes.
5. Registrar y consultar vehiculos.
6. Consultar historial base por id o por placa.

## Fuera de alcance por ahora

- Frontend.
- Modulos operativos del taller.
- Integraciones externas.
- Multitenancy.
- Subida real de imagenes.
- Facturacion SRI.
