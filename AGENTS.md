# Taller360 Backend - Agent Instructions

## 1. Product Context

Taller360 is a backend MVP for a SaaS web application for mechanical workshops in Ecuador.

The system is intended for small and medium mechanical workshops that currently manage their operations using notebooks, Excel, WhatsApp or informal processes.

The goal is to provide a simple but professional backend that allows a workshop to manage the full vehicle service flow from reception to delivery.

The main business flow is:

Vehicle reception
→ Initial vehicle inspection
→ Work order creation
→ Technical diagnosis
→ Quotation
→ Customer approval or rejection
→ Repair execution
→ Used parts and labor registration
→ Quality control
→ Payment / external invoice
→ Vehicle delivery
→ Vehicle history

The product differential is:

- Transparency with the customer.
- Digital quotation approval through a public secure link.
- Evidence of the vehicle condition at reception.
- Basic control of used parts per work order.
- Basic labor registration per work order.
- Vehicle service history.
- Simple workflow for workshops that currently use notebooks, Excel or WhatsApp.
- A clear operational flow that reduces disputes with customers.

This backend must prioritize business clarity, maintainability and correct rules over unnecessary technical complexity.

---

## 2. Language Rules

Use English for:

- Package names.
- Class names.
- Method names.
- Variable names.
- Entity names.
- DTO names.
- Endpoint paths.
- Enum names.
- Database table names and columns.

Use Spanish only for:

- README documentation if needed.
- Business-facing messages if needed.
- User-facing error messages if needed.
- Future frontend labels if needed.

Do not mix Spanish and English in code names.

Correct examples:

- WorkOrder
- Quotation
- Customer
- Vehicle
- InventoryItem
- WorkOrderPart
- BusinessRuleException
- createWorkOrder()
- updateStatus()
- approveQuotation()

Incorrect examples:

- OrdenTrabajo
- ClienteRepository
- crearWorkOrder()
- cotizacionService

---

## 3. Tech Stack

Use:

- Java 21
- Spring Boot 3.x
- Gradle
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security with JWT
- MySQL
- Flyway
- JUnit 5
- Mockito

Optional:

- Lombok, only if it keeps the code clean and consistent.

Do not use:

- Microservices
- Kafka
- RabbitMQ
- Event sourcing
- CQRS
- Over-engineered hexagonal architecture
- Real SRI electronic invoicing
- Real WhatsApp API
- Online payments
- Real image upload
- Multitenancy
- Native mobile app
- Advanced accounting
- Mechanic commissions
- Advanced warranty management

For photos, store only a simulated text URL.

For invoicing, leave only simple notes or placeholders if needed. Do not integrate with SRI in the MVP.

---

## 4. Backend Architecture

Use a simple modular clean architecture.

Base package:

com.taller360.app

Recommended modules:

auth
users
customers
vehicles
workorders
inspections
quotations
inventory
dashboard
shared

Each business module may follow this structure:

module
- domain
- application
- infrastructure
- web

Example structure:

workorders
- domain
  - WorkOrder.java
  - WorkOrderStatus.java
  - WorkOrderPart.java
  - LaborItem.java
- application
  - WorkOrderService.java
  - dto
- infrastructure
  - WorkOrderRepository.java
- web
  - WorkOrderController.java

The architecture must be clean but pragmatic.

Avoid unnecessary interfaces if there is only one implementation and no clear business reason.

Controllers must not contain business logic.

Application services must coordinate business use cases.

Domain entities may contain simple behavior if it improves business consistency.

Repositories must be placed in the infrastructure layer.

DTOs must be used for API input and output.

Do not expose JPA entities directly from controllers.

---

## 5. General Coding Rules

