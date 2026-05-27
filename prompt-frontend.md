Continúa con la fase frontend 1C siguiendo estrictamente AGENTS.md y el backend existente de acuerdo al contrato que esta en el archivo openapi.yaml.

Implementa autenticación e intégrala con la plantilla PrimeNG.

Incluye:

1. AuthService.
2. AuthInterceptor para adjuntar JWT.
3. AuthGuard.
4. LoginComponent usando PrimeNG.
5. Logout.
6. Manejo de sesión en localStorage o sessionStorage.
7. Modelos/interfaces:
   - User
   - LoginRequest
   - LoginResponse
8. Integrar usuario autenticado en TopbarComponent.
9. Proteger rutas privadas con AuthGuard.
10. Dejar ruta pública de login fuera del MainLayoutComponent.
11. Redireccionar al dashboard después de login exitoso.
12. Mostrar errores de login usando p-toast o mensaje visual.
13. Manejar expiración o token inválido de forma básica.
14. Confirmar que public quotation routes puedan existir sin AuthGuard más adelante.

Usa PrimeNG:

- p-card para contenedor de login.
- p-inputText para email.
- p-password para password si está disponible y es compatible.
- p-button para ingresar.
- p-toast para errores.
- p-progressSpinner o loading visual si aplica.

Reglas:

- Respeta el endpoint real POST /api/auth/login.
- Revisa el DTO real del backend para LoginRequest y LoginResponse.
- No inventes campos si el backend no los retorna.
- No implementes todavía customers, vehicles, workorders ni otros módulos.
- UI en español.
- Código en inglés.
- Mantén el diseño del login coherente con Taller360.

Antes de modificar archivos, revisa los endpoints reales de auth en el backend.

***NOTA: se debe añadir cors al backend

***
Continúa con la fase frontend 2 siguiendo estrictamente AGENTS.md y el backend existente de acuerdo al contrato que esta en el archivo openapi.yaml.

Implementa únicamente customers y vehicles usando PrimeNG y la plantilla propia ya creada.

Incluye:

1. CustomerService.
2. VehicleService.
3. Interfaces/Models necesarios.
4. CustomersListComponent usando p-table.
5. CustomerFormComponent usando Reactive Forms y PrimeNG inputs.
6. CustomerDetailComponent si aplica.
7. VehiclesListComponent usando p-table.
8. VehicleFormComponent usando Reactive Forms y PrimeNG inputs.
9. VehicleDetailComponent si aplica.
10. Búsqueda de clientes por nombre, identificación o teléfono si el backend lo permite.
11. Búsqueda de vehículos por placa si el backend lo permite.
12. Botones de crear, editar y ver detalle usando p-button.
13. ConfirmDialog si existe alguna acción crítica.
14. Toast messages para éxito y error.
15. Validaciones visuales en español.
16. Rutas protegidas dentro del MainLayoutComponent.
17. Opciones de navegación en SidebarComponent.

Usa PrimeNG:

- p-table
- p-card
- p-toolbar
- p-button
- p-inputText
- p-inputNumber si aplica
- p-dialog si decides usar modal
- p-toast
- p-confirmDialog si aplica
- p-tag si aplica

Reglas:

- Respeta los endpoints reales del backend.
- No inventes endpoints.
- Si un filtro no existe en backend, implementa búsqueda local simple solo si los datos ya fueron cargados, o documenta la limitación.
- No implementes todavía workorders, inspections, quotations, inventory, labor, delivery, vehicle history ni dashboard real.
- UI en español.
- Código en inglés.

Antes de modificar archivos, revisa los DTOs y endpoints reales del backend para customers y vehicles.

***
Continúa con la fase frontend 3 siguiendo estrictamente AGENTS.md y el backend existente de acuerdo al contrato que esta en el archivo openapi.yaml.

Implementa únicamente work orders, initial inspections y diagnosis usando PrimeNG.

Incluye:

1. WorkOrderService.
2. InspectionService si corresponde.
3. Interfaces/Models necesarios.
4. WorkOrdersListComponent usando p-table.
5. WorkOrderCreateComponent.
6. WorkOrderDetailComponent como pantalla central del flujo.
7. Resumen de cliente y vehículo en p-card.
8. Visualización clara del estado actual con p-tag.
9. Badges visuales para WorkOrderStatus.
10. Acción para asignar mecánico si el backend lo permite.
11. Acción para cambiar estado según reglas del backend.
12. Formulario para registrar diagnóstico técnico.
13. Formulario para notas internas si el backend lo permite.
14. Sección de inspección inicial usando p-panel o p-card.
15. Formulario de inspección inicial.
16. Formulario para agregar fotos simuladas por URL.
17. Tabla/lista de fotos simuladas.
18. Validaciones con Reactive Forms.
19. Toast messages para éxito y error.
20. ConfirmDialog para cambios críticos de estado.
21. Actualización del sidebar y rutas necesarias.

Usa PrimeNG:

- p-table
- p-card
- p-panel
- p-tag
- p-button
- p-dropdown o p-select según versión instalada
- p-inputText
- p-inputNumber
- p-calendar o DatePicker según versión instalada
- p-dialog si aplica
- p-toast
- p-confirmDialog

Reglas:

- WorkOrderDetailComponent debe ser la pantalla central del taller.
- Las acciones visibles deben depender del estado de la orden.
- No muestres acciones que el backend va a rechazar claramente.
- Respeta los endpoints y DTOs reales del backend.
- No implementes todavía quotations, inventory, used parts, labor, quality control, delivery, vehicle history ni dashboard real.
- UI en español.
- Código en inglés.

Antes de modificar archivos, revisa los endpoints y DTOs reales del backend para workorders e inspections.

***
Continúa con la fase frontend 4 siguiendo estrictamente AGENTS.md y el backend existente de acuerdo al contrato que esta en el archivo openapi.yaml.

Implementa únicamente quotations y public quotation approval flow usando PrimeNG.

Incluye:

1. QuotationService.
2. PublicQuotationService si conviene separarlo.
3. Interfaces/Models necesarios.
4. Sección de cotización dentro de WorkOrderDetailComponent.
5. Crear cotización para una orden.
6. Agregar items de cotización.
7. Editar items mientras esté en DRAFT si el backend lo permite.
8. Eliminar items mientras esté en DRAFT si el backend lo permite.
9. Mostrar items usando p-table.
10. Mostrar subtotal, IVA y total.
11. Enviar cotización.
12. Mostrar y copiar publicToken o link público.
13. Crear ruta pública para cotización por token.
14. PublicQuotationComponent sin MainLayoutComponent privado.
15. Vista pública profesional usando p-card.
16. Botón para aprobar cotización.
17. Botón para rechazar cotización.
18. Estado visual después de aprobar o rechazar.
19. Toast messages o mensajes visuales para éxito y error.
20. ConfirmDialog para aprobar/rechazar/enviar cuando aplique.

Usa PrimeNG:

- p-card
- p-table
- p-button
- p-tag
- p-inputText
- p-inputNumber
- p-dropdown o p-select según versión instalada
- p-toast
- p-confirmDialog
- p-divider si aplica

Reglas:

- Los endpoints públicos no requieren JWT.
- La página pública no debe mostrar sidebar ni topbar privado.
- La página pública debe verse profesional y confiable.
- Respeta el DTO real que retorna el backend.
- No inventes campos que no existan.
- No implementes todavía inventory, used parts, labor, quality control, delivery, vehicle history ni dashboard real.
- UI en español.
- Código en inglés.

Antes de modificar archivos, revisa los endpoints y DTOs reales del backend para quotations y public quotations.

***
Continúa con la fase frontend 5 siguiendo estrictamente AGENTS.md y el backend existente de acuerdo al contrato que esta en el archivo openapi.yaml.

Implementa únicamente inventory y used parts usando PrimeNG.

Incluye:

