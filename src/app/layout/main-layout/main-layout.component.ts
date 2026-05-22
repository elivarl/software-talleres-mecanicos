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
      label: 'Navegación',
      icon: 'pi pi-compass',
      expanded: true,
      items: [
        {
          label: 'Dashboard',
          icon: 'pi pi-chart-bar',
          routerLink: ['/dashboard'],
          command: () => this.closeSidebar()
        },
        {
          label: 'Clientes',
          icon: 'pi pi-users',
          routerLink: ['/customers'],
          command: () => this.closeSidebar()
        },
        {
          label: 'Vehículos',
          icon: 'pi pi-car',
          routerLink: ['/vehicles'],
          command: () => this.closeSidebar()
        },
        {
          label: 'Órdenes de trabajo',
          icon: 'pi pi-briefcase',
          routerLink: ['/work-orders'],
          command: () => this.closeSidebar()
        },
        {
          label: 'Inventario',
          icon: 'pi pi-box',
          routerLink: ['/inventory'],
          command: () => this.closeSidebar()
        }
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
