import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import {
  WORK_ORDER_STATUS_OPTIONS,
  getWorkOrderStatusLabel,
  getWorkOrderStatusSeverity
} from '../../../../core/models/work-order.model';
import { DashboardData } from '../../../../core/models/dashboard.model';
import { StatCardModel } from '../../../../core/models/stat-card.model';
import { DashboardService } from '../../../../core/services/dashboard.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { StatCardComponent } from '../../../../shared/components/stat-card/stat-card.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    Button,
    Card,
    TableModule,
    Tag,
    EmptyStateComponent,
    LoadingStateComponent,
    PageHeaderComponent,
    StatCardComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);
  private readonly messageService = inject(MessageService);
  private readonly router = inject(Router);

  readonly dashboard = signal<DashboardData | null>(null);
  readonly loading = signal(false);
  readonly stats = computed<StatCardModel[]>(() => {
    const dashboard = this.dashboard();

    if (!dashboard) {
      return [];
    }

    return [
      {
        label: 'Órdenes del mes',
        value: String(dashboard.totalWorkOrdersThisMonth),
        helper: 'Órdenes registradas en el mes actual.',
        icon: 'pi pi-briefcase',
        tagLabel: 'Mes actual',
        severity: 'info'
      },
      {
        label: 'Ingreso estimado',
        value: new Intl.NumberFormat('en-US', {
          style: 'currency',
          currency: 'USD',
          minimumFractionDigits: 2
        }).format(dashboard.estimatedRevenueThisMonth),
        helper: 'Suma estimada de ingresos del mes.',
        icon: 'pi pi-dollar',
        tagLabel: 'Facturación',
        severity: 'success'
      },
      {
        label: 'Órdenes pendientes',
        value: String(dashboard.pendingWorkOrders),
        helper: 'Órdenes activas pendientes de cierre.',
        icon: 'pi pi-clock',
        tagLabel: 'Pendientes',
        severity: 'warn'
      },
      {
        label: 'Listas por entregar',
        value: String(dashboard.readyToDeliverWorkOrders),
        helper: 'Vehículos listos para proceso de entrega.',
        icon: 'pi pi-check-circle',
        tagLabel: 'Entrega',
        severity: 'contrast'
      },
      {
        label: 'Entregadas del mes',
        value: String(dashboard.deliveredWorkOrdersThisMonth),
        helper: 'Órdenes entregadas durante el mes actual.',
        icon: 'pi pi-send',
        tagLabel: 'Cerradas',
        severity: 'success'
      }
    ];
  });
  readonly statusItems = computed(() => {
    const workOrdersByStatus = this.dashboard()?.workOrdersByStatus ?? {};

    return WORK_ORDER_STATUS_OPTIONS.map((option) => ({
      status: option.value,
      label: getWorkOrderStatusLabel(option.value),
      count: workOrdersByStatus[option.value] ?? 0,
      severity: getWorkOrderStatusSeverity(option.value)
    })).filter((item) => item.count > 0);
  });

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading.set(true);

    this.dashboardService.getDashboard().subscribe({
      next: (dashboard) => {
        this.dashboard.set(dashboard);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.dashboard.set(null);
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Dashboard',
          detail: (error.error?.message as string) || 'No se pudo cargar el dashboard operativo.'
        });
      }
    });
  }

  goToWorkOrders(): void {
    void this.router.navigate(['/work-orders']);
  }

  goToInventory(): void {
    void this.router.navigate(['/inventory']);
  }
}