1. InventoryService.
2. Interfaces/Models necesarios.
3. InventoryListComponent usando p-table.
4. InventoryFormComponent.
5. Búsqueda por nombre o SKU si el backend lo permite.
6. Visualización de currentStock y minStock.
7. Alerta visual de stock bajo usando p-tag.
8. Acción para desactivar repuesto.
9. Sección de repuestos usados dentro de WorkOrderDetailComponent.
10. Formulario para registrar repuesto usado en una orden.
11. Selector de repuesto usando dropdown/select/autocomplete según convenga y según PrimeNG instalado.
12. Listado de repuestos usados por orden usando p-table.
13. Acción para eliminar repuesto usado si el backend lo permite.
14. Mostrar total y margin si el backend lo retorna.
15. Validaciones con Reactive Forms.
16. Toast messages para éxito y error.
17. ConfirmDialog para desactivar repuesto o eliminar repuesto usado.

Usa PrimeNG:

- p-table
- p-card
- p-toolbar
- p-button
- p-inputText
- p-inputNumber
- p-dropdown, p-select o p-autoComplete según versión instalada
- p-tag
- p-toast
- p-confirmDialog

Reglas:

- Solo mostrar acción de agregar repuesto si la orden está APPROVED o IN_PROGRESS.
- No mostrar acción de agregar repuesto si la orden está DELIVERED, CANCELLED, REJECTED, RECEIVED, DIAGNOSIS o QUOTED.
- Manejar error de stock insuficiente.
- No usar repuestos inactivos para nuevos registros.
- Respeta endpoints y DTOs reales del backend.
- No implementes todavía labor, quality control, delivery, vehicle history ni dashboard real.
- UI en español.
- Código en inglés.

Antes de modificar archivos, revisa los endpoints y DTOs reales del backend para inventory y used parts.

***
Continúa con la fase frontend 6 siguiendo estrictamente AGENTS.md y el backend existente de acuerdo al contrato que esta en el archivo openapi.yaml.

Implementa únicamente labor usando PrimeNG.

Incluye:

1. LaborService si no existe.
2. Interfaces/Models necesarios.
3. Sección de mano de obra dentro de WorkOrderDetailComponent.
4. Formulario para agregar mano de obra.
5. Listado de mano de obra por orden usando p-table.
6. Acción para eliminar mano de obra si el backend lo permite.
7. Visualización del total de mano de obra si el backend lo retorna o si puede calcularse localmente.
8. Validaciones con Reactive Forms.
9. Toast messages para éxito y error.
10. ConfirmDialog para eliminar mano de obra.

Usa PrimeNG:

- p-card
- p-table
- p-button
- p-inputText
- p-inputNumber
- p-toast
- p-confirmDialog

Reglas:

- Solo mostrar acción de agregar mano de obra si la orden está APPROVED o IN_PROGRESS.
- No mostrar acción si la orden está RECEIVED, DIAGNOSIS, QUOTED, REJECTED, READY, DELIVERED o CANCELLED.
- No eliminar mano de obra de una orden DELIVERED.
- description es obligatorio.
- price debe ser mayor o igual a 0.
- Respeta endpoints y DTOs reales del backend.
- No implementes todavía quality control, delivery, vehicle history ni dashboard real.
- UI en español.
- Código en inglés.

Antes de modificar archivos, revisa los endpoints y DTOs reales del backend para labor.
***	
Continúa con la fase frontend 7 siguiendo estrictamente AGENTS.md y el backend existente de acuerdo al contrato que esta en el archivo openapi.yaml.

Implementa únicamente quality control y mark ready usando PrimeNG.

Incluye:

1. Sección de control de calidad dentro de WorkOrderDetailComponent.
2. Formulario para marcar qualityControlCompleted.
3. Campo para qualityControlNotes.
4. Acción para guardar control de calidad.
5. Acción para marcar la orden como READY.
6. Actualización visual del estado de la orden.
7. Validaciones.
8. Toast messages para éxito y error.
9. ConfirmDialog antes de marcar como READY.

Usa PrimeNG:

- p-card
- p-panel
- p-checkbox
- p-button
- p-inputText o textarea compatible
- p-toast
- p-confirmDialog
- p-tag

Reglas:

- Solo mostrar quality control editable si la orden está IN_PROGRESS.
- Solo mostrar acción de marcar READY si la orden está IN_PROGRESS y qualityControlCompleted es true.
- No modificar quality control después de DELIVERED.
- No permitir READY visualmente para órdenes CANCELLED o REJECTED.
- Respeta endpoints y DTOs reales del backend.
- No implementes todavía delivery, vehicle history ni dashboard real.
- UI en español.
- Código en inglés.

Antes de modificar archivos, revisa los endpoints y DTOs reales del backend para quality control y mark ready.

***	
Continúa con la fase frontend 8 siguiendo estrictamente AGENTS.md y el backend existente de acuerdo al contrato que esta en el archivo openapi.yaml.

Implementa únicamente delivery usando PrimeNG.

Incluye:

1. Sección de entrega dentro de WorkOrderDetailComponent.
2. Formulario para registrar deliveredTo.
3. Formulario para registrar finalMileage.
4. Campo de notes si el backend lo acepta.
5. Acción para entregar vehículo.
6. ConfirmDialog antes de entregar.
7. Actualización visual del estado a DELIVERED.
8. Mostrar deliveredAt, deliveredTo y finalMileage.
9. Toast messages para éxito y error.
10. Mostrar secciones importantes como solo lectura después de DELIVERED.

Usa PrimeNG:

- p-card
- p-button
- p-inputText
- p-inputNumber
- p-toast
- p-confirmDialog
- p-tag

Reglas:

- Solo mostrar acción de entregar si la orden está READY.
- No mostrar entrega si la orden está CANCELLED o REJECTED.
- No entregar visualmente una orden IN_PROGRESS directamente.
- deliveredTo es obligatorio.
- finalMileage debe ser mayor o igual a currentMileage.
- Respeta endpoints y DTOs reales del backend.
- No implementes todavía vehicle history ni dashboard real.
- UI en español.
- Código en inglés.

Antes de modificar archivos, revisa los endpoints y DTOs reales del backend para delivery.
****	
Continúa con la fase frontend 9 siguiendo estrictamente AGENTS.md y el backend existente de acuerdo al contrato que esta en el archivo openapi.yaml.

Implementa únicamente vehicle history usando PrimeNG.

Incluye:

1. VehicleHistoryService si hace falta.
2. Interfaces/Models necesarios.
3. VehicleHistoryComponent.
4. Ruta para historial por vehicleId.
5. Opción para consultar historial desde el detalle del vehículo.
6. Opción para consultar historial por placa si el backend lo permite.
7. Visualización del vehículo usando p-card.
8. Visualización del cliente actual usando p-card.
9. Lista de órdenes del vehículo ordenadas de más reciente a más antigua.
10. Mostrar cada orden usando p-card, p-panel o p-table según convenga.
11. Mostrar por cada orden:
    - código.
    - fecha.
    - estado.
    - motivo de ingreso.
    - diagnóstico.
    - repuestos usados.
    - mano de obra.
    - total si está disponible.
    - fecha de entrega si existe.
12. Estado vacío cuando no existan órdenes.
13. Toast messages o mensajes visuales de error.

Usa PrimeNG:

- p-card
- p-panel
- p-table
- p-tag
- p-button
- p-inputText
- p-toast

Reglas:

- El historial no debe exponer internalNotes si no vienen en la respuesta.
- El historial debe servir para entender trabajos anteriores del vehículo.
- Si la placa no existe, mostrar un mensaje claro.
- Respeta endpoints y DTOs reales del backend.
- No implementes todavía dashboard real.
- UI en español.
- Código en inglés.

Antes de modificar archivos, revisa los endpoints y DTOs reales del backend para vehicle history.
NOTA: no se implementaron bien hay que revisar
****	
Continúa con la fase frontend 10 siguiendo estrictamente AGENTS.md y el backend existente de acuerdo al contrato que esta en el archivo openapi.yaml.

Implementa únicamente dashboard operativo real usando PrimeNG y la plantilla propia.

Incluye:

1. DashboardService.
2. Interfaces/Models necesarios.
3. DashboardComponent conectado al endpoint real GET /api/dashboard.
4. Cards/resúmenes usando StatCardComponent para:
   - totalWorkOrdersThisMonth.
   - estimatedRevenueThisMonth.
   - pendingWorkOrders.
   - readyToDeliverWorkOrders.
   - deliveredWorkOrdersThisMonth.
