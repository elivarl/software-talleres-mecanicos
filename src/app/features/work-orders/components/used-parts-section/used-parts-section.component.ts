import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputNumber } from 'primeng/inputnumber';
import { Select } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../../../core/auth/auth.service';
import { InventoryItem } from '../../../../core/models/inventory.model';
import { WorkOrderStatus } from '../../../../core/models/work-order.model';
import { WorkOrderPart } from '../../../../core/models/used-part.model';
import { InventoryService } from '../../../../core/services/inventory.service';
import { UsedPartService } from '../../../../core/services/used-part.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-used-parts-section',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CurrencyPipe,
    DatePipe,
    EmptyStateComponent,
    Card,
    TableModule,
    Select,
    InputNumber,
    Button,
    Tag
  ],
  templateUrl: './used-parts-section.component.html',
  styleUrl: './used-parts-section.component.css'
})
export class UsedPartsSectionComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly inventoryService = inject(InventoryService);
  private readonly usedPartService = inject(UsedPartService);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);

  readonly workOrderId = input.required<number>();
  readonly workOrderStatus = input.required<WorkOrderStatus>();

  readonly parts = signal<WorkOrderPart[]>([]);
  readonly inventoryItems = signal<InventoryItem[]>([]);
  readonly loading = signal(false);
  readonly inventoryLoading = signal(false);
  readonly submitting = signal(false);
  readonly deletingPartId = signal<number | null>(null);
  readonly currentUser = this.authService.currentUser;
  readonly inventoryOptions = computed(() =>
    this.inventoryItems().map((item) => ({
      label: `${item.name} (${item.sku}) · Stock: ${item.currentStock}`,
      value: item.id
    }))
  );
  readonly isEditableStatus = computed(() => {
    const status = this.workOrderStatus();
    return status === 'APPROVED' || status === 'IN_PROGRESS';
  });
  readonly canAddParts = computed(() => {
    const role = this.currentUser()?.role;

    return role === 'ADMIN' && this.isEditableStatus();
  });
  readonly canDeleteParts = computed(() => {
    const role = this.currentUser()?.role;

    return (role === 'ADMIN' || role === 'MECHANIC') && this.isEditableStatus();
  });
  readonly statusMessage = computed(() => {
    const role = this.currentUser()?.role;

    if (this.canAddParts()) {
      return 'Puedes registrar y retirar repuestos usados en esta orden.';
    }

    if (role === 'MECHANIC' && this.isEditableStatus()) {
      return 'El backend actual no expone el catálogo de inventario para mecánicos, así que solo pueden ver o retirar registros existentes.';
    }

    return 'Los repuestos usados solo se registran cuando la orden está aprobada o en progreso.';
  });
  readonly totalAmount = computed(() =>
    this.parts().reduce((sum, part) => sum + (part.total ?? part.quantity * part.salePrice), 0)
  );
  readonly totalMargin = computed(() =>
    this.parts().reduce((sum, part) => sum + (part.margin ?? 0), 0)
  );

  readonly partForm = this.formBuilder.group({
    inventoryItemId: [null as number | null, [Validators.required]],
    quantity: [1, [Validators.required, Validators.min(0.01)]]
  });

  ngOnInit(): void {
    this.loadParts();

    if (this.canAddParts()) {
      this.loadInventoryItems();
    }
  }

  submit(): void {
    if (this.partForm.invalid || !this.canAddParts()) {
      this.partForm.markAllAsTouched();
      return;
    }

    const rawValue = this.partForm.getRawValue();

    this.submitting.set(true);
    this.usedPartService
      .registerUsedPart(this.workOrderId(), {
        inventoryItemId: Number(rawValue.inventoryItemId),
        quantity: rawValue.quantity ?? 0
      })
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: (part) => {
          this.parts.update((parts) => [part, ...parts]);
          this.partForm.reset({
            inventoryItemId: null,
            quantity: 1
          });
          if (this.canAddParts()) {
            this.loadInventoryItems();
          }
          this.messageService.add({
            severity: 'success',
            summary: 'Repuestos usados',
            detail: 'Repuesto registrado correctamente en la orden.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Repuestos usados',
            detail: this.resolveRegisterError(error)
          });
        }
      });
  }

  confirmDelete(part: WorkOrderPart): void {
    this.confirmationService.confirm({
      header: 'Eliminar repuesto usado',
      message: `Se retirará ${part.inventoryItemName} del consumo registrado en la orden.`,
      acceptLabel: 'Eliminar',
      rejectLabel: 'Cancelar',
      acceptButtonProps: { severity: 'danger' },
      rejectButtonProps: { severity: 'secondary', outlined: true },
      accept: () => this.deletePart(part.id)
    });
  }

  hasError(controlName: 'inventoryItemId' | 'quantity', errorCode: string): boolean {
    const control = this.partForm.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }

  private loadParts(): void {
    this.loading.set(true);

    this.usedPartService.listUsedParts(this.workOrderId()).subscribe({
      next: (parts) => {
        this.parts.set(parts);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Repuestos usados',
          detail:
            (error.error?.message as string) ||
            'No se pudo cargar el consumo de repuestos de esta orden.'
        });
      }
    });
  }

  private loadInventoryItems(): void {
    this.inventoryLoading.set(true);

    this.inventoryService.listInventory().subscribe({
      next: (items) => {
        this.inventoryItems.set(
          items
            .filter((item) => item.active)
            .sort((left, right) => left.name.localeCompare(right.name))
        );
        this.inventoryLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.inventoryLoading.set(false);
        this.messageService.add({
          severity: 'warn',
          summary: 'Repuestos usados',
          detail:
            (error.error?.message as string) ||
            'No se pudo cargar el catálogo de repuestos activos.'
        });
      }
    });
  }

  private deletePart(partId: number): void {
    if (!this.canDeleteParts()) {
      return;
    }

    this.deletingPartId.set(partId);

    this.usedPartService
      .deleteUsedPart(this.workOrderId(), partId)
      .pipe(finalize(() => this.deletingPartId.set(null)))
      .subscribe({
        next: () => {
          this.parts.update((parts) => parts.filter((part) => part.id !== partId));
          if (this.canAddParts()) {
            this.loadInventoryItems();
          }
          this.messageService.add({
            severity: 'success',
            summary: 'Repuestos usados',
            detail: 'Repuesto retirado correctamente de la orden.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Repuestos usados',
            detail:
              (error.error?.message as string) ||
              'No se pudo eliminar el repuesto usado de la orden.'
          });
        }
      });
  }

  private resolveRegisterError(error: HttpErrorResponse): string {
    const backendMessage = error.error?.message as string | undefined;

    if (backendMessage) {
      return backendMessage;
    }

    if (error.status === 400) {
      return 'Stock insuficiente o cantidad inválida para registrar el repuesto.';
    }

    return 'No se pudo registrar el repuesto usado.';
  }
}
