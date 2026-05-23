import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputNumber } from 'primeng/inputnumber';
import { InputText } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../../../core/auth/auth.service';
import { LaborItem } from '../../../../core/models/labor.model';
import { WorkOrderStatus } from '../../../../core/models/work-order.model';
import { LaborService } from '../../../../core/services/labor.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-labor-section',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CurrencyPipe,
    DatePipe,
    EmptyStateComponent,
    Card,
    TableModule,
    InputText,
    InputNumber,
    Button,
    Tag
  ],
  templateUrl: './labor-section.component.html',
  styleUrl: './labor-section.component.css'
})
export class LaborSectionComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly laborService = inject(LaborService);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);

  readonly workOrderId = input.required<number>();
  readonly workOrderStatus = input.required<WorkOrderStatus>();

  readonly laborItems = signal<LaborItem[]>([]);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly deletingLaborId = signal<number | null>(null);
  readonly currentUser = this.authService.currentUser;
  readonly isEditableStatus = computed(() => {
    const status = this.workOrderStatus();
    return status === 'APPROVED' || status === 'IN_PROGRESS';
  });
  readonly canAddLabor = computed(() => {
    const role = this.currentUser()?.role;
    return (role === 'ADMIN' || role === 'MECHANIC') && this.isEditableStatus();
  });
  readonly canDeleteLabor = computed(() => {
    const role = this.currentUser()?.role;
    return (role === 'ADMIN' || role === 'MECHANIC') && this.isEditableStatus();
  });
  readonly totalLabor = computed(() =>
    this.laborItems().reduce((sum, item) => sum + item.price, 0)
  );
  readonly statusMessage = computed(() => {
    if (this.canAddLabor()) {
      return 'Puedes registrar y retirar mano de obra en esta orden.';
    }

    return 'La mano de obra solo se registra cuando la orden está aprobada o en progreso.';
  });

  readonly laborForm = this.formBuilder.nonNullable.group({
    description: ['', [Validators.required, Validators.pattern(/.*\S.*/)]],
    price: [0, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.loadLaborItems();
  }

  submit(): void {
    if (this.laborForm.invalid || !this.canAddLabor()) {
      this.laborForm.markAllAsTouched();
      return;
    }

    const rawValue = this.laborForm.getRawValue();

    this.submitting.set(true);
    this.laborService
      .registerLaborItem(this.workOrderId(), {
        description: rawValue.description.trim(),
        price: rawValue.price
      })
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: (laborItem) => {
          this.laborItems.update((items) => [laborItem, ...items]);
          this.laborForm.reset({
            description: '',
            price: 0
          });
          this.messageService.add({
            severity: 'success',
            summary: 'Mano de obra',
            detail: 'Mano de obra registrada correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Mano de obra',
            detail:
              (error.error?.message as string) || 'No se pudo registrar la mano de obra.'
          });
        }
      });
  }

  confirmDelete(item: LaborItem): void {
    this.confirmationService.confirm({
      header: 'Eliminar mano de obra',
      message: `Se eliminará "${item.description}" de esta orden.`,
      acceptLabel: 'Eliminar',
      rejectLabel: 'Cancelar',
      acceptButtonProps: { severity: 'danger' },
      rejectButtonProps: { severity: 'secondary', outlined: true },
      accept: () => this.deleteLaborItem(item.id)
    });
  }

  hasError(controlName: 'description' | 'price', errorCode: string): boolean {
    const control = this.laborForm.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }

  private loadLaborItems(): void {
    this.loading.set(true);

    this.laborService.listLaborItems(this.workOrderId()).subscribe({
      next: (items) => {
        const sortedItems = [...items].sort((left, right) => {
          const leftDate = left.createdAt ? new Date(left.createdAt).getTime() : 0;
          const rightDate = right.createdAt ? new Date(right.createdAt).getTime() : 0;
          return rightDate - leftDate;
        });

        this.laborItems.set(sortedItems);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Mano de obra',
          detail:
            (error.error?.message as string) ||
            'No se pudo cargar la mano de obra registrada en esta orden.'
        });
      }
    });
  }

  private deleteLaborItem(laborId: number): void {
    if (!this.canDeleteLabor()) {
      return;
    }

    this.deletingLaborId.set(laborId);

    this.laborService
      .deleteLaborItem(this.workOrderId(), laborId)
      .pipe(finalize(() => this.deletingLaborId.set(null)))
      .subscribe({
        next: () => {
          this.laborItems.update((items) => items.filter((item) => item.id !== laborId));
          this.messageService.add({
            severity: 'success',
            summary: 'Mano de obra',
            detail: 'Registro de mano de obra eliminado correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Mano de obra',
            detail:
              (error.error?.message as string) ||
              'No se pudo eliminar la mano de obra de esta orden.'
          });
        }
      });
  }
}
