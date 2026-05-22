import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputText } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { Toolbar } from 'primeng/toolbar';
import { Vehicle } from '../../../../core/models/vehicle.model';
import { VehicleService } from '../../../../core/services/vehicle.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-vehicles-list',
  standalone: true,
  imports: [
    CommonModule,
    PageHeaderComponent,
    EmptyStateComponent,
    Card,
    Toolbar,
    TableModule,
    Button,
    InputText
  ],
  templateUrl: './vehicles-list.component.html',
  styleUrl: './vehicles-list.component.css'
})
export class VehiclesListComponent implements OnInit {
  private readonly vehicleService = inject(VehicleService);
  private readonly messageService = inject(MessageService);
  private readonly router = inject(Router);

  readonly vehicles = signal<Vehicle[]>([]);
  readonly loading = signal(false);
  readonly searchPlate = signal('');

  ngOnInit(): void {
    this.loadVehicles();
  }

  loadVehicles(): void {
    this.loading.set(true);

    this.vehicleService.listVehicles(this.searchPlate()).subscribe({
      next: (vehicles) => {
        this.vehicles.set(vehicles);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Vehículos',
          detail: (error.error?.message as string) || 'No se pudo cargar la lista de vehículos.'
        });
      }
    });
  }

  search(): void {
    this.loadVehicles();
  }

  clearSearch(): void {
    this.searchPlate.set('');
    this.loadVehicles();
  }

  createVehicle(): void {
    void this.router.navigate(['/vehicles/new']);
  }

  viewVehicle(id: number): void {
    void this.router.navigate(['/vehicles', id]);
  }

  editVehicle(id: number): void {
    void this.router.navigate(['/vehicles', id, 'edit']);
  }
}
