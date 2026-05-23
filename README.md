# Taller360 Frontend

Frontend Angular + PrimeNG para Taller360, orientado al flujo operativo real de un taller mecánico.

## Stack

- Angular 20 standalone
- TypeScript
- Angular Router
- Reactive Forms
- HttpClient
- PrimeNG 20
- PrimeIcons
- Tema PrimeNG Aura

No se usa Bootstrap ni Tailwind.

## Requisitos

- Node.js 20 o superior
- npm 10 o superior
- Backend Taller360 ejecutándose localmente

## Instalación

```bash
npm install
```

## Configuración del backend

La URL base del backend se configura en:

- `src/environments/environment.development.ts`
- `src/environments/environment.ts`

Valor actual:

```ts
backendApiUrl: 'http://localhost:8080/api'
```

Si el backend corre en otra URL o puerto, actualiza ambos archivos.

## Ejecutar la app

Modo desarrollo:

```bash
npm start
```

Build de verificación:

```bash
npm run build
```

La app queda disponible en:

```text
http://localhost:4200
```

## Librería UI

La interfaz usa PrimeNG como librería principal:

- `p-table`
- `p-card`
- `p-button`
- `p-select`
- `p-inputText`
- `p-inputNumber`
- `p-datepicker`
- `p-panel`
- `p-tag`
- `p-toolbar`
- `p-toast`
- `p-confirmDialog`

La plantilla administrativa es propia del proyecto y está separada en:

- `src/app/layout/main-layout`
- `src/app/layout/sidebar`
- `src/app/layout/topbar`
- `src/app/shared/components`

## Arquitectura

Estructura principal:

```text
src/app
├─ core
│  ├─ auth
│  ├─ guards
│  ├─ interceptors
│  ├─ models
│  └─ services
├─ features
│  ├─ auth
│  ├─ customers
│  ├─ dashboard
│  ├─ inventory
│  ├─ labor
│  ├─ public-quotation
│  ├─ quotations
│  ├─ vehicle-history
│  ├─ vehicles
│  └─ work-orders
├─ layout
└─ shared
```

## Rutas principales

Privadas:

- `/dashboard`
- `/customers`
- `/customers/new`
- `/customers/:id`
- `/customers/:id/edit`
- `/vehicles`
- `/vehicles/new`
- `/vehicles/:id`
- `/vehicles/:id/edit`
- `/vehicles/:id/history`
- `/work-orders`
- `/work-orders/new`
- `/work-orders/:id`
- `/inventory`
- `/inventory/new`
- `/inventory/:id/edit`

Públicas:

- `/login`
- `/public/quotations/:token`

## Autenticación

Implementado:

- Login contra `POST /api/auth/login`
- JWT en `localStorage`
- `AuthInterceptor` para adjuntar token
- `AuthGuard` para rutas privadas
- Logout
- Redirección al login cuando expira la sesión

La ruta pública de cotización no usa JWT.

## Credenciales seed del backend

Según el contrato en `openapi.yaml`, el backend expone estas credenciales de ejemplo:

```text
Correo: admin@taller360.com
Clave: Admin12345*
```

Si tu backend local usa otra data seed, usa las credenciales reales de ese entorno.

## Flujo MVP para pruebas

El frontend quedó preparado para probar este flujo:

1. Iniciar sesión.
2. Crear cliente.
3. Crear vehículo.
4. Crear orden de trabajo.
5. Registrar inspección inicial.
6. Agregar fotos simuladas por URL.
7. Cambiar la orden a `DIAGNOSIS`.
8. Registrar diagnóstico técnico.
9. Crear cotización.
10. Agregar items de cotización.
11. Enviar cotización.
12. Abrir el enlace público.
13. Aprobar o rechazar cotización.
14. Verificar la orden en `APPROVED`.
15. Cambiar la orden a `IN_PROGRESS`.
16. Registrar repuestos usados.
17. Registrar mano de obra.
18. Completar control de calidad.
19. Marcar la orden como `READY`.
20. Entregar el vehículo.
21. Consultar historial del vehículo.
22. Consultar dashboard operativo.

## Notas operativas del MVP

- `WorkOrderDetail` es la pantalla central del taller.
- Las acciones visibles dependen del estado real de la orden.
- Después de `DELIVERED`, las secciones relevantes quedan mayormente en solo lectura.
- La aprobación pública de cotización funciona sin autenticación.
- El dashboard usa el endpoint real `GET /api/dashboard`.
- El historial usa `GET /api/vehicles/{id}/history` y búsqueda por placa con `GET /api/vehicles/by-plate/{plate}/history`.

## Limitaciones conocidas por contrato backend

- El backend no expone un `GET` de cotización por `workOrderId`; la vista privada reutiliza `quotationId` cuando ya existe una cotización creada.
- El catálogo de inventario está restringido por rol en backend; esto puede limitar el alta de repuestos usados para ciertos perfiles.
- No se implementan pagos, WhatsApp real, carga real de imágenes, facturación SRI, dashboard avanzado ni notificaciones push.

## Alcance del MVP

Incluido:

- Autenticación JWT
- Dashboard operativo
- Clientes
- Vehículos
- Órdenes de trabajo
- Inspección inicial
- Diagnóstico
- Cotizaciones
- Aprobación pública
- Inventario
- Repuestos usados
- Mano de obra
- Control de calidad
- Entrega
- Historial del vehículo

Fuera de alcance:

- Pagos en línea
- WhatsApp real
- Facturación electrónica
- Carga real de archivos
- Gráficos avanzados
- App móvil nativa
- Offline mode

## Comandos útiles

```bash
npm install
npm start
npm run build
npm test
```
