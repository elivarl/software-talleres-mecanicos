import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputText } from 'primeng/inputtext';
import { finalize } from 'rxjs/operators';
import { CustomerUpsertRequest } from '../../../../core/models/customer.model';
import { CustomerService } from '../../../../core/services/customer.service';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-customer-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PageHeaderComponent,
    LoadingStateComponent,
    Card,
    InputText,
    Button
  ],
  templateUrl: './customer-form.component.html',
  styleUrl: './customer-form.component.css'
})
export class CustomerFormComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly customerService = inject(CustomerService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly messageService = inject(MessageService);

  readonly customerId = signal<number | null>(null);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly isEditMode = computed(() => this.customerId() !== null);

  readonly customerForm = this.formBuilder.nonNullable.group({
    fullName: ['', [Validators.required, Validators.maxLength(150)]],
    identification: ['', [Validators.maxLength(50)]],
    phone: ['', [Validators.required, Validators.maxLength(50)]],
    email: ['', [Validators.email, Validators.maxLength(150)]],
    address: ['', [Validators.maxLength(255)]]
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (!idParam) {
      return;
    }

    this.customerId.set(Number(idParam));
    this.loadCustomer();
  }

  submit(): void {
    if (this.customerForm.invalid) {
      this.customerForm.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    const payload = this.buildPayload();

    const request$ = this.isEditMode()
      ? this.customerService.updateCustomer(this.customerId()!, payload)
      : this.customerService.createCustomer(payload);

    request$
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: (customer) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Clientes',
            detail: this.isEditMode()
              ? 'Cliente actualizado correctamente.'
              : 'Cliente creado correctamente.'
          });
          void this.router.navigate(['/customers', customer.id]);
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Clientes',
            detail: this.resolveErrorMessage(error, 'No se pudo guardar el cliente.')
          });
        }
      });
  }

  cancel(): void {
    if (this.isEditMode()) {
      void this.router.navigate(['/customers', this.customerId()]);
      return;
    }

    void this.router.navigate(['/customers']);
  }

  hasError(
    controlName: 'fullName' | 'identification' | 'phone' | 'email' | 'address',
    errorCode: string
  ): boolean {
    const control = this.customerForm.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }

  private loadCustomer(): void {
    this.loading.set(true);

    this.customerService.getCustomerById(this.customerId()!).subscribe({
      next: (customer) => {
        this.customerForm.patchValue({
          fullName: customer.fullName,
          identification: customer.identification || '',
          phone: customer.phone,
          email: customer.email || '',
          address: customer.address || ''
        });
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Clientes',
          detail: this.resolveErrorMessage(error, 'No se pudo cargar el cliente.')
        });
        void this.router.navigate(['/customers']);
      }
    });
  }

  private buildPayload(): CustomerUpsertRequest {
    const rawValue = this.customerForm.getRawValue();

    return {
      fullName: rawValue.fullName.trim(),
      identification: rawValue.identification.trim() || undefined,
      phone: rawValue.phone.trim(),
      email: rawValue.email.trim() || undefined,
      address: rawValue.address.trim() || undefined
    };
  }

  private resolveErrorMessage(error: HttpErrorResponse, fallback: string): string {
    return (error.error?.message as string) || fallback;
  }
}
