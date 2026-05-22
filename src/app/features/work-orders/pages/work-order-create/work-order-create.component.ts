import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { DatePicker } from 'primeng/datepicker';
import { InputNumber } from 'primeng/inputnumber';
import { InputText } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import { Textarea } from 'primeng/textarea';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../../../core/auth/auth.service';
import { Customer } from '../../../../core/models/customer.model';
import { User } from '../../../../core/models/user.model';
import { Vehicle } from '../../../../core/models/vehicle.model';
import { WorkOrderCreateRequest } from '../../../../core/models/work-order.model';
import { CustomerService } from '../../../../core/services/customer.service';
import { UserService } from '../../../../core/services/user.service';
import { VehicleService } from '../../../../core/services/vehicle.service';
import { WorkOrderService } from '../../../../core/services/work-order.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-work-order-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PageHeaderComponent,
    EmptyStateComponent,
    LoadingStateComponent,
    Card,
    Select,
    DatePicker,
    InputNumber,
    InputText,
    Textarea,
    Button
  ],
  templateUrl: './work-order-create.component.html',
  styleUrl: './work-order-create.component.css'
})
export class WorkOrderCreateComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly customerService = inject(CustomerService);
  private readonly vehicleService = inject(VehicleService);
  private readonly userService = inject(UserService);
  private readonly workOrderService = inject(WorkOrderService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly messageService = inject(MessageService);
  private readonly destroyRef = inject(DestroyRef);

  readonly customers = signal<Customer[]>([]);
  readonly vehicles = signal<Vehicle[]>([]);
  readonly mechanics = signal<User[]>([]);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly currentUser = this.authService.currentUser;
  readonly canSelectMechanic = computed(() => this.currentUser()?.role === 'ADMIN');
  readonly filteredVehicles = computed(() => {
    const customerId = this.workOrderForm.controls.customerId.value;

    if (!customerId) {
      return [];
    }

    return this.vehicles().filter((vehicle) => vehicle.customerId === customerId);
  });

  readonly workOrderForm = this.formBuilder.group({
    customerId: [null as number | null, [Validators.required]],
    vehicleId: [null as number | null, [Validators.required]],
    assignedMechanicId: [null as number | null],
    receptionDate: [new Date()],
    estimatedDeliveryDate: [null as Date | null],
    currentMileage: [null as number | null, [Validators.required, Validators.min(0)]],
    fuelLevel: ['', [Validators.maxLength(50)]],
    customerComplaint: ['', [Validators.required]],
    initialObservations: ['']
  });

  ngOnInit(): void {
    this.loadDependencies();

    this.workOrderForm.controls.customerId.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((customerId) => {
        const vehicleId = this.workOrderForm.controls.vehicleId.value;

        if (!customerId || !vehicleId) {
          return;
        }

        const vehicleBelongsToCustomer = this.vehicles().some(
          (vehicle) => vehicle.id === vehicleId && vehicle.customerId === customerId
        );

        if (!vehicleBelongsToCustomer) {
          this.workOrderForm.controls.vehicleId.setValue(null);
        }
      });
  }

  submit(): void {
    if (this.workOrderForm.invalid) {
      this.workOrderForm.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.workOrderService
      .createWorkOrder(this.buildPayload())
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: (workOrder) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Órdenes de trabajo',
            detail: 'Orden de trabajo creada correctamente.'
          });
          void this.router.navigate(['/work-orders', workOrder.id]);
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Órdenes de trabajo',
            detail: (error.error?.message as string) || 'No se pudo crear la orden de trabajo.'
          });
        }
      });
  }

  cancel(): void {
    void this.router.navigate(['/work-orders']);
  }

  hasError(controlName: keyof typeof this.workOrderForm.controls, errorCode: string): boolean {
    const control = this.workOrderForm.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }

  formatVehicleLabel(vehicle: Vehicle): string {
    return `${vehicle.plate} · ${vehicle.brand} ${vehicle.model}`;
  }

  private loadDependencies(): void {
    this.loading.set(true);

    this.customerService.listCustomers().subscribe({
      next: (customers) => {
        this.customers.set(customers);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Órdenes de trabajo',
          detail: (error.error?.message as string) || 'No se pudo cargar la lista de clientes.'
        });
      }
    });

    this.vehicleService.listVehicles().subscribe({
      next: (vehicles) => this.vehicles.set(vehicles),
      error: (error: HttpErrorResponse) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Órdenes de trabajo',
          detail:
            (error.error?.message as string) || 'No se pudo cargar la lista de vehículos.'
        });
      }
    });

    if (this.canSelectMechanic()) {
      this.userService.listUsers().subscribe({
        next: (users) =>
          this.mechanics.set(
            users.filter((user) => user.role === 'MECHANIC' && user.active !== false)
          ),
        error: () => {
          this.messageService.add({
            severity: 'warn',
            summary: 'Órdenes de trabajo',
            detail: 'No se pudo cargar la lista de mecánicos.'
          });
        }
      });
    }
  }

  private buildPayload(): WorkOrderCreateRequest {
    const rawValue = this.workOrderForm.getRawValue();

    return {
      customerId: rawValue.customerId!,
      vehicleId: rawValue.vehicleId!,
      assignedMechanicId: rawValue.assignedMechanicId ?? undefined,
      receptionDate: this.formatDateTime(rawValue.receptionDate),
      estimatedDeliveryDate: this.formatDate(rawValue.estimatedDeliveryDate),
      currentMileage: rawValue.currentMileage!,
      fuelLevel: rawValue.fuelLevel?.trim() || undefined,
      customerComplaint: rawValue.customerComplaint?.trim() || '',
      initialObservations: rawValue.initialObservations?.trim() || undefined
    };
  }

  private formatDateTime(date: Date | null): string | undefined {
    if (!date) {
      return undefined;
    }

    const year = date.getFullYear();
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');
    const hours = `${date.getHours()}`.padStart(2, '0');
    const minutes = `${date.getMinutes()}`.padStart(2, '0');
    const seconds = `${date.getSeconds()}`.padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  }

  private formatDate(date: Date | null): string | undefined {
    if (!date) {
      return undefined;
    }

    const year = date.getFullYear();
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');

    return `${year}-${month}-${day}`;
  }
}
