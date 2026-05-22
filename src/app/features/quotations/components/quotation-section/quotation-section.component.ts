import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Divider } from 'primeng/divider';
import { InputNumber } from 'primeng/inputnumber';
import { InputText } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { finalize } from 'rxjs/operators';
import {
  getQuotationItemTypeLabel,
  getQuotationStatusLabel,
  getQuotationStatusSeverity,
  QUOTATION_ITEM_TYPE_OPTIONS,
  Quotation,
  QuotationItem,
  QuotationItemRequest,
  QuotationStatus
} from '../../../../core/models/quotation.model';
import { WorkOrderStatus } from '../../../../core/models/work-order.model';
import { QuotationService } from '../../../../core/services/quotation.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-quotation-section',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CurrencyPipe,
    DatePipe,
    EmptyStateComponent,
    Card,
    TableModule,
    Tag,
    Button,
    InputText,
    InputNumber,
    Select,
    Divider
  ],
  templateUrl: './quotation-section.component.html',
  styleUrl: './quotation-section.component.css'
})
export class QuotationSectionComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly quotationService = inject(QuotationService);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);

  readonly workOrderId = input.required<number>();
  readonly workOrderStatus = input.required<WorkOrderStatus>();
  readonly canManage = input(false);

  readonly quotation = signal<Quotation | null>(null);
  readonly loading = signal(false);
  readonly creating = signal(false);
  readonly savingItem = signal(false);
  readonly deletingItemId = signal<number | null>(null);
  readonly sending = signal(false);
  readonly editingItemId = signal<number | null>(null);
  readonly itemTypeOptions = QUOTATION_ITEM_TYPE_OPTIONS;

  readonly canCreateQuotation = computed(
    () => this.canManage() && this.workOrderStatus() === 'DIAGNOSIS' && !this.quotation()
  );
  readonly canEditDraftQuotation = computed(
    () => this.canManage() && this.quotation()?.status === 'DRAFT'
  );
  readonly canSendQuotation = computed(
    () =>
      this.canManage() &&
      this.quotation()?.status === 'DRAFT' &&
      (this.quotation()?.items.length ?? 0) > 0
  );
  readonly hasUnresolvedQuotation = computed(() => {
    const status = this.workOrderStatus();
    return (
      !this.quotation() &&
      (status === 'QUOTED' || status === 'APPROVED' || status === 'REJECTED')
    );
  });
  readonly publicLink = computed(() => {
    const token = this.quotation()?.publicToken;
    const origin = globalThis.location?.origin;

    if (!token || !origin) {
      return '';
    }

    return `${origin}/public/quotations/${token}`;
  });

  readonly itemForm = this.formBuilder.nonNullable.group({
    type: ['PART' as QuotationItemRequest['type'], [Validators.required]],
    description: ['', [Validators.required, Validators.maxLength(255)]],
    quantity: [1, [Validators.required, Validators.min(0.01)]],
    unitPrice: [0, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.loadQuotationFromQueryParam();
  }

  createQuotation(): void {
    this.creating.set(true);
    this.quotationService
      .createQuotation(this.workOrderId())
      .pipe(finalize(() => this.creating.set(false)))
      .subscribe({
        next: (quotation) => {
          this.setQuotation(quotation);
          this.messageService.add({
            severity: 'success',
            summary: 'Cotización',
            detail: 'Cotización creada correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Cotización',
            detail: (error.error?.message as string) || 'No se pudo crear la cotización.'
          });
        }
      });
  }

  saveItem(): void {
    if (this.itemForm.invalid || !this.quotation()) {
      this.itemForm.markAllAsTouched();
      return;
    }

    this.savingItem.set(true);
    const quotationId = this.quotation()!.id;
    const editingItemId = this.editingItemId();
    const payload = this.buildItemPayload();

    const request$ = editingItemId
      ? this.quotationService.updateQuotationItem(quotationId, editingItemId, payload)
      : this.quotationService.addQuotationItem(quotationId, payload);

    request$
      .pipe(finalize(() => this.savingItem.set(false)))
      .subscribe({
        next: (quotation) => {
          this.setQuotation(quotation);
          this.cancelEdit();
          this.messageService.add({
            severity: 'success',
            summary: 'Cotización',
            detail: editingItemId
              ? 'Ítem actualizado correctamente.'
              : 'Ítem agregado correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Cotización',
            detail: (error.error?.message as string) || 'No se pudo guardar el ítem.'
          });
        }
      });
  }

  editItem(item: QuotationItem): void {
    this.editingItemId.set(item.id);
    this.itemForm.setValue({
      type: item.type,
      description: item.description,
      quantity: item.quantity,
      unitPrice: item.unitPrice
    });
  }

  cancelEdit(): void {
    this.editingItemId.set(null);
    this.itemForm.reset({
      type: 'PART',
      description: '',
      quantity: 1,
      unitPrice: 0
    });
  }

  deleteItem(item: QuotationItem): void {
    if (!this.quotation()) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Eliminar ítem',
      message: 'Este ítem se eliminará de la cotización mientras siga en borrador.',
      acceptLabel: 'Eliminar',
      rejectLabel: 'Cancelar',
      acceptButtonProps: { severity: 'danger' },
      rejectButtonProps: { severity: 'secondary', outlined: true },
      accept: () => this.confirmDeleteItem(item.id)
    });
  }

  sendQuotation(): void {
    if (!this.quotation()) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Enviar cotización',
      message:
        'La cotización quedará lista para aprobación pública y ya no podrá editarse como borrador.',
      acceptLabel: 'Enviar',
      rejectLabel: 'Cancelar',
      acceptButtonProps: { severity: 'primary' },
      rejectButtonProps: { severity: 'secondary', outlined: true },
      accept: () => this.confirmSendQuotation()
    });
  }

  copyPublicLink(): void {
    const link = this.publicLink();

    if (!link) {
      return;
    }

    if (!globalThis.navigator?.clipboard) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Cotización',
        detail: 'No se pudo copiar automáticamente el enlace público.'
      });
      return;
    }

    void globalThis.navigator.clipboard.writeText(link).then(
      () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Cotización',
          detail: 'Enlace público copiado correctamente.'
        });
      },
      () => {
        this.messageService.add({
          severity: 'warn',
          summary: 'Cotización',
          detail: 'No se pudo copiar automáticamente el enlace público.'
        });
      }
    );
  }

  hasItemError(
    controlName: 'type' | 'description' | 'quantity' | 'unitPrice',
    errorCode: string
  ): boolean {
    const control = this.itemForm.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }

  getStatusLabel(status: QuotationStatus): string {
    return getQuotationStatusLabel(status);
  }

  getStatusSeverity(status: QuotationStatus) {
    return getQuotationStatusSeverity(status);
  }

  getItemTypeLabel(type: QuotationItemRequest['type']): string {
    return getQuotationItemTypeLabel(type);
  }

  private loadQuotationFromQueryParam(): void {
    const quotationIdParam = this.route.snapshot.queryParamMap.get('quotationId');

    if (!quotationIdParam) {
      return;
    }

    const quotationId = Number(quotationIdParam);

    if (!quotationId) {
      return;
    }

    this.loading.set(true);
    this.quotationService
      .getQuotationById(quotationId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (quotation) => {
          if (quotation.workOrderId !== this.workOrderId()) {
            this.messageService.add({
              severity: 'warn',
              summary: 'Cotización',
              detail: 'La cotización indicada no corresponde a esta orden de trabajo.'
            });
            return;
          }

          this.quotation.set(quotation);
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'warn',
            summary: 'Cotización',
            detail:
              (error.error?.message as string) ||
              'No se pudo cargar la cotización asociada a esta orden.'
          });
        }
      });
  }

  private setQuotation(quotation: Quotation): void {
    this.quotation.set(quotation);
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { quotationId: quotation.id },
      queryParamsHandling: 'merge',
      replaceUrl: true
    });
  }

  private buildItemPayload(): QuotationItemRequest {
    const rawValue = this.itemForm.getRawValue();

    return {
      type: rawValue.type,
      description: rawValue.description.trim(),
      quantity: rawValue.quantity,
      unitPrice: rawValue.unitPrice
    };
  }

  private confirmDeleteItem(itemId: number): void {
    if (!this.quotation()) {
      return;
    }

    this.deletingItemId.set(itemId);
    this.quotationService
      .deleteQuotationItem(this.quotation()!.id, itemId)
      .pipe(finalize(() => this.deletingItemId.set(null)))
      .subscribe({
        next: () => {
          const currentQuotation = this.quotation();

          if (!currentQuotation) {
            return;
          }

          const updatedItems = currentQuotation.items.filter((item) => item.id !== itemId);
          const subtotal = updatedItems.reduce((sum, item) => sum + item.total, 0);
          const ratio = currentQuotation.subtotal > 0 ? currentQuotation.tax / currentQuotation.subtotal : 0;
          const tax = subtotal * ratio;

          this.quotation.set({
            ...currentQuotation,
            items: updatedItems,
            subtotal,
            tax,
            total: subtotal + tax
          });

          this.messageService.add({
            severity: 'success',
            summary: 'Cotización',
            detail: 'Ítem eliminado correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Cotización',
            detail: (error.error?.message as string) || 'No se pudo eliminar el ítem.'
          });
        }
      });
  }

  private confirmSendQuotation(): void {
    if (!this.quotation()) {
      return;
    }

    this.sending.set(true);
    this.quotationService
      .sendQuotation(this.quotation()!.id)
      .pipe(finalize(() => this.sending.set(false)))
      .subscribe({
        next: (quotation) => {
          this.setQuotation(quotation);
          this.messageService.add({
            severity: 'success',
            summary: 'Cotización',
            detail: 'Cotización enviada correctamente.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Cotización',
            detail: (error.error?.message as string) || 'No se pudo enviar la cotización.'
          });
        }
      });
  }
}
