import { Routes } from '@angular/router';
import { DashboardHomeComponent } from './features/dashboard/pages/dashboard-home/dashboard-home.component';
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
        component: DashboardHomeComponent,
        title: 'Dashboard | Taller360'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
