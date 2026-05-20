# Taller360 Backend

Backend MVP para la gestion operativa de talleres mecanicos en Ecuador. Cubre el flujo completo desde la recepcion del vehiculo hasta su entrega, incluyendo inspeccion inicial, diagnostico, cotizacion con aprobacion publica, ejecucion del trabajo, repuestos usados, mano de obra, quality control, historial del vehiculo y dashboard operativo.

## Stack tecnico

- Java 21
- Spring Boot 3.5
- Gradle
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security con JWT
- Flyway
- MySQL
- JUnit 5
- Mockito

## Requisitos

- Java 21
- Gradle 8+
- MySQL 8+

## Estructura

Base package: `com.taller360.app`

Modulos implementados:

- `auth`
- `users`
- `customers`
- `vehicles`
- `workorders`
- `inspections`
- `quotations`
- `inventory`
- `labor`
- `dashboard`
- `security`
- `shared`

Cada modulo usa la estructura:

- `domain`
- `application`
- `infrastructure`
- `web`

## Configuracion de base de datos

Crear la base si no quieres usar `createDatabaseIfNotExist=true`:

```sql
CREATE DATABASE taller360 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Variables y properties

`application.yaml` soporta:

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
JWT_SECRET=Taller360JwtSecretKeyForDevelopmentOnly1234567890
JWT_EXPIRATION_SECONDS=3600
```

## Migraciones Flyway

Las migraciones viven en `src/main/resources/db/migration` y se ejecutan automaticamente al iniciar la aplicacion.

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

## Como ejecutar

Levantar la aplicacion:

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

## Endpoints principales

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
- `POST /api/customers`
- `GET /api/customers/{id}`
- `PUT /api/customers/{id}`

Vehiculos:

- `GET /api/vehicles`
- `POST /api/vehicles`
- `GET /api/vehicles/{id}`
- `PUT /api/vehicles/{id}`
- `GET /api/vehicles/{id}/history`
- `GET /api/vehicles/by-plate/{plate}/history`

Ordenes de trabajo:

- `GET /api/work-orders`
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
- `POST /api/inventory`
- `GET /api/inventory/{id}`
- `PUT /api/inventory/{id}`
- `PATCH /api/inventory/{id}/deactivate`
- `GET /api/inventory/low-stock`

Repuestos usados:

- `POST /api/work-orders/{id}/parts`
- `GET /api/work-orders/{id}/parts`
- `DELETE /api/work-orders/{id}/parts/{partId}`

Mano de obra:

- `POST /api/work-orders/{id}/labor`
- `GET /api/work-orders/{id}/labor`
- `DELETE /api/work-orders/{id}/labor/{laborId}`

Dashboard:

- `GET /api/dashboard`

## Reglas de negocio principales

- Los endpoints privados requieren JWT.
- Los endpoints publicos de cotizaciones no requieren autenticacion.
- Solo `ADMIN` puede administrar usuarios, inventario y dashboard.
- Solo `ADMIN` y `RECEPTIONIST` pueden gestionar clientes, vehiculos, work orders, inspecciones y cotizaciones privadas.
- Solo `ADMIN` y `MECHANIC` pueden registrar repuestos usados, mano de obra y quality control.
- Las contrasenas se almacenan con BCrypt.
- Los usuarios inactivos no pueden autenticarse.
- `work_orders.code` se genera como `OT-000001`.
- `quotations.code` se genera como `COT-000001`.
- Una orden inicia en `RECEIVED`.
- `RECEIVED -> DIAGNOSIS -> QUOTED -> APPROVED -> IN_PROGRESS -> READY -> DELIVERED`.
- `QUOTED -> REJECTED`.
- `RECEIVED`, `DIAGNOSIS` o `QUOTED` pueden pasar a `CANCELLED`.
- Una orden `DELIVERED` o `CANCELLED` bloquea modificaciones importantes.
- Una cotizacion solo puede modificarse si esta en `DRAFT`.
- No se puede enviar una cotizacion sin items.
- Al enviar una cotizacion, la orden pasa a `QUOTED`.
- Al aprobar una cotizacion, la orden pasa a `APPROVED`.
- Al rechazar una cotizacion, la orden pasa a `REJECTED`.
- Los repuestos usados solo pueden agregarse cuando la orden esta `APPROVED` o `IN_PROGRESS`.
- Al registrar un repuesto usado se descuenta stock.
- Si se elimina un repuesto usado antes de entregar, el stock se restaura.
- La mano de obra solo puede agregarse cuando la orden esta `APPROVED` o `IN_PROGRESS`.
- `qualityControlCompleted` debe ser `true` para marcar una orden como `READY`.
- Solo una orden en `READY` puede entregarse.
- `finalMileage` debe ser mayor o igual a `currentMileage`.
- El historial del vehiculo no expone `internalNotes`.
- `lowStockItems` son items con `currentStock <= minStock`.
- `estimatedRevenueThisMonth` se calcula como `used parts total + labor items` de work orders del mes actual, excluyendo `CANCELLED` y `REJECTED`.

## Flujo completo de prueba

Todas las requests privadas deben incluir:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

### 1. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@taller360.com",
    "password": "Admin12345*"
  }'
```

Guardar `accessToken`.

### 2. Crear cliente

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Juan Perez",
    "identification": "0912345678",
    "phone": "0999999999",
    "email": "juan@example.com",
    "address": "Guayaquil"
  }'
```

### 3. Crear vehiculo

