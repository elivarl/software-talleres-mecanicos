# Taller360 Backend

Backend MVP para la gestion operativa de talleres mecanicos. Actualmente incluye autenticacion JWT, usuarios internos, clientes, vehiculos, ordenes de trabajo, inspecciones iniciales, cotizaciones con aprobacion publica, inventario, repuestos usados, mano de obra, quality control, entrega del vehiculo, historial completo del vehiculo y dashboard operativo.

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
- Modulos `auth`, `users`, `customers`, `vehicles`, `workorders`, `inspections`, `quotations`, `inventory`, `labor` y `dashboard`.
- Historial completo del vehiculo.
- Seed inicial de usuario ADMIN.

## Estructura base

```text
com.taller360.app
|-- auth
|-- customers
|-- dashboard
|-- inventory
|-- inspections
|-- labor
|-- quotations
|-- security
|-- shared
|-- users
|-- vehicles
`-- workorders
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
- `V12__create_inventory_items_table.sql`
- `V13__create_work_order_parts_table.sql`
- `V14__create_labor_items_table.sql`

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
- `PATCH /api/work-orders/{id}/quality-control`
- `PATCH /api/work-orders/{id}/mark-ready`
- `PATCH /api/work-orders/{id}/deliver`

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

Inventario:

- `GET /api/inventory`
- `GET /api/inventory?search={value}`
- `POST /api/inventory`
- `GET /api/inventory/{id}`
- `PUT /api/inventory/{id}`
- `PATCH /api/inventory/{id}/deactivate`
- `GET /api/inventory/low-stock`

Repuestos usados:

- `POST /api/work-orders/{id}/parts`
- `GET /api/work-orders/{id}/parts`
- `DELETE /api/work-orders/{id}/parts/{partId}`

Labor:

- `POST /api/work-orders/{id}/labor`
- `GET /api/work-orders/{id}/labor`
- `DELETE /api/work-orders/{id}/labor/{laborId}`

Dashboard:

- `GET /api/dashboard`

## Reglas principales implementadas

- Solo `ADMIN` puede administrar usuarios, inventario y dashboard.
- `ADMIN` y `RECEPTIONIST` pueden gestionar clientes, vehiculos, work orders e inspecciones.
- `ADMIN` y `RECEPTIONIST` pueden gestionar cotizaciones privadas.
- `ADMIN` y `MECHANIC` pueden registrar repuestos usados, mano de obra y quality control.
- `users.email`, `vehicles.plate` e `inventory_items.sku` son unicos.
- `customers.identification` es unico si se registra.
- Las contrasenas se almacenan con BCrypt y los usuarios inactivos no pueden autenticarse.
- `work_orders.code` se genera con formato `OT-000001`.
- `quotations.code` se genera con formato `COT-000001`.
- Una orden inicia en `RECEIVED`.
- `RECEIVED -> DIAGNOSIS` es valido.
- No se puede mover a `IN_PROGRESS` si la orden no esta `APPROVED`.
- No se puede mover a `READY` sin quality control completo.
- No se puede mover a `DELIVERED` si la orden no esta `READY`.
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
- Los repuestos usados solo pueden agregarse cuando la orden esta `APPROVED` o `IN_PROGRESS`.
- Al registrar un repuesto usado, el stock se descuenta automaticamente.
- Si no hay stock suficiente, el registro se rechaza.
- Un item inactivo no puede usarse en nuevos work orders.
- Al eliminar un repuesto usado antes de entregar, el stock se restaura.
- La mano de obra solo puede agregarse cuando la orden esta `APPROVED` o `IN_PROGRESS`.
- La mano de obra no puede eliminarse de una orden `DELIVERED`.
- El quality control solo puede completarse si la orden esta `IN_PROGRESS`.
- Una orden solo puede pasar a `READY` si esta `IN_PROGRESS` y `qualityControlCompleted` es `true`.
- Solo una orden en estado `READY` puede entregarse.
- Al entregar, se registran `deliveredAt`, `deliveredTo` y `finalMileage`.
- `finalMileage` debe ser mayor o igual a `currentMileage`.
- El historial del vehiculo se ordena de mas reciente a mas antiguo y no expone `internalNotes`.
- `GET /api/dashboard` es solo para `ADMIN`.
- `lowStockItems` incluye items donde `currentStock <= minStock`.

## Historial del vehiculo

Los endpoints `GET /api/vehicles/{id}/history` y `GET /api/vehicles/by-plate/{plate}/history` devuelven:

- Datos del vehiculo.
- Datos del cliente actual.
- Ordenes asociadas al vehiculo desde la mas reciente hasta la mas antigua.
- Diagnostico, estado final y fechas relevantes por orden.
- Inspeccion inicial si existe.
- Cotizacion si existe.
- Repuestos usados.
- Mano de obra.
- Totales basicos por orden.
- Kilometraje registrado y entrega si aplica.

## Dashboard operativo

El endpoint `GET /api/dashboard` devuelve:

- `totalWorkOrdersThisMonth`
- `workOrdersByStatus`
- `estimatedRevenueThisMonth`
- `pendingWorkOrders`
- `readyToDeliverWorkOrders`
- `lowStockItems`
- `deliveredWorkOrdersThisMonth`

Criterio de ingresos estimados del MVP:

- Se calcula como `used parts total + labor items`.
- Solo cuenta work orders cuya `receptionDate` pertenece al mes actual.
- Excluye work orders en estado `CANCELLED` y `REJECTED`.

Otros criterios del dashboard:

- `totalWorkOrdersThisMonth` usa `receptionDate` dentro del mes actual.
- `workOrdersByStatus` muestra el conteo actual agrupado por estado.
- `pendingWorkOrders` cuenta ordenes abiertas, es decir, todas excepto `DELIVERED`, `CANCELLED` y `REJECTED`.
- `readyToDeliverWorkOrders` cuenta ordenes con estado `READY`.
- `deliveredWorkOrdersThisMonth` cuenta ordenes `DELIVERED` con `deliveredAt` dentro del mes actual.

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
11. Crear items de inventario.
12. Registrar repuestos usados sobre una orden aprobada o en progreso.
13. Registrar items de mano de obra sobre una orden aprobada o en progreso.
14. Completar quality control.
15. Marcar la orden como `READY`.
16. Entregar el vehiculo y verificar estado `DELIVERED`.
17. Consultar historial completo por id o por placa.
18. Consultar el dashboard operativo.

## Fuera de alcance del MVP

- Frontend.
- Facturacion SRI real.
- Integraciones con WhatsApp o pagos online.
- Multitenancy.
- Subida real de imagenes.
- Microservicios, Kafka o RabbitMQ.
- Contabilidad completa.
- Reporteria compleja.
- Soporte multi-sucursal.
