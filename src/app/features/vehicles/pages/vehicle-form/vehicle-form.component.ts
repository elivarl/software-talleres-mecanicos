import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputNumber } from 'primeng/inputnumber';
import { InputText } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import { finalize } from 'rxjs/operators';
import { Customer } from '../../../../core/models/customer.model';
import { VehicleUpsertRequest } from '../../../../core/models/vehicle.model';
import { CustomerService } from '../../../../core/services/customer.service';
import { VehicleService } from '../../../../core/services/vehicle.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-vehicle-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PageHeaderComponent,
    EmptyStateComponent,
    LoadingStateComponent,
    Card,
    InputText,
    InputNumber,
    Select,
    Button
  ],
  templateUrl: './vehicle-form.component.html',
  styleUrl: './vehicle-form.component.css'
})
export class VehicleFormComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly vehicleService = inject(VehicleService);
  private readonly customerService = inject(CustomerService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly messageService = inject(MessageService);

  readonly vehicleId = signal<number | null>(null);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly customers = signal<Customer[]>([]);
  readonly isEditMode = computed(() => this.vehicleId() !== null);

  readonly vehicleForm = this.formBuilder.group({
    customerId: [null as number | null, [Validators.required]],
    plate: ['', [Validators.required, Validators.maxLength(20)]],
    brand: ['', [Validators.required, Validators.maxLength(100)]],
    model: ['', [Validators.required, Validators.maxLength(100)]],
    year: [null as number | null],
    color: ['', [Validators.maxLength(50)]],
    vin: ['', [Validators.maxLength(100)]],
    mileage: [null as number | null, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.loadCustomers();

    const idParam = this.route.snapshot.paramMap.get('id');

    if (!idParam) {
      return;
    }

    this.vehicleId.set(Number(idParam));
    this.loadVehicle();
  }

  submit(): void {
    if (this.vehicleForm.invalid || this.customers().length === 0) {
      this.vehicleForm.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    const payload = this.buildPayload();

    const request$ = this.isEditMode()
      ? this.vehicleService.updateVehicle(this.vehicleId()!, payload)
      : this.vehicleService.createVehicle(payload);

    request$
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: (vehicle) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Vehículos',
            detail: this.isEditMode()
              ? 'Vehículo actualizado correctamente.'
              : 'Vehículo creado correctamente.'
          });
          void this.router.navigate(['/vehicles', vehicle.id]);
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Vehículos',
            detail: (error.error?.message as string) || 'No se pudo guardar el vehículo.'
          });
        }
      });
  }

  cancel(): void {
    if (this.isEditMode()) {
      void this.router.navigate(['/vehicles', this.vehicleId()]);
      return;
    }

    void this.router.navigate(['/vehicles']);
  }

  hasError(
    controlName:
      | 'customerId'
      | 'plate'
      | 'brand'
      | 'model'
      | 'year'
      | 'color'
      | 'vin'
      | 'mileage',
    errorCode: string
  ): boolean {
    const control = this.vehicleForm.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }

  private loadCustomers(): void {
    this.customerService.listCustomers().subscribe({
      next: (customers) => this.customers.set(customers),
      error: (error: HttpErrorResponse) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Vehículos',
          detail: (error.error?.message as string) || 'No se pudo cargar la lista de clientes.'
        });
      }
    });
  }

  private loadVehicle(): void {
    this.loading.set(true);

    this.vehicleService.getVehicleById(this.vehicleId()!).subscribe({
      next: (vehicle) => {
        this.vehicleForm.patchValue({
          customerId: vehicle.customerId,
          plate: vehicle.plate,
          brand: vehicle.brand,
          model: vehicle.model,
          year: vehicle.year ?? null,
          color: vehicle.color ?? '',
          vin: vehicle.vin ?? '',
          mileage: vehicle.mileage
        });
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Vehículos',
          detail: (error.error?.message as string) || 'No se pudo cargar el vehículo.'
        });
        void this.router.navigate(['/vehicles']);
      }
    });
  }

  private buildPayload(): VehicleUpsertRequest {
    const rawValue = this.vehicleForm.getRawValue();

    return {
      customerId: rawValue.customerId!,
      plate: rawValue.plate!.trim(),
      brand: rawValue.brand!.trim(),
      model: rawValue.model!.trim(),
      year: rawValue.year ?? undefined,
      color: rawValue.color?.trim() || undefined,
      vin: rawValue.vin?.trim() || undefined,
      mileage: rawValue.mileage!
    };
  }
}
