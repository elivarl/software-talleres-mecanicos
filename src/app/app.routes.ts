import { Routes } from '@angular/router';
import { authChildGuard, authGuard } from './core/guards/auth.guard';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: 'public/quotations/:token',
    loadComponent: () =>
      import('./features/public-quotation/pages/public-quotation/public-quotation.component').then(
        (m) => m.PublicQuotationComponent
      ),
    title: 'Cotización pública | Taller360'
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login.component').then((m) => m.LoginComponent),
    title: 'Iniciar sesión | Taller360'
  },
  // Future public quotation routes can be added here without AuthGuard.
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    canActivateChild: [authChildGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/pages/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
        title: 'Dashboard | Taller360'
      },
      {
        path: 'customers',
        loadComponent: () =>
          import('./features/customers/pages/customers-list/customers-list.component').then(
            (m) => m.CustomersListComponent
          ),
        title: 'Clientes | Taller360'
      },
      {
        path: 'customers/new',
        loadComponent: () =>
          import('./features/customers/pages/customer-form/customer-form.component').then(
            (m) => m.CustomerFormComponent
          ),
        title: 'Nuevo cliente | Taller360'
      },
      {
        path: 'customers/:id',
        loadComponent: () =>
          import('./features/customers/pages/customer-detail/customer-detail.component').then(
            (m) => m.CustomerDetailComponent
          ),
        title: 'Detalle de cliente | Taller360'
      },
      {
        path: 'customers/:id/edit',
        loadComponent: () =>
          import('./features/customers/pages/customer-form/customer-form.component').then(
            (m) => m.CustomerFormComponent
          ),
        title: 'Editar cliente | Taller360'
      },
      {
        path: 'vehicles',
        loadComponent: () =>
          import('./features/vehicles/pages/vehicles-list/vehicles-list.component').then(
            (m) => m.VehiclesListComponent
          ),
        title: 'Vehículos | Taller360'
      },
      {
        path: 'vehicles/new',
        loadComponent: () =>
          import('./features/vehicles/pages/vehicle-form/vehicle-form.component').then(
            (m) => m.VehicleFormComponent
          ),
        title: 'Nuevo vehículo | Taller360'
      },
      {
        path: 'vehicles/:id',
        loadComponent: () =>
          import('./features/vehicles/pages/vehicle-detail/vehicle-detail.component').then(
            (m) => m.VehicleDetailComponent
          ),
        title: 'Detalle de vehículo | Taller360'
      },
      {
        path: 'vehicles/:id/history',
        loadComponent: () =>
          import('./features/vehicle-history/pages/vehicle-history/vehicle-history.component').then(
            (m) => m.VehicleHistoryComponent
          ),
        title: 'Historial del vehículo | Taller360'
      },
      {
        path: 'vehicles/:id/edit',
        loadComponent: () =>
          import('./features/vehicles/pages/vehicle-form/vehicle-form.component').then(
            (m) => m.VehicleFormComponent
          ),
        title: 'Editar vehículo | Taller360'
      },
      {
        path: 'work-orders',
        loadComponent: () =>
          import('./features/work-orders/pages/work-orders-list/work-orders-list.component').then(
            (m) => m.WorkOrdersListComponent
          ),
        title: 'Órdenes de trabajo | Taller360'
      },
      {
        path: 'work-orders/new',
        loadComponent: () =>
          import('./features/work-orders/pages/work-order-create/work-order-create.component').then(
            (m) => m.WorkOrderCreateComponent
          ),
        title: 'Nueva orden de trabajo | Taller360'
      },
      {
        path: 'work-orders/:id',
        loadComponent: () =>
          import('./features/work-orders/pages/work-order-detail/work-order-detail.component').then(
            (m) => m.WorkOrderDetailComponent
          ),
        title: 'Detalle de orden de trabajo | Taller360'
      },
      {
        path: 'inventory',
        loadComponent: () =>
          import('./features/inventory/pages/inventory-list/inventory-list.component').then(
            (m) => m.InventoryListComponent
          ),
        title: 'Inventario | Taller360'
      },
      {
        path: 'inventory/new',
        loadComponent: () =>
          import('./features/inventory/pages/inventory-form/inventory-form.component').then(
            (m) => m.InventoryFormComponent
          ),
        title: 'Nuevo repuesto | Taller360'
      },
      {
        path: 'inventory/:id/edit',
        loadComponent: () =>
          import('./features/inventory/pages/inventory-form/inventory-form.component').then(
            (m) => m.InventoryFormComponent
          ),
        title: 'Editar repuesto | Taller360'
      },
      {
        path: '**',
        loadComponent: () =>
          import('./features/not-found/pages/not-found/not-found.component').then(
            (m) => m.NotFoundComponent
          ),
        title: 'Página no encontrada | Taller360'
      }
    ]
  }
];