- Generate functional, consistent and compilable code.
- Do not generate pseudocode.
- Do not leave empty classes.
- Do not leave TODOs for core MVP features unless explicitly requested.
- Keep the MVP focused.
- Prefer clear code over excessive abstraction.
- Use DTOs for request and response models.
- Validate request bodies with Bean Validation.
- Use @ControllerAdvice for global error handling.
- Use custom business exceptions for business rule violations.
- Use transactions where state changes involve multiple entities.
- Use Flyway migrations for all database schema changes.
- Do not modify generated migrations after they are already created unless the project is still in the initial setup phase.
- Create seed data only through Flyway migrations or a clearly documented development seeder.
- Keep names consistent between entities, DTOs, services, controllers and database tables.
- Keep endpoint paths RESTful and consistent.
- Keep business rules out of controllers.
- Use BigDecimal for money values.
- Avoid using double or float for monetary calculations.
- Use LocalDateTime for timestamps.
- Use LocalDate for dates without time.
- Add createdAt and updatedAt fields where relevant.
- Use indexes for frequently searched fields like plate, identification, email, code and publicToken.

---

## 6. Roles

The system has these internal roles:

ADMIN
RECEPTIONIST
MECHANIC

There is no CUSTOMER login in the MVP.

The customer accesses a public quotation link generated from a secure publicToken.

### ADMIN

Can manage:

- Users
- Customers
- Vehicles
- Work orders
- Inspections
- Diagnosis
- Quotations
- Inventory
- Dashboard

### RECEPTIONIST

Can:

- Register customers.
- Register vehicles.
- Create work orders.
- Register initial inspections.
- Create quotations.
- View vehicle history.

### MECHANIC

Can:

- View assigned work orders.
- Register technical diagnosis.
- Register work observations.
- Register used parts.
- Register labor items.
- Complete quality control.

### CUSTOMER

The customer is not an authenticated system user in the MVP.

The customer can use a public link to:

- View quotation.
- Approve quotation.
- Reject quotation.

---

## 7. Authentication and Users Module

Implement JWT authentication.

### User entity

Fields:

id
fullName
email
password
role
active
createdAt
updatedAt

### UserRole enum

Values:

ADMIN
RECEPTIONIST
MECHANIC

### Authentication endpoint

POST /api/auth/login

Login request:

{
  "email": "admin@taller360.com",
  "password": "Admin12345*"
}

Login response:

{
  "accessToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "fullName": "Admin Taller360",
    "email": "admin@taller360.com",
    "role": "ADMIN"
  }
}

### User endpoints

GET /api/users
POST /api/users
GET /api/users/{id}
PUT /api/users/{id}
PATCH /api/users/{id}/deactivate

### Security rules

- Passwords must be encrypted with BCrypt.
- JWT must protect private endpoints.
- Public quotation endpoints must not require authentication.
- Disabled users must not authenticate.
- User email must be unique.
- Only ADMIN can manage users.
- Seed an initial ADMIN user for development.

Seed user:

email: admin@taller360.com
password: Admin12345*
role: ADMIN

---

## 8. Customers Module

The customer represents the owner or person responsible for the vehicle.

### Customer entity

Fields:

id
fullName
identification
phone
email
address
createdAt
updatedAt

### Operations

- Create customer.
- Update customer.
- List customers.
- Search customers by name, identification or phone.
- View customer detail.

### Endpoints

GET /api/customers
POST /api/customers
GET /api/customers/{id}
PUT /api/customers/{id}

### Validations

- fullName is required.
- phone is required.
- identification must be unique if provided.
- email must be valid if provided.

---

## 9. Vehicles Module

A vehicle belongs to a customer.

### Vehicle entity

Fields:

id
customerId
plate
brand
model
year
color
vin
mileage
createdAt
updatedAt

### Operations

- Register vehicle associated with a customer.
- Update vehicle.
- List vehicles.
- Search vehicle by plate.
- View vehicle detail.
- View vehicle history.

### Endpoints

GET /api/vehicles
POST /api/vehicles
GET /api/vehicles/{id}
PUT /api/vehicles/{id}
GET /api/vehicles/{id}/history
GET /api/vehicles/by-plate/{plate}/history

### Validations

