# Taller360 Backend

Backend MVP para la gestion operativa de talleres mecanicos. En el estado actual ya incluye autenticacion JWT, administracion de usuarios internos, clientes, vehiculos, ordenes de trabajo, inspecciones iniciales y cotizaciones con flujo publico de aprobacion o rechazo.

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
- Modulo `workorders`.
- Modulo `inspections`.
- Modulo `quotations`.
- Seed inicial de usuario ADMIN.
- Endpoints base de historial del vehiculo.

Todavia no implementado:

- Inventory
- Labor
- Dashboard

## Estructura base

```text
com.taller360.app
├── auth
├── customers
├── inspections
├── quotations
├── security
├── shared
├── users
├── vehicles
└── workorders
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
- `APP_TAX_RATE`
- `JWT_SECRET`
- `JWT_EXPIRATION_SECONDS`

Valores por defecto locales:

```yaml
DB_URL=jdbc:mysql://localhost:3306/taller360?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Guayaquil
DB_USERNAME=root
DB_PASSWORD=root
APP_TAX_RATE=0.15
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
- `V5__create_work_order_sequences_table.sql`
- `V6__create_work_orders_table.sql`
- `V7__create_reception_inspections_table.sql`
- `V8__create_inspection_photos_table.sql`
- `V9__create_quotation_sequences_table.sql`
- `V10__create_quotations_table.sql`
- `V11__create_quotation_items_table.sql`

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

Ordenes de trabajo:

- `GET /api/work-orders`
- `GET /api/work-orders?status={status}&plate={plate}&customerId={customerId}&receptionDateFrom={yyyy-MM-dd}&receptionDateTo={yyyy-MM-dd}`
- `POST /api/work-orders`
- `GET /api/work-orders/{id}`
- `PATCH /api/work-orders/{id}/assign-mechanic`
- `PATCH /api/work-orders/{id}/status`
- `PATCH /api/work-orders/{id}/diagnosis`
- `PATCH /api/work-orders/{id}/internal-notes`

Inspecciones:

- `POST /api/work-orders/{id}/inspection`
- `GET /api/work-orders/{id}/inspection`
- `PUT /api/inspections/{id}`
- `POST /api/inspections/{id}/photos`

Cotizaciones privadas:

- `POST /api/work-orders/{id}/quotation`
- `GET /api/quotations/{id}`
- `PUT /api/quotations/{id}`
- `POST /api/quotations/{id}/items`
- `PUT /api/quotations/{id}/items/{itemId}`
- `DELETE /api/quotations/{id}/items/{itemId}`
- `POST /api/quotations/{id}/send`

Cotizaciones publicas:

- `GET /api/public/quotations/{token}`
- `POST /api/public/quotations/{token}/approve`
- `POST /api/public/quotations/{token}/reject`

## Reglas implementadas hasta ahora

- Solo `ADMIN` puede administrar usuarios.
- `ADMIN` y `RECEPTIONIST` pueden gestionar clientes y vehiculos.
- `ADMIN` y `RECEPTIONIST` pueden crear ordenes, asignar mecanicos y registrar inspecciones.
- `ADMIN` y `RECEPTIONIST` pueden crear y gestionar cotizaciones.
- `MECHANIC` puede consultar ordenes y registrar diagnostico o notas internas.
- `users.email` es unico.
- `customers.identification` es unico si se registra.
- `vehicles.plate` es unico.
- `work_orders.code` se genera automaticamente con formato `OT-000001`.
- `quotations.code` se genera automaticamente con formato `COT-000001`.
- Las contrasenas se almacenan con BCrypt.
- Los usuarios inactivos no pueden autenticarse.
- `customerId` es obligatorio al registrar un vehiculo.
- `mileage` debe ser mayor o igual a 0.
- `year` del vehiculo debe estar entre 1900 y el siguiente anio calendario.
- Una orden inicia en `RECEIVED`.
- `RECEIVED -> DIAGNOSIS` es valido.
- No se puede mover a `APPROVED`, `REJECTED` o `QUOTED` hasta implementar quotations.
- No se puede mover a `IN_PROGRESS` si no esta `APPROVED`.
- No se puede mover a `READY` sin control de calidad completo.
- No se puede mover a `DELIVERED` si no esta `READY`.
- Una orden `CANCELLED` no puede moverse a otro estado.
- Una orden `DELIVERED` o `CANCELLED` no permite modificaciones importantes.
- Una orden solo puede tener una inspeccion inicial.
- La inspeccion no puede modificarse si la orden esta `DELIVERED` o `CANCELLED`.
- Solo cotizaciones `DRAFT` pueden modificarse.
- No se puede enviar una cotizacion sin items.
- Al enviar una cotizacion, la orden cambia a `QUOTED`.
- Al aprobar una cotizacion, la orden cambia a `APPROVED`.
- Al rechazar una cotizacion, la orden cambia a `REJECTED`.
- `subtotal`, `tax` y `total` se calculan con `app.tax-rate`.
- El `publicToken` se genera al enviar la cotizacion.
- Los endpoints privados requieren JWT, salvo `POST /api/auth/login`.
- Los endpoints publicos de cotizaciones no requieren JWT.

## Historial base del vehiculo

Los endpoints de historial del vehiculo ya existen en esta fase y devuelven:

- Datos del vehiculo.
- Datos del cliente actual.
- Coleccion `workOrders`.

Ahora `workOrders` devuelve un resumen basico con id, codigo, fecha de recepcion y estado cuando existen ordenes asociadas.

## Flujo basico de prueba

1. Iniciar la aplicacion.
2. Hacer login con el usuario ADMIN seed.
3. Usar el token JWT en `Authorization: Bearer <token>`.
4. Crear y consultar clientes.
5. Registrar y consultar vehiculos.
6. Crear una orden de trabajo.
7. Registrar diagnostico.
8. Registrar inspeccion inicial y fotos simuladas por URL.
9. Crear cotizacion, agregar items y enviarla.
10. Consultar, aprobar o rechazar la cotizacion mediante el endpoint publico.
11. Consultar historial base por id o por placa.

## Fuera de alcance por ahora

- Frontend.
- Modulos operativos del taller.
- Integraciones externas.
- Multitenancy.
- Subida real de imagenes.
- Facturacion SRI.
