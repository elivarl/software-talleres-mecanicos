import { CommonModule, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Vehicle } from '../../../../core/models/vehicle.model';
import { VehicleService } from '../../../../core/services/vehicle.service';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-vehicle-detail',
  standalone: true,
  imports: [CommonModule, DatePipe, PageHeaderComponent, LoadingStateComponent, Card, Button],
  templateUrl: './vehicle-detail.component.html',
  styleUrl: './vehicle-detail.component.css'
})
export class VehicleDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly vehicleService = inject(VehicleService);
  private readonly messageService = inject(MessageService);

  readonly vehicle = signal<Vehicle | null>(null);
  readonly loading = signal(false);

  ngOnInit(): void {
    const vehicleId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadVehicle(vehicleId);
  }

  editVehicle(): void {
    void this.router.navigate(['/vehicles', this.vehicle()?.id, 'edit']);
  }

  backToList(): void {
    void this.router.navigate(['/vehicles']);
  }

  goToCustomer(): void {
    const customerId = this.vehicle()?.customerId;

    if (customerId) {
      void this.router.navigate(['/customers', customerId]);
    }
  }

  viewHistory(): void {
    const vehicleId = this.vehicle()?.id;

    if (vehicleId) {
      void this.router.navigate(['/vehicles', vehicleId, 'history']);
    }
  }

  private loadVehicle(vehicleId: number): void {
    this.loading.set(true);

    this.vehicleService.getVehicleById(vehicleId).subscribe({
      next: (vehicle) => {
        this.vehicle.set(vehicle);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Vehículos',
          detail: (error.error?.message as string) || 'No se pudo cargar el detalle del vehículo.'
        });
        void this.router.navigate(['/vehicles']);
      }
    });
  }
}