5. Sección para workOrdersByStatus.
6. Sección para lowStockItems usando p-table.
7. Estados de carga.
8. Estados vacíos.
9. Toast messages o mensajes de error.
10. Diseño simple, claro y responsive.

Usa PrimeNG:

- p-card
- p-table
- p-tag
- p-button
- p-progressSpinner
- p-toast
- opcional: chart solo si ya está disponible y no complica el MVP

Reglas:

- No implementes gráficos complejos si no son necesarios.
- Respeta el contrato real del endpoint GET /api/dashboard.
- Mantén el dashboard propio, no uses plantilla externa.
- UI en español.
- Código en inglés.

Antes de modificar archivos, revisa el DTO real del backend para dashboard.
***	
Continúa con la fase frontend 11 siguiendo estrictamente AGENTS.md y el backend existente.

Realiza una revisión integral del frontend Angular + PrimeNG de Taller360.

No implementes funcionalidades nuevas fuera del MVP.

Objetivo:
Corregir inconsistencias, mejorar integración con backend, limpiar código, revisar rutas, formularios, servicios, plantilla propia y dejar el frontend listo para probar el flujo completo.

Incluye:

1. Revisión de arquitectura
- Verifica que la estructura Angular respete AGENTS.md.
- Verifica que la plantilla propia esté bien separada en layout, sidebar, topbar y componentes reutilizables.
- Verifica que PrimeNG esté configurado correctamente.
- Verifica que no se use Bootstrap ni Tailwind.
- Verifica que los servicios consuman el backend real.
- Verifica que los modelos/interfaces coincidan con los DTOs reales del backend.
- Verifica que no haya código muerto, imports sin usar o componentes vacíos.

2. Revisión de autenticación
- Verifica login.
- Verifica almacenamiento de token.
- Verifica interceptor JWT.
- Verifica AuthGuard.
- Verifica logout.
- Verifica que rutas privadas estén protegidas.
- Verifica que public quotation no requiera login.

3. Revisión de la plantilla
- Verifica sidebar.
- Verifica topbar.
- Verifica layout responsive.
- Verifica PageHeaderComponent.
- Verifica StatCardComponent.
- Verifica Toast global.
- Verifica ConfirmDialog global.
- Verifica consistencia visual entre pantallas.

4. Revisión del flujo principal
Verifica que se pueda probar desde la interfaz:
- login.
- crear cliente.
- crear vehículo.
- crear orden.
- registrar inspección inicial.
- agregar fotos simuladas.
- cambiar a DIAGNOSIS.
- registrar diagnóstico.
- crear cotización.
- agregar items.
- enviar cotización.
- abrir página pública.
- aprobar cotización.
- verificar orden APPROVED.
- cambiar a IN_PROGRESS.
- registrar repuestos usados.
- registrar mano de obra.
- completar quality control.
- marcar READY.
- entregar vehículo.
- consultar historial.
- consultar dashboard.

5. Revisión UX
- Verifica textos en español.
- Verifica botones claros.
- Verifica estados de carga.
- Verifica mensajes de error.
- Verifica mensajes de éxito.
- Verifica confirmaciones antes de acciones críticas.
- Verifica tags de estado.
- Verifica que DELIVERED sea mayormente solo lectura.
- Verifica que las acciones disponibles dependan del estado de la orden.

6. Revisión de errores
- Maneja errores 400, 401, 403, 404 y 409.
- Si la sesión expira, redirige a login o muestra mensaje claro.
- Muestra errores de negocio del backend cuando existan.

7. README frontend
Actualiza o crea README del frontend con:
- stack usado.
- PrimeNG usado como librería UI.
- cómo instalar dependencias.
- cómo configurar URL del backend.
- cómo ejecutar la app.
- flujo completo de prueba.
- rutas principales.
- credenciales seed del backend.
- notas de alcance del MVP.

Al final entrega un resumen de:
- problemas encontrados.
- archivos corregidos.
- funcionalidades revisadas.
- comandos para ejecutar el frontend.
