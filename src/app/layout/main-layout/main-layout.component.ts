import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { TopbarComponent } from '../topbar/topbar.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent, TopbarComponent],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent {
  readonly sidebarOpen = signal(false);

  readonly menuItems: MenuItem[] = [
    {
      label: 'Operación',
      icon: 'pi pi-home',
      items: [
        { label: 'Dashboard', icon: 'pi pi-chart-bar', routerLink: ['/dashboard'] },
        { label: 'Clientes', icon: 'pi pi-users' },
        { label: 'Vehículos', icon: 'pi pi-car' },
        { label: 'Órdenes de trabajo', icon: 'pi pi-briefcase' }
      ]
    },
    {
      label: 'Gestión',
      icon: 'pi pi-warehouse',
      items: [
        { label: 'Inventario', icon: 'pi pi-box' },
        { label: 'Cotizaciones', icon: 'pi pi-file-edit' },
        { label: 'Historial', icon: 'pi pi-history' }
      ]
    }
  ];

  toggleSidebar(): void {
    this.sidebarOpen.update((current) => !current);
  }

  closeSidebar(): void {
    this.sidebarOpen.set(false);
  }
}