- customerId is required.
- plate is required and unique.
- brand is required.
- model is required.
- mileage must be greater than or equal to 0.
- year must be reasonable if provided.

---

## 10. Work Orders Module

The work order is the central document of the system.

A work order represents the complete service process of a vehicle inside the workshop.

### WorkOrder entity

Fields:

id
code
customerId
vehicleId
assignedMechanicId
status
receptionDate
estimatedDeliveryDate
currentMileage
fuelLevel
customerComplaint
initialObservations
diagnosis
internalNotes
qualityControlCompleted
qualityControlNotes
readyAt
deliveredAt
deliveredTo
finalMileage
createdAt
updatedAt

The code field must be automatically generated using this format:

OT-000001
OT-000002
OT-000003

### WorkOrderStatus enum

Values:

RECEIVED
DIAGNOSIS
QUOTED
APPROVED
REJECTED
IN_PROGRESS
READY
DELIVERED
CANCELLED

### Main status flow

RECEIVED
→ DIAGNOSIS
→ QUOTED
→ APPROVED
→ IN_PROGRESS
→ READY
→ DELIVERED

### Alternative flows

QUOTED → REJECTED

RECEIVED / DIAGNOSIS / QUOTED → CANCELLED

### Conceptual meaning of each status

RECEIVED:
The vehicle has arrived and the work order has been created.

DIAGNOSIS:
The mechanic is reviewing the vehicle and preparing the technical diagnosis.

QUOTED:
The quotation was sent to the customer and is waiting for approval or rejection.

APPROVED:
The customer approved the quotation.

REJECTED:
The customer rejected the quotation.

IN_PROGRESS:
The workshop is executing the repair or service.

READY:
The work is finished and quality control was completed. The vehicle is ready for pickup.

DELIVERED:
The vehicle was delivered to the customer.

CANCELLED:
The work order was cancelled before completion.

### Operations

- Create work order.
- Assign mechanic.
- Update status.
- Register technical diagnosis.
- Register internal notes.
- Register quality control.
- Mark work order as ready.
- Deliver vehicle.
- List work orders.
- Filter by status, plate, customer and date.
- View work order detail.

### Endpoints

GET /api/work-orders
POST /api/work-orders
GET /api/work-orders/{id}
PATCH /api/work-orders/{id}/assign-mechanic
PATCH /api/work-orders/{id}/status
PATCH /api/work-orders/{id}/diagnosis
PATCH /api/work-orders/{id}/internal-notes
PATCH /api/work-orders/{id}/quality-control
PATCH /api/work-orders/{id}/mark-ready
PATCH /api/work-orders/{id}/deliver

### Business rules

1. A work order starts in RECEIVED.
2. A work order can only move to DIAGNOSIS from RECEIVED.
3. A work order can only move to QUOTED when a quotation is sent.
4. A work order can only move to APPROVED if there is an approved quotation.
5. A work order can only move to REJECTED if there is a rejected quotation.
6. A work order can only move to IN_PROGRESS if it is APPROVED.
7. A work order can only move to READY if it is IN_PROGRESS and quality control is completed.
8. A work order can only move to DELIVERED if it is READY.
9. A DELIVERED work order cannot be modified except for administrative notes if explicitly allowed.
10. A CANCELLED work order cannot move to another status.
11. Diagnosis and quotation are different concepts.
12. A quotation cannot be created if the work order is DELIVERED or CANCELLED.
13. Parts and labor cannot be added if the work order is not APPROVED or IN_PROGRESS.
14. currentMileage must be greater than or equal to 0.
15. finalMileage must be greater than or equal to currentMileage.
16. assignedMechanicId must reference a user with MECHANIC role if provided.

---

## 11. Initial Vehicle Inspection Module

Before any repair, the workshop must document the condition in which the vehicle arrived.

This protects both the workshop and the customer.

The inspection should record visible vehicle condition, accessories, fuel level, mileage and simulated photo URLs.

### ReceptionInspection entity

