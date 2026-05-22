import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Tag } from 'primeng/tag';
import { StatCardModel } from '../../../../core/models/stat-card.model';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { StatCardComponent } from '../../../../shared/components/stat-card/stat-card.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, Button, Card, Tag, EmptyStateComponent, PageHeaderComponent, StatCardComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  readonly stats: StatCardModel[] = [
    {
      label: 'Recepción del día',
      value: '12',
      helper: 'Espacio preparado para órdenes recibidas y pendientes de ingreso.',
      icon: 'pi pi-car',
      severity: 'info'
    },
    {
      label: 'Cotizaciones',
      value: '5',
      helper: 'Aquí se integrará el seguimiento de cotizaciones enviadas.',
      icon: 'pi pi-file-edit',
      severity: 'warn'
    },
    {
      label: 'Vehículos listos',
      value: '3',
      helper: 'La tarjeta queda lista para mostrar vehículos por entregar.',
      icon: 'pi pi-check-circle',
      severity: 'success'
    },
    {
      label: 'Alertas de inventario',
      value: '2',
      helper: 'Se conectará después al stock real del backend.',
      icon: 'pi pi-exclamation-triangle',
      severity: 'contrast'
    }
  ];
}
