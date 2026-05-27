Lee el archivo AGENTS.md y empieza la fase 1 del backend de Taller360.

Implementa únicamente:

1. Proyecto Spring Boot con Java 21 y Gradle.
2. Estructura modular base.
3. Configuración para MySQL.
4. Flyway.
5. Manejo global de errores.
6. Seguridad JWT.
7. Módulo users.
8. Módulo auth.
9. Seed inicial de usuario ADMIN.
10. README inicial.

No implementes todavía customers, vehicles, workorders, inspections, quotations, inventory, labor ni dashboard.

Antes de modificar archivos, propón brevemente la estructura que vas a crear y luego implementa.

***

Continúa con la fase 2 siguiendo AGENTS.md.

Implementa los módulos customers y vehicles.

Incluye:
- entidades
- DTOs
- repositorios
- servicios
- controllers
- migraciones Flyway
- validaciones
- búsqueda por nombre, identificación, teléfono y placa
- endpoints de historial base del vehículo

Agrega o actualiza tests si corresponde.
Actualiza el README con los endpoints nuevos.

***

Continúa con la fase 3 siguiendo AGENTS.md.

Implementa workorders e inspections.

Incluye:
- entidad WorkOrder
- enum WorkOrderStatus
- generación automática de código OT-000001
- creación de órdenes
- asignación de mecánico
- cambio de estado con reglas de negocio
- registro de diagnóstico
- notas internas
- inspección inicial
- fotos simuladas por URL
- endpoints correspondientes
- migraciones Flyway
- tests de reglas críticas

No implementes quotations todavía.

***
Continúa con la fase 4 siguiendo estrictamente AGENTS.md.

Implementa únicamente el módulo quotations y el flujo público de aprobación/rechazo de cotizaciones.

Incluye:
- entidades, enums, DTOs, repositories, services y controllers necesarios.
- migraciones Flyway.
- cálculo de subtotal, tax y total usando app.tax-rate.
- generación de código COT-000001.
- generación de publicToken.
- endpoints privados y públicos definidos en AGENTS.md.
- actualización del README.
- tests de reglas críticas del módulo quotations.

Respeta las reglas de negocio ya definidas en AGENTS.md, especialmente:
- solo modificar cotizaciones DRAFT.
- no enviar cotizaciones sin items.
- al enviar, la orden pasa a QUOTED.
- al aprobar, la orden pasa a APPROVED.
- al rechazar, la orden pasa a REJECTED.
- los endpoints públicos no requieren JWT.

No implementes todavía inventory, used parts, labor, quality control, delivery, vehicle history ni dashboard.

Antes de modificar archivos, revisa lo ya implementado en workorders e inspections para mantener consistencia.

***

Continúa con la fase 5 siguiendo estrictamente AGENTS.md.

Implementa únicamente inventory y used parts.

Incluye:
- entidades, DTOs, repositories, services y controllers necesarios.
- migraciones Flyway.
- endpoints definidos en AGENTS.md.
- descuento automático de stock al registrar repuestos usados.
- devolución de stock al eliminar un repuesto usado antes de entregar.
- cálculo de total y margin.
- validaciones de stock e ítems inactivos.
- actualización del README.
- tests de reglas críticas del módulo inventory y used parts.

Respeta todas las reglas de negocio ya definidas en AGENTS.md.

No implementes todavía labor, quality control, delivery, vehicle history ni dashboard.

Antes de modificar archivos, revisa lo ya implementado para mantener consistencia.

***

Continúa con la fase 6 siguiendo estrictamente el archivo AGENTS.md.

Implementa únicamente el módulo labor.

Incluye:
- entidad LaborItem.
- DTOs necesarios.
- repository.
- service.
- controller.
- migración Flyway.
- endpoints definidos en AGENTS.md.
- validaciones.
- reglas de negocio.
- tests de reglas críticas.
- actualización del README.

Respeta todas las reglas de negocio definidas en AGENTS.md, especialmente:
- labor solo puede agregarse cuando la orden está APPROVED o IN_PROGRESS.
- labor no puede agregarse si la orden está RECEIVED, DIAGNOSIS, QUOTED, REJECTED, READY, DELIVERED o CANCELLED.
- labor no puede eliminarse de una orden DELIVERED.
- price debe ser mayor o igual a 0.
- description es obligatorio.