Fields:

id
workOrderId
mileage
fuelLevel
exteriorCondition
visibleScratches
visibleDents
lightsWorking
tiresCondition
mirrorsCondition
hasSpareTire
hasJack
hasTools
hasDocuments
personalItemsNotes
generalNotes
createdAt
updatedAt

### InspectionPhoto entity

Fields:

id
inspectionId
photoUrl
description
createdAt

For the MVP, do not implement real file upload.

Store only a simulated URL or text URL in photoUrl.

Examples:

https://example.com/photos/front-view.jpg
https://example.com/photos/dashboard-mileage.jpg

### Operations

- Create initial inspection for a work order.
- Update inspection if allowed.
- Add simulated photo URLs.
- View inspection by work order.

### Endpoints

POST /api/work-orders/{id}/inspection
GET /api/work-orders/{id}/inspection
PUT /api/inspections/{id}
POST /api/inspections/{id}/photos

### Business rules

1. A work order can have only one initial inspection.
2. The inspection belongs to a work order.
3. Inspection cannot be modified if the work order is DELIVERED or CANCELLED.
4. Initial inspection serves as vehicle reception evidence.
5. Inspection should normally be registered when the work order is RECEIVED or DIAGNOSIS.
6. Inspection mileage should match or be close to the work order currentMileage.

---

## 12. Diagnosis Rules

Diagnosis is stored in the work order.

Relevant fields:

customerComplaint
diagnosis
internalNotes
status

### Conceptual distinction

customerComplaint means what the customer reports.

Example:

Customer says the vehicle makes noise when braking.

diagnosis means the technical evaluation by the mechanic.

Example:

Advanced wear was found on the front brake pads. Brake discs show light grooves. Brake pad replacement and disc resurfacing are recommended.

### Rules

1. customerComplaint is not a diagnosis.
2. diagnosis is the mechanic's technical evaluation.
3. The quotation should be based on the diagnosis.
4. The workshop should not repair without quotation approval, except for explicitly authorized services.
5. Registering diagnosis should be allowed only before the work order is DELIVERED or CANCELLED.
6. Registering diagnosis may move the work order to DIAGNOSIS if it was RECEIVED.

---

## 13. Quotations Module

A quotation is the commercial proposal sent to the customer after the technical diagnosis.

It contains parts, labor, subtotal, tax and total.

The quotation is the document the customer approves or rejects.

### Quotation entity

Fields:

id
code
workOrderId
status
subtotal
tax
total
publicToken
sentAt
customerDecisionAt
createdAt
updatedAt

The code field must be automatically generated using this format:

COT-000001
COT-000002
COT-000003

### QuotationItem entity

Fields:

id
quotationId
type
description
quantity
unitPrice
total

### QuotationStatus enum

Values:

DRAFT
SENT
APPROVED
REJECTED
EXPIRED

### QuotationItemType enum

Values:

PART
LABOR

### Private operations

- Create quotation for a work order.
- Add parts or labor items.
- Update items while quotation is DRAFT.
- Delete items while quotation is DRAFT.
- Calculate subtotal, tax and total.
- Send quotation.
- Generate secure public link using publicToken.

### Public operations

- View quotation by token.
- Approve quotation by token.
- Reject quotation by token.

### Private endpoints

POST /api/work-orders/{id}/quotation
GET /api/quotations/{id}
PUT /api/quotations/{id}
POST /api/quotations/{id}/items
PUT /api/quotations/{id}/items/{itemId}
DELETE /api/quotations/{id}/items/{itemId}
POST /api/quotations/{id}/send

### Public endpoints

GET /api/public/quotations/{token}
POST /api/public/quotations/{token}/approve
POST /api/public/quotations/{token}/reject

### Business rules

1. A work order can have only one active quotation.
2. A quotation starts in DRAFT.
3. Only DRAFT quotations can be modified.
4. When a quotation is sent, it moves to SENT.
5. When a quotation is sent, generate publicToken if it does not exist.
6. When a quotation is sent, the work order moves to QUOTED.
7. If the customer approves, the quotation moves to APPROVED and the work order moves to APPROVED.
8. If the customer rejects, the quotation moves to REJECTED and the work order moves to REJECTED.
9. After approval or rejection, the quotation cannot be modified.
10. publicToken must be hard to guess. Use UUID.
11. The customer does not need authentication to view, approve or reject a quotation from the public link.
12. A quotation cannot be approved twice.
13. A quotation cannot be rejected after being approved.
14. A quotation cannot be approved after being rejected.
15. A quotation with no items should not be sent.
16. A quotation cannot be created for a DELIVERED or CANCELLED work order.
17. A quotation item quantity must be greater than 0.
18. A quotation item unitPrice must be greater than or equal to 0.

### Tax calculation

Use:

subtotal = sum(item.total)
tax = subtotal * app.tax-rate
total = subtotal + tax

Use configurable property:

app.tax-rate=0.15

---

## 14. Inventory Module

Inventory manages workshop parts, products and supplies.

### InventoryItem entity

Fields:

id
name
sku
description
currentStock
minStock
unitCost
salePrice
active
createdAt
updatedAt

### WorkOrderPart entity

Fields:

id
workOrderId
inventoryItemId
quantity
unitCost
salePrice
total
margin
createdAt

### Inventory operations

- Create inventory item.
- Update inventory item.
- List inventory items.
- Search by name or SKU.
- Deactivate inventory item.
- View low stock items.

### Used parts operations

- Register part usage in a work order.
- Automatically subtract stock.
- View used parts by work order.
- Calculate total and margin.
- Delete used part before delivery and return stock.

### Inventory endpoints

GET /api/inventory
POST /api/inventory
GET /api/inventory/{id}
PUT /api/inventory/{id}
PATCH /api/inventory/{id}/deactivate
GET /api/inventory/low-stock

### Used parts endpoints

POST /api/work-orders/{id}/parts
GET /api/work-orders/{id}/parts
DELETE /api/work-orders/{id}/parts/{partId}

### Business rules

1. Stock must not go below zero.
2. Every used part must be associated with a work order.
3. Parts cannot be added to work orders in DELIVERED, CANCELLED, REJECTED, RECEIVED, DIAGNOSIS or QUOTED.
4. Parts can only be added when the work order is APPROVED or IN_PROGRESS.
5. When a part is added, copy unitCost and salePrice to WorkOrderPart.
6. Margin is calculated as margin = (salePrice - unitCost) * quantity.
7. total is calculated as salePrice * quantity.
8. If a used part is deleted before the work order is delivered, return the stock.
9. An inactive inventory item should not be used in new work orders.
10. currentStock must be greater than or equal to 0.
11. minStock must be greater than or equal to 0.
12. unitCost must be greater than or equal to 0.
13. salePrice must be greater than or equal to 0.

---

## 15. Labor Module

Labor represents work performed by the workshop that is not a physical inventory item.

### LaborItem entity

Fields:

id
workOrderId
description
price
createdAt

### Operations

- Add labor item to work order.
- List labor items by work order.
- Delete labor item if the work order is not delivered.

### Endpoints

POST /api/work-orders/{id}/labor
GET /api/work-orders/{id}/labor
DELETE /api/work-orders/{id}/labor/{laborId}

### Business rules

1. Labor cannot be added if the work order is DELIVERED, CANCELLED, REJECTED, RECEIVED, DIAGNOSIS or QUOTED.
2. Labor can only be added when the work order is APPROVED or IN_PROGRESS.
3. Labor cannot be deleted from a DELIVERED work order.
4. Labor price must be greater than or equal to 0.
5. Labor description is required.

---

## 16. Quality Control

Quality control happens before marking a work order as READY.

It confirms that the work was completed and reviewed before delivery.

Relevant fields in WorkOrder:

qualityControlCompleted
qualityControlNotes

### Endpoint

PATCH /api/work-orders/{id}/quality-control

Request example:

{
  "completed": true,
  "notes": "Road test completed. Braking system working correctly."
}

### Business rules

1. Quality control can only be completed if the work order is IN_PROGRESS.
2. A work order can only move to READY if qualityControlCompleted is true.
3. READY means the work was completed but the customer has not picked up the vehicle yet.
4. qualityControlNotes should be optional but recommended.
5. Quality control cannot be modified after DELIVERED.

---

## 17. Vehicle Delivery

When the vehicle is ready and delivered to the customer, the work order moves to DELIVERED.

Relevant fields:

deliveredAt
deliveredTo
finalMileage

### Endpoint

PATCH /api/work-orders/{id}/deliver

Request example:

{
  "deliveredTo": "Juan Pérez",
  "finalMileage": 85450,
  "notes": "Vehicle delivered to customer."
}

### Business rules

1. Only a READY work order can be delivered.
2. When delivered, set deliveredAt.
3. When delivered, change status to DELIVERED.
4. After DELIVERED, block important modifications.
5. finalMileage must be greater than or equal to currentMileage.
6. deliveredTo is required.

---

## 18. Vehicle History

Vehicle history must show everything that happened to a vehicle.

This is one of the most important features because it gives continuity and trust to the workshop and the customer.

### Endpoints

GET /api/vehicles/{id}/history
GET /api/vehicles/by-plate/{plate}/history

### Response should include

- Vehicle data.
- Current customer data.
- Previous work orders.
- Service dates.
- Customer complaint.
- Technical diagnosis.
- Final status.
- Used parts.
- Labor items.
- Totals.
- Registered mileage.
- Delivery date if available.
- Quotation status if available.

### Business rules

1. Vehicle history must be ordered from newest to oldest work order.
2. Vehicle history should not expose sensitive internal notes unless the endpoint is administrative.
3. Vehicle history should include only work orders related to the selected vehicle.

---

## 19. Dashboard Module

The dashboard provides a simple operational summary for the workshop owner or administrator.

### Endpoint

GET /api/dashboard

### Response should include

totalWorkOrdersThisMonth
workOrdersByStatus
estimatedRevenueThisMonth
pendingWorkOrders
readyToDeliverWorkOrders
lowStockItems
deliveredWorkOrdersThisMonth

### Revenue calculation for MVP

For the MVP, calculate estimatedRevenueThisMonth using:

labor items + used parts

from work orders in the current month that are not:

CANCELLED
REJECTED

Document this criterion in the README.

### Business rules

1. Cancelled work orders must not count as revenue.
2. Rejected work orders must not count as revenue.
3. Low stock items are inventory items where currentStock is less than or equal to minStock.
4. readyToDeliverWorkOrders should count work orders with READY status.

---

## 20. Error Handling

Use a standard error response.

Example:

{
  "timestamp": "2026-05-18T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot deliver a work order that is not ready",
  "path": "/api/work-orders/1/deliver"
}

Handle:

- Entity not found.
- Business rule violations.
- Validation errors.
- Access denied errors.
- Authentication errors.
- Illegal state errors if needed.

Recommended custom exceptions:

ResourceNotFoundException
BusinessRuleException
InvalidStatusTransitionException
InsufficientStockException
QuotationAlreadyDecidedException

Recommended HTTP status mapping:

- 400 for business rule violations.
- 401 for authentication errors.
- 403 for authorization errors.
- 404 for missing resources.
- 409 for conflicts such as duplicate data or invalid state conflicts.
- 422 may be used for semantic validation errors if preferred, but keep it consistent.

---

## 21. Required Unit Tests

Create unit tests for these critical business rules:

1. A work order cannot move to IN_PROGRESS if it is not APPROVED.
2. A work order cannot move to DELIVERED if it is not READY.
3. A work order cannot move to READY unless quality control is completed.
4. A work order cannot move to APPROVED without an approved quotation.
5. A delivered work order cannot be modified in important fields.
6. A cancelled work order cannot move to another status.
7. An APPROVED or REJECTED quotation cannot be modified.
8. A quotation cannot be approved twice.
9. A quotation cannot be rejected after being approved.
10. A quotation cannot be approved after being rejected.
11. A quotation without items cannot be sent.
12. Stock cannot be reduced if there is not enough quantity.
13. When a used part is registered, stock is reduced.
14. When a used part is deleted before delivery, stock is restored.
15. Parts cannot be added to a DELIVERED work order.
16. Labor cannot be added to a DELIVERED work order.
17. Inspection cannot be modified after the work order is DELIVERED or CANCELLED.
18. finalMileage must be greater than or equal to currentMileage when delivering.

---

## 22. Main Acceptance Flow

The backend must allow testing this complete flow:

1. Login with ADMIN user.
2. Create customer.
3. Create vehicle associated with customer.
4. Create work order.
5. Register initial inspection.
6. Add simulated inspection photo URLs.
7. Change work order to DIAGNOSIS.
8. Register technical diagnosis.
9. Create quotation.
10. Add quotation items for parts and labor.
11. Send quotation.
12. Get public token/link.
13. View quotation from public endpoint.
14. Approve quotation from public endpoint.
15. Verify that work order changed to APPROVED.
16. Change work order to IN_PROGRESS.
17. Register used parts.
18. Register labor items.
19. Complete quality control.
20. Mark work order as READY.
21. Deliver vehicle.
22. Verify that work order changed to DELIVERED.
23. View vehicle history.
24. View dashboard.

---

## 23. Suggested Endpoint Summary

Authentication:

POST /api/auth/login

Users:

GET /api/users
POST /api/users
GET /api/users/{id}
PUT /api/users/{id}
PATCH /api/users/{id}/deactivate

Customers:

GET /api/customers
POST /api/customers
GET /api/customers/{id}
PUT /api/customers/{id}

Vehicles:

GET /api/vehicles
POST /api/vehicles
GET /api/vehicles/{id}
PUT /api/vehicles/{id}
GET /api/vehicles/{id}/history
GET /api/vehicles/by-plate/{plate}/history

Work orders:

GET /api/work-orders
POST /api/work-orders
GET /api/work-orders/{id}
PATCH /api/work-orders/{id}/assign-mechanic
PATCH /api/work-orders/{id}/status
PATCH /api/work-orders/{id}/diagnosis
PATCH /api/work-orders/{id}/internal-notes
PATCH /api/work-orders/{id}/quality-control
PATCH /api/work-orders/{id}/mark-ready
PATCH /api/work-orders/{id}/deliver

Inspections:

POST /api/work-orders/{id}/inspection
GET /api/work-orders/{id}/inspection
PUT /api/inspections/{id}
POST /api/inspections/{id}/photos

Quotations:

POST /api/work-orders/{id}/quotation
GET /api/quotations/{id}
PUT /api/quotations/{id}
POST /api/quotations/{id}/items
PUT /api/quotations/{id}/items/{itemId}
DELETE /api/quotations/{id}/items/{itemId}
POST /api/quotations/{id}/send

Public quotations:

GET /api/public/quotations/{token}
POST /api/public/quotations/{token}/approve
POST /api/public/quotations/{token}/reject

Inventory:

GET /api/inventory
POST /api/inventory
GET /api/inventory/{id}
PUT /api/inventory/{id}
PATCH /api/inventory/{id}/deactivate
GET /api/inventory/low-stock

Used parts:

POST /api/work-orders/{id}/parts
GET /api/work-orders/{id}/parts
DELETE /api/work-orders/{id}/parts/{partId}

Labor:

POST /api/work-orders/{id}/labor
GET /api/work-orders/{id}/labor
DELETE /api/work-orders/{id}/labor/{laborId}

Dashboard:

GET /api/dashboard

---

## 24. Database and Flyway Guidelines

Use Flyway for database migrations.

Suggested migration order:

V1__create_users_table.sql
V2__create_customers_table.sql
V3__create_vehicles_table.sql
V4__create_work_orders_table.sql
V5__create_reception_inspections_table.sql
V6__create_inspection_photos_table.sql
V7__create_quotations_table.sql
V8__create_quotation_items_table.sql
V9__create_inventory_items_table.sql
V10__create_work_order_parts_table.sql
V11__create_labor_items_table.sql
V12__insert_seed_data.sql

Use snake_case for table and column names.

Examples:

users
customers
vehicles
work_orders
reception_inspections
inspection_photos
quotations
quotation_items
inventory_items
work_order_parts
labor_items

Add unique constraints where needed:

users.email
customers.identification if provided and not null
vehicles.plate
work_orders.code
quotations.code
quotations.public_token

Add indexes for search fields:

customers.full_name
customers.identification
customers.phone
vehicles.plate
work_orders.status
work_orders.code
quotations.public_token

---

## 25. README Requirements

The README must include:

1. Project description.
2. Tech stack.
3. Requirements:
   - Java 21
   - Maven
   - MySQL
4. Environment variables or configuration.
5. How to create the database.
6. How to run Flyway migrations.
7. How to start the application.
8. Seed user credentials.
9. Complete test flow.
10. Main endpoints.
11. Main business rules.
12. Out-of-scope features.

The README can be written in Spanish because the initial target users and project owner are Spanish-speaking.

---

## 26. Out of Scope for MVP

Do not implement:

- Angular frontend.
- Real SRI electronic invoicing.
- Real WhatsApp API.
- Online payments.
- Multitenancy.
- Native mobile app.
- Microservices.
- Kafka.
- RabbitMQ.
- Real image upload.
- Advanced warranty management.
- Mechanic commissions.
- Complete accounting module.
- Complex reporting.
- Multi-branch support.

For photos, store only simulated URLs.

For invoicing, leave only simple notes or placeholders if needed. Do not integrate with SRI.

---

## 27. Implementation Preference

When asked to implement, proceed incrementally.

Recommended implementation order:

1. Base Spring Boot project configuration.
2. Security JWT.
3. Users and roles.
4. Customers.
5. Vehicles.
6. Work orders.
7. Initial inspection.
8. Diagnosis.
9. Quotations and public approval flow.
10. Inventory and used parts.
11. Labor.
12. Quality control.
13. Vehicle delivery.
14. Vehicle history.
15. Dashboard.
16. Tests.
17. README.

When implementing a new module:

1. Create or update Flyway migration.
2. Create entity and enum if needed.
3. Create repository.
4. Create DTOs.
5. Create application service.
6. Create controller.
7. Add validations.
8. Add business rule checks.
9. Add tests for critical rules.
10. Update README if relevant.

Do not implement multiple large modules carelessly in a single step if the task can be safely split.

If the user asks to implement a phase, implement only that phase unless explicitly instructed otherwise.

---

## 28. First Development Phase Recommendation

If starting from an empty project, begin with this phase:

1. Create Spring Boot project with Java 21 and Maven.
2. Create modular package structure.
3. Configure MySQL.
4. Configure Flyway.
5. Add global error handling.
6. Add JWT security.
7. Implement users module.
8. Implement auth module.
9. Add seed ADMIN user.
10. Add initial README.

Do not implement customers, vehicles, work orders, inspections, quotations, inventory, labor or dashboard in the first phase unless explicitly requested.

---

## 29. Product Philosophy

The software must not feel like a generic ERP.

It should feel like a workflow system for mechanical workshops.

The core value is not just storing data.

The core value is controlling the process:

Customer arrives
→ Vehicle is documented
→ Problem is registered
→ Diagnosis is created
→ Quotation is sent
→ Customer approves
→ Work is performed
→ Parts and labor are tracked
→ Quality is checked
→ Vehicle is delivered
→ History remains available

Every major backend decision should support this operational flow.