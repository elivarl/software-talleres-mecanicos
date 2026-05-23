import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputText } from 'primeng/inputtext';
import { Panel } from 'primeng/panel';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { getWorkOrderStatusLabel, getWorkOrderStatusSeverity } from '../../../../core/models/work-order.model';
import {
  VehicleHistoryOrderTotals,
  VehicleHistoryResponse,
  VehicleHistoryWorkOrder
} from '../../../../core/models/vehicle-history.model';
import { VehicleHistoryService } from '../../../../core/services/vehicle-history.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-vehicle-history',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    CurrencyPipe,
    PageHeaderComponent,
    LoadingStateComponent,
    EmptyStateComponent,
    Card,
    Panel,
    TableModule,
    Tag,
    Button,
    InputText
  ],
  templateUrl: './vehicle-history.component.html',
  styleUrl: './vehicle-history.component.css'
})
export class VehicleHistoryComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly vehicleHistoryService = inject(VehicleHistoryService);
  private readonly messageService = inject(MessageService);

  readonly history = signal<VehicleHistoryResponse | null>(null);
  readonly loading = signal(false);
  readonly searchPlate = signal('');
  readonly searchedByPlate = signal(false);
  readonly vehicleId = signal<number | null>(null);
  readonly sortedWorkOrders = computed(() => {
    const workOrders = this.history()?.workOrders ?? [];

    return [...workOrders].sort((left, right) => {
      const leftTime = left.receptionDate ? new Date(left.receptionDate).getTime() : 0;
      const rightTime = right.receptionDate ? new Date(right.receptionDate).getTime() : 0;
      return rightTime - leftTime;
    });
  });

  ngOnInit(): void {
    const vehicleIdParam = this.route.snapshot.paramMap.get('id');
    const plateParam = this.route.snapshot.queryParamMap.get('plate');

    if (plateParam?.trim()) {
      this.searchPlate.set(plateParam.trim());
      this.loadHistoryByPlate();
      return;
    }

    if (!vehicleIdParam) {
      return;
    }

    const vehicleId = Number(vehicleIdParam);
    this.vehicleId.set(vehicleId);
    this.loadHistoryById(vehicleId);
  }

  back(): void {
    const vehicleId = this.vehicleId();

    if (vehicleId) {
      void this.router.navigate(['/vehicles', vehicleId]);
      return;
    }

    void this.router.navigate(['/vehicles']);
  }

  searchByPlate(): void {
    this.loadHistoryByPlate();
  }

  clearPlateSearch(): void {
    this.searchPlate.set('');
    this.searchedByPlate.set(false);
    this.history.set(null);

    const vehicleId = this.vehicleId();

    if (vehicleId) {
      void this.router.navigate(['/vehicles', vehicleId, 'history']);
      this.loadHistoryById(vehicleId);
      return;
    }

    void this.router.navigate(['/vehicles']);
  }

  viewWorkOrder(workOrderId: number): void {
    void this.router.navigate(['/work-orders', workOrderId]);
  }

  goToCustomer(): void {
    const customerId = this.history()?.customer.id;

    if (customerId) {
      void this.router.navigate(['/customers', customerId]);
    }
  }

  getStatusLabel(status: VehicleHistoryWorkOrder['status']): string {
    return getWorkOrderStatusLabel(status);
  }

  getStatusSeverity(status: VehicleHistoryWorkOrder['status']) {
    return getWorkOrderStatusSeverity(status);
  }

  getOrderTotal(totals?: VehicleHistoryOrderTotals | null): number | null {
    if (!totals) {
      return null;
    }

    if (typeof totals.serviceTotal === 'number') {
      return totals.serviceTotal;
    }

    if (typeof totals.quotationTotal === 'number') {
      return totals.quotationTotal;
    }

    if (typeof totals.partsTotal === 'number' || typeof totals.laborTotal === 'number') {
      return (totals.partsTotal ?? 0) + (totals.laborTotal ?? 0);
    }

    return null;
  }

  private loadHistoryById(vehicleId: number): void {
    this.loading.set(true);
    this.searchedByPlate.set(false);

    this.vehicleHistoryService.getVehicleHistoryById(vehicleId).subscribe({
      next: (history) => {
        this.history.set(history);
        this.searchPlate.set(history.vehicle.plate);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.history.set(null);
        this.messageService.add({
          severity: 'error',
          summary: 'Historial del vehículo',
          detail:
            (error.error?.message as string) ||
            'No se pudo cargar el historial del vehículo.'
        });
      }
    });
  }

  private loadHistoryByPlate(): void {
    const plate = this.searchPlate().trim();

    if (!plate) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Historial del vehículo',
        detail: 'Ingresa una placa para consultar el historial.'
      });
      return;
    }

    this.loading.set(true);
    this.searchedByPlate.set(true);

    this.vehicleHistoryService.getVehicleHistoryByPlate(plate).subscribe({
      next: (history) => {
        this.history.set(history);
        this.searchPlate.set(history.vehicle.plate);
        this.vehicleId.set(history.vehicle.id);
        this.loading.set(false);
        void this.router.navigate(['/vehicles', history.vehicle.id, 'history'], {
          queryParams: { plate: history.vehicle.plate },
          replaceUrl: true
        });
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.history.set(null);

        if (error.status === 404) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Historial del vehículo',
            detail: 'No existe un vehículo registrado con esa placa.'
          });
          return;
        }

        this.messageService.add({
          severity: 'error',
          summary: 'Historial del vehículo',
          detail:
            (error.error?.message as string) ||
            'No se pudo consultar el historial por placa.'
        });
      }
    });
  }
}