```bash
curl -X POST http://localhost:8080/api/vehicles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "plate": "ABC-1234",
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2020,
    "color": "Plata",
    "vin": "JTDBR32E720123456",
    "mileage": 85000
  }'
```

### 4. Crear orden de trabajo

```bash
curl -X POST http://localhost:8080/api/work-orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "vehicleId": 1,
    "receptionDate": "2026-05-20T09:00:00",
    "estimatedDeliveryDate": "2026-05-23",
    "currentMileage": 85000,
    "fuelLevel": "Half",
    "customerComplaint": "Ruido al frenar",
    "initialObservations": "Vehiculo ingresa con rayones leves"
  }'
```

### 5. Registrar inspeccion inicial

```bash
curl -X POST http://localhost:8080/api/work-orders/1/inspection \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "mileage": 85000,
    "fuelLevel": "Half",
    "exteriorCondition": "Good",
    "visibleScratches": "Leves rayones en parachoque",
    "visibleDents": null,
    "lightsWorking": true,
    "tiresCondition": "Good",
    "mirrorsCondition": "Good",
    "hasSpareTire": true,
    "hasJack": true,
    "hasTools": true,
    "hasDocuments": true,
    "personalItemsNotes": "Radio desmontable",
    "generalNotes": "Sin novedades mayores"
  }'
```

### 6. Agregar fotos simuladas

```bash
curl -X POST http://localhost:8080/api/inspections/1/photos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "photoUrl": "https://example.com/photos/front-view.jpg",
    "description": "Vista frontal"
  }'
```

### 7. Cambiar la orden a DIAGNOSIS

```bash
curl -X PATCH http://localhost:8080/api/work-orders/1/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status":"DIAGNOSIS"}'
```

### 8. Registrar diagnostico

```bash
curl -X PATCH http://localhost:8080/api/work-orders/1/diagnosis \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "diagnosis": "Desgaste avanzado en pastillas de freno delanteras"
  }'
```

### 9. Crear cotizacion

```bash
curl -X POST http://localhost:8080/api/work-orders/1/quotation \
  -H "Authorization: Bearer $TOKEN"
```

### 10. Agregar items a la cotizacion

```bash
curl -X POST http://localhost:8080/api/quotations/1/items \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "PART",
    "description": "Pastillas de freno",
    "quantity": 1,
    "unitPrice": 45.00
  }'
```

```bash
curl -X POST http://localhost:8080/api/quotations/1/items \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "LABOR",
    "description": "Cambio de pastillas",
    "quantity": 1,
    "unitPrice": 25.00
  }'
```

### 11. Enviar cotizacion

```bash
curl -X POST http://localhost:8080/api/quotations/1/send \
  -H "Authorization: Bearer $TOKEN"
```

Guardar `publicToken`.

### 12. Abrir endpoint publico

```bash
curl http://localhost:8080/api/public/quotations/<publicToken>
```

### 13. Aprobar cotizacion

```bash
curl -X POST http://localhost:8080/api/public/quotations/<publicToken>/approve
```

### 14. Verificar orden APPROVED

```bash
curl http://localhost:8080/api/work-orders/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 15. Cambiar a IN_PROGRESS

```bash
curl -X PATCH http://localhost:8080/api/work-orders/1/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_PROGRESS"}'
```

### 16. Registrar repuestos usados

Primero crear inventario:

```bash
curl -X POST http://localhost:8080/api/inventory \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pastillas de freno delanteras",
    "sku": "PAD-001",
    "description": "Juego delantero",
    "currentStock": 10,
    "minStock": 2,
    "unitCost": 20,
    "salePrice": 45,
    "active": true
  }'
```

Luego registrar el uso:

```bash
curl -X POST http://localhost:8080/api/work-orders/1/parts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "inventoryItemId": 1,
    "quantity": 1
  }'
```

### 17. Registrar mano de obra

```bash
curl -X POST http://localhost:8080/api/work-orders/1/labor \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Cambio de pastillas y limpieza",
    "price": 25
  }'
```

### 18. Completar quality control

```bash
curl -X PATCH http://localhost:8080/api/work-orders/1/quality-control \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "completed": true,
    "notes": "Prueba de ruta y frenado correcto"
  }'
```

### 19. Marcar READY

```bash
curl -X PATCH http://localhost:8080/api/work-orders/1/mark-ready \
  -H "Authorization: Bearer $TOKEN"
```

### 20. Entregar vehiculo

```bash
curl -X PATCH http://localhost:8080/api/work-orders/1/deliver \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "deliveredTo": "Juan Perez",
    "finalMileage": 85010
  }'
```

### 21. Consultar historial del vehiculo

```bash
curl http://localhost:8080/api/vehicles/1/history \
  -H "Authorization: Bearer $TOKEN"
```

O por placa:

```bash
curl http://localhost:8080/api/vehicles/by-plate/ABC-1234/history \
  -H "Authorization: Bearer $TOKEN"
```

### 22. Consultar dashboard

```bash
curl http://localhost:8080/api/dashboard \
  -H "Authorization: Bearer $TOKEN"
```

## Funcionalidades fuera del MVP

- Frontend.
- Facturacion SRI real.
- API real de WhatsApp.
- Pagos online.
- Multitenancy.
- Microservicios.
- Kafka o RabbitMQ.
- Subida real de imagenes.
- Dashboard avanzado o reporteria compleja.
- Contabilidad completa.
- Garantias avanzadas.
- Comisiones de mecanicos.
