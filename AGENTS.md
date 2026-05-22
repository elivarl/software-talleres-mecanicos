# Taller360 Frontend - Angular Instructions

## Frontend Context

The frontend is an Angular application for Taller360, a SaaS system for mechanical workshops.

The frontend must consume the existing Spring Boot backend API.

The UI must follow the real workshop flow:

Login
→ Dashboard
→ Customers
→ Vehicles
→ Work Orders
→ Initial Inspection
→ Diagnosis
→ Quotations
→ Public Quotation Approval
→ Used Parts
→ Labor
→ Quality Control
→ Mark Ready
→ Delivery
→ Vehicle History

The frontend must be simple, clear and usable by non-technical workshop users.

The product is intended for Spanish-speaking users in Ecuador and LATAM.

## Frontend Language Rules

Use English for:

- Angular file names.
- Component class names.
- Service names.
- Interfaces.
- Types.
- Variables.
- Routes.
- Guards.
- Interceptors.

Use Spanish for:

- UI labels.
- Buttons.
- Form labels.
- Validation messages.
- User-facing errors.
- Menu labels.
- Page titles.

Correct examples:

Code:
- WorkOrdersComponent
- QuotationService
- VehicleHistoryComponent
- AuthInterceptor
- WorkOrderStatus

UI:
- Órdenes de trabajo
- Crear cliente
- Registrar diagnóstico
- Enviar cotización
- Aprobar cotización
- Marcar como listo
- Entregar vehículo

## Frontend Tech Stack

Use:

- Angular 18 or higher
- TypeScript
- Angular Router
- Reactive Forms
- HttpClient
- Guards
- Interceptors
- Bootstrap or Tailwind CSS

Choose one CSS framework and keep it consistent.

Prefer a simple, clean admin dashboard layout.

## Frontend Architecture

Recommended structure:

src/app
- core
  - auth
  - interceptors
  - guards
  - models
  - services
- shared
  - components
  - pipes
  - utils
- layout
  - main-layout
  - sidebar
  - navbar
- features
  - auth
  - dashboard
  - customers
  - vehicles
  - work-orders
  - inspections
  - quotations
  - inventory
  - labor
  - public-quotation
  - vehicle-history

Use feature-based organization.

Use services to communicate with the backend.

Do not call HttpClient directly from components except in very small cases.

Components should coordinate UI state, not business logic.

Use interfaces for API request and response contracts.

Use route guards for protected pages.

Use an HTTP interceptor to attach JWT token.

Use an HTTP interceptor or centralized service for error handling if appropriate.

## UX Rules

The UI must be simple and practical.

Prioritize:

- Clear tables.
- Simple forms.
- Obvious actions.
- Status badges.
- Confirmation dialogs for destructive actions.
- Loading states.
- Error messages.
- Empty states.
- Success messages.

Avoid:

- Overloaded screens.
- Complex dashboards.
- Too many nested modals.
- Unnecessary animations.
- Complex state management unless needed.

Do not use NgRx unless explicitly requested.

## Authentication

Implement:

- Login page.
- JWT storage.
- Auth service.
- Auth guard.
- Role guard if useful.
- Logout.
- User session handling.
- Token attached to private API requests.

Public quotation routes must not require authentication.

## Main Private Pages

Implement:

- Login
- Main layout
- Dashboard
- Customers list
- Customer form
- Customer detail
- Vehicles list
- Vehicle form
- Vehicle detail
- Vehicle history
- Work orders list
- Work order creation
- Work order detail
- Initial inspection section
- Diagnosis section
- Quotation section
- Inventory list
- Inventory form
- Used parts section inside work order detail
- Labor section inside work order detail
- Quality control section
- Delivery section

## Public Pages

Implement:

- Public quotation page by token
- Approve quotation
- Reject quotation
- Public quotation result state

The public quotation page must be professional and trustworthy.

It should show:

- Workshop/product name
- Customer data if returned by backend
- Vehicle data
- Work order information
- Diagnosis
- Quotation items
- Subtotal
- Tax
- Total
- Status
- Approve button
- Reject button

## Work Order UI Flow

Work order detail is the central screen.

It should show:

- Work order code
- Current status
- Customer summary
- Vehicle summary
- Complaint
- Initial inspection
- Diagnosis
- Quotation
- Used parts
- Labor
- Quality control
- Delivery information

Actions must depend on current status.

Examples:

- RECEIVED: allow inspection and start diagnosis.
- DIAGNOSIS: allow diagnosis and quotation creation.
- QUOTED: show quotation status.
- APPROVED: allow start repair / move to IN_PROGRESS.
- IN_PROGRESS: allow used parts, labor and quality control.
- READY: allow vehicle delivery.
- DELIVERED: read-only important fields.
- CANCELLED / REJECTED: limited actions.

## API Integration

The frontend must use the backend endpoints defined in AGENTS.md and README.

If endpoint names or DTO fields differ from the expected contract, inspect the backend code and adapt the frontend to the actual implementation.

Do not invent backend endpoints unless explicitly requested.

If an endpoint is missing, document it clearly instead of silently creating fake frontend logic.

## Error Handling

Show clear Spanish messages for users.

Examples:

- No se pudo cargar la información.
- Cliente creado correctamente.
- La orden no puede avanzar a este estado.
- Stock insuficiente.
- La cotización ya fue aprobada o rechazada.
- Sesión expirada. Inicia sesión nuevamente.

## Out of Scope for Frontend MVP

Do not implement:

- Online payments.
- Real WhatsApp integration.
- Real SRI invoicing.
- Real image upload.
- Advanced charts.
- Complex role matrix UI.
- Offline mode.
- Native mobile app.
- Push notifications.

## PrimeNG UI Rules

Use PrimeNG as the main UI component library.

Use:

- PrimeNG components
- PrimeIcons
- PrimeFlex if useful
- Angular Reactive Forms
- Angular Router
- HttpClient
- Guards
- Interceptors

Do not use Bootstrap or Tailwind unless explicitly requested.

The frontend must use a custom dashboard template built inside the project.

Do not use a downloaded admin template.

The template must include:

- MainLayoutComponent
- SidebarComponent
- TopbarComponent
- BreadcrumbComponent if useful
- PageHeaderComponent
- StatCardComponent
- ConfirmDialog integration
- Toast notifications
- Loading indicators
- Empty state component if useful

Use PrimeNG components such as:

- p-table
- p-card
- p-button
- p-inputText
- p-inputNumber
- p-dropdown or p-select depending on installed PrimeNG version
- p-dialog
- p-confirmDialog
- p-toast
- p-tag
- p-badge
- p-panel
- p-toolbar
- p-menu or p-panelMenu
- p-calendar or DatePicker depending on installed PrimeNG version
- p-textarea or textarea with PrimeNG styling depending on installed version

Important:
Before using a PrimeNG component, verify the installed PrimeNG version and use component names/imports compatible with that version.

The custom dashboard must be clean, responsive and business-oriented.

The UI must be in Spanish.

Code names must remain in English.

Use status badges for work order statuses.

Use confirmation dialogs for critical actions:

- Delete item.
- Send quotation.
- Approve quotation.
- Reject quotation.
- Mark work order as READY.
- Deliver vehicle.
- Deactivate inventory item.

Use toast messages for success and error feedback.

The application must have a clear sidebar navigation:

- Dashboard
- Clientes
- Vehículos
- Órdenes de trabajo
- Inventario
- Cotizaciones if needed
- Historial if needed

The WorkOrderDetail screen is the central operational page and must use PrimeNG cards, panels, tags, tables and forms to organize the workshop flow.