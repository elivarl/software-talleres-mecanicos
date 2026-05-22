import { Routes } from '@angular/router';
import { CustomersComponent } from './features/customers/pages/customers/customers.component';
import { DashboardComponent } from './features/dashboard/pages/dashboard/dashboard.component';
import { InventoryComponent } from './features/inventory/pages/inventory/inventory.component';
import { NotFoundComponent } from './features/not-found/pages/not-found/not-found.component';
import { VehiclesComponent } from './features/vehicles/pages/vehicles/vehicles.component';
import { WorkOrdersComponent } from './features/work-orders/pages/work-orders/work-orders.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        component: DashboardComponent,
        title: 'Dashboard | Taller360'
      },
      {
        path: 'customers',
        component: CustomersComponent,
        title: 'Clientes | Taller360'
      },
      {
        path: 'vehicles',
        component: VehiclesComponent,
        title: 'Vehículos | Taller360'
      },
      {
        path: 'work-orders',
        component: WorkOrdersComponent,
        title: 'Órdenes de trabajo | Taller360'
      },
      {
        path: 'inventory',
        component: InventoryComponent,
        title: 'Inventario | Taller360'
      }
    ]
  },
  {
    path: '**',
    component: NotFoundComponent,
    title: 'Página no encontrada | Taller360'
  }
];