No implementes todavía quality control, delivery, vehicle history ni dashboard.

Antes de modificar archivos, revisa lo ya implementado en workorders, inspections, quotations, inventory y used parts para mantener consistencia con nombres, estados, servicios, DTOs, errores y migraciones.

***

Continúa con la fase 7 siguiendo estrictamente el archivo AGENTS.md.

Implementa únicamente quality control y mark ready para work orders.

Incluye:
- endpoint de quality control definido en AGENTS.md.
- endpoint para marcar una orden como READY.
- DTOs necesarios.
- métodos de service necesarios.
- validaciones.
- reglas de negocio.
- tests de reglas críticas.
- actualización del README.

Respeta todas las reglas de negocio definidas en AGENTS.md, especialmente:
- quality control solo puede completarse si la orden está IN_PROGRESS.
- una orden solo puede pasar a READY si está IN_PROGRESS.
- una orden solo puede pasar a READY si qualityControlCompleted es true.
- READY significa que el trabajo terminó, pero el cliente todavía no retira el vehículo.
- quality control no puede modificarse después de DELIVERED.
- una orden CANCELLED o REJECTED no puede pasar a READY.

No implementes todavía delivery, vehicle history ni dashboard.

Antes de modificar archivos, revisa lo ya implementado en workorders, inspections, quotations, inventory, used parts y labor para mantener consistencia con nombres, estados, servicios, DTOs, errores y migraciones.

***
Continúa con la fase 8 siguiendo estrictamente el archivo AGENTS.md.

Implementa únicamente la entrega del vehículo.

Incluye:
- endpoint de delivery definido en AGENTS.md.
- DTOs necesarios.
- métodos de service necesarios.
- actualización de estado a DELIVERED.
- registro de deliveredAt.
- registro de deliveredTo.
- registro de finalMileage.
- validaciones.
- reglas de negocio.
- tests de reglas críticas.
- actualización del README.

Respeta todas las reglas de negocio definidas en AGENTS.md, especialmente:
- solo una orden en estado READY puede entregarse.
- al entregar, la orden debe pasar a DELIVERED.
- al entregar, se debe registrar deliveredAt.
- deliveredTo es obligatorio.
- finalMileage debe ser mayor o igual a currentMileage.
- después de DELIVERED se deben bloquear modificaciones importantes.
- una orden CANCELLED o REJECTED no puede entregarse.
- una orden IN_PROGRESS no puede entregarse directamente.

No implementes todavía vehicle history ni dashboard.

Antes de modificar archivos, revisa lo ya implementado en workorders, quality control, inspections, quotations, inventory, used parts y labor para mantener consistencia con nombres, estados, servicios, DTOs, errores y migraciones.

***

Continúa con la fase 9 siguiendo estrictamente el archivo AGENTS.md.

Implementa únicamente vehicle history.

Incluye:
- endpoint GET /api/vehicles/{id}/history.
- endpoint GET /api/vehicles/by-plate/{plate}/history.
- DTOs necesarios para representar el historial.
- consulta de datos del vehículo.
- consulta de datos del cliente actual.
- consulta de órdenes asociadas al vehículo.
- inclusión de inspección si existe.
- inclusión de cotización si existe.
- inclusión de repuestos usados.
- inclusión de mano de obra.
- inclusión de diagnóstico.
- inclusión de estado final de cada orden.
- inclusión de fechas relevantes.
- inclusión de kilometraje registrado.
- cálculo de totales básicos por orden.
- ordenamiento del historial desde la orden más reciente hasta la más antigua.
- tests necesarios.
- actualización del README.

Respeta todas las reglas de negocio definidas en AGENTS.md, especialmente:
- el historial debe mostrar únicamente órdenes asociadas al vehículo consultado.
- el historial debe ordenarse desde newest to oldest.
- el historial no debe exponer internalNotes si AGENTS.md lo considera información administrativa sensible.
- el historial debe ser útil para entender qué trabajos se hicieron en visitas anteriores.
- si se busca por placa y no existe vehículo, devolver error 404 consistente.

No implementes todavía dashboard.

Antes de modificar archivos, revisa lo ya implementado en vehicles, workorders, inspections, quotations, inventory, used parts, labor, quality control y delivery para mantener consistencia con nombres, DTOs, errores y relaciones.

***

Continúa con la fase 10 siguiendo estrictamente el archivo AGENTS.md.

Implementa únicamente el dashboard operativo.

Incluye:
- endpoint GET /api/dashboard.
- DTOs necesarios.
- service de dashboard.
- consultas necesarias a workorders, inventory, used parts y labor.
- totalWorkOrdersThisMonth.
- workOrdersByStatus.
- estimatedRevenueThisMonth.
- pendingWorkOrders.
- readyToDeliverWorkOrders.
- lowStockItems.
- deliveredWorkOrdersThisMonth.
- tests necesarios.
- actualización del README.

Respeta todas las reglas de negocio definidas en AGENTS.md, especialmente:
- estimatedRevenueThisMonth se calcula usando labor items + used parts.
- no deben contar órdenes CANCELLED.
- no deben contar órdenes REJECTED.
- lowStockItems son ítems donde currentStock es menor o igual a minStock.
- readyToDeliverWorkOrders debe contar órdenes en estado READY.
- deliveredWorkOrdersThisMonth debe contar órdenes entregadas en el mes actual.
- documenta en README el criterio usado para calcular ingresos estimados.

No implementes funcionalidades fuera del MVP.

Antes de modificar archivos, revisa lo ya implementado en workorders, inventory, used parts, labor y delivery para mantener consistencia con nombres, consultas, DTOs, errores y reglas.

***

Continúa con la fase 11 siguiendo estrictamente el archivo AGENTS.md.

Realiza una revisión integral del backend MVP de Taller360.

No implementes módulos nuevos fuera del alcance del MVP.

Objetivo:
Verificar consistencia, corregir errores, completar tests faltantes y dejar el README listo para probar el flujo completo.

Incluye:

1. Revisión de arquitectura
- Verifica que los módulos respeten la estructura definida en AGENTS.md.
- Verifica que no haya lógica de negocio en controllers.
- Verifica que se usen DTOs y no se expongan entidades JPA directamente.
- Verifica que los nombres estén en inglés y sean consistentes.
- Verifica que no existan clases vacías, código muerto o TODOs importantes.

2. Revisión de reglas de negocio
- Verifica transiciones de WorkOrderStatus.
- Verifica reglas de quotations.
- Verifica reglas de inventory y used parts.
- Verifica reglas de labor.
- Verifica reglas de quality control.
- Verifica reglas de delivery.
- Verifica que DELIVERED y CANCELLED bloqueen modificaciones importantes.
- Verifica que los endpoints públicos de quotations no requieran JWT.

3. Revisión de migraciones Flyway
- Verifica que las migraciones estén ordenadas correctamente.
- Verifica constraints, foreign keys, unique indexes e índices de búsqueda.
- Verifica seed del usuario ADMIN.
- Verifica que las tablas y columnas usen snake_case.
- Verifica que no haya conflictos entre entidades JPA y esquema SQL.

4. Revisión de seguridad
- Verifica login JWT.
- Verifica BCrypt para passwords.
- Verifica autorización por roles cuando aplique.
- Verifica que usuarios inactivos no puedan autenticarse.
- Verifica que endpoints privados requieran autenticación.
- Verifica que endpoints públicos de quotation estén permitidos sin autenticación.

5. Tests
- Completa o corrige tests unitarios de reglas críticas definidas en AGENTS.md.
- Agrega tests faltantes si detectas reglas importantes sin cobertura.
- Ejecuta o deja documentado el comando para correr tests.
- Corrige errores de compilación o tests fallidos.

6. README final
Actualiza el README en español con:
- descripción del proyecto.
- stack técnico.
- requisitos.
- configuración de base de datos.
- variables o properties necesarias.
- cómo ejecutar la aplicación.
- usuario seed.
- flujo completo de prueba paso a paso.
- endpoints principales.
- reglas de negocio principales.
- funcionalidades fuera del MVP.
- ejemplos básicos de requests para el flujo principal.

7. Flujo principal obligatorio en README
Documenta cómo probar:
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
- abrir endpoint público.
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

Al final, entrega un resumen de:
- problemas encontrados.
- archivos corregidos.
- tests agregados o corregidos.
- comandos para ejecutar el proyecto.
- comandos para ejecutar tests.