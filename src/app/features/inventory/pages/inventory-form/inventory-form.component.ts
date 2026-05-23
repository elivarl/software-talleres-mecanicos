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
import { Textarea } from 'primeng/textarea';
import { finalize } from 'rxjs/operators';
import {
  CreateInventoryItemRequest,
  UpdateInventoryItemRequest
} from '../../../../core/models/inventory.model';
import { InventoryService } from '../../../../core/services/inventory.service';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-inventory-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PageHeaderComponent,
    LoadingStateComponent,
    Card,
    InputText,
    InputNumber,
    Select,
    Textarea,
    Button
  ],
  templateUrl: './inventory-form.component.html',
  styleUrl: './inventory-form.component.css'
})
export class InventoryFormComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly inventoryService = inject(InventoryService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly messageService = inject(MessageService);

  readonly inventoryItemId = signal<number | null>(null);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly isEditMode = computed(() => this.inventoryItemId() !== null);
  readonly activeOptions = [
    { label: 'Activo', value: true },
    { label: 'Inactivo', value: false }
  ];

  readonly inventoryForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(150)]],
    sku: ['', [Validators.required, Validators.maxLength(80)]],
    description: ['', [Validators.maxLength(255)]],
    currentStock: [0, [Validators.required, Validators.min(0)]],
    minStock: [0, [Validators.required, Validators.min(0)]],
    unitCost: [0, [Validators.required, Validators.min(0)]],
    salePrice: [0, [Validators.required, Validators.min(0)]],
    active: [true]
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (!idParam) {
      return;
    }

    this.inventoryItemId.set(Number(idParam));
    this.loadInventoryItem();
  }

  submit(): void {
    if (this.inventoryForm.invalid) {
      this.inventoryForm.markAllAsTouched();
      return;
    }

    this.submitting.set(true);

    const request$ = this.isEditMode()
      ? this.inventoryService.updateInventoryItem(this.inventoryItemId()!, this.buildUpdatePayload())
      : this.inventoryService.createInventoryItem(this.buildCreatePayload());

    request$
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Inventario',
            detail: this.isEditMode()
              ? 'Repuesto actualizado correctamente.'
              : 'Repuesto creado correctamente.'
          });
          void this.router.navigate(['/inventory']);
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Inventario',
            detail: this.resolveErrorMessage(error, 'No se pudo guardar el repuesto.')
          });
        }
      });
  }

  cancel(): void {
    void this.router.navigate(['/inventory']);
  }

  hasError(
    controlName:
      | 'name'
      | 'sku'
      | 'description'
      | 'currentStock'
      | 'minStock'
      | 'unitCost'
      | 'salePrice'
      | 'active',
    errorCode: string
  ): boolean {
    const control = this.inventoryForm.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }

  private loadInventoryItem(): void {
    this.loading.set(true);

    this.inventoryService.getInventoryItemById(this.inventoryItemId()!).subscribe({
      next: (item) => {
        this.inventoryForm.patchValue({
          name: item.name,
          sku: item.sku,
          description: item.description || '',
          currentStock: item.currentStock,
          minStock: item.minStock,
          unitCost: item.unitCost,
          salePrice: item.salePrice,
          active: item.active
        });
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Inventario',
          detail: this.resolveErrorMessage(error, 'No se pudo cargar el repuesto.')
        });
        void this.router.navigate(['/inventory']);
      }
    });
  }

  private buildCreatePayload(): CreateInventoryItemRequest {
    const rawValue = this.inventoryForm.getRawValue();

    return {
      name: rawValue.name.trim(),
      sku: rawValue.sku.trim(),
      description: rawValue.description.trim() || undefined,
      currentStock: rawValue.currentStock,
      minStock: rawValue.minStock,
      unitCost: rawValue.unitCost,
      salePrice: rawValue.salePrice,
      active: rawValue.active
    };
  }

  private buildUpdatePayload(): UpdateInventoryItemRequest {
    const rawValue = this.inventoryForm.getRawValue();

    return {
      name: rawValue.name.trim(),
      sku: rawValue.sku.trim(),
      description: rawValue.description.trim() || undefined,
      currentStock: rawValue.currentStock,
      minStock: rawValue.minStock,
      unitCost: rawValue.unitCost,
      salePrice: rawValue.salePrice,
      active: rawValue.active
    };
  }

  private resolveErrorMessage(error: HttpErrorResponse, fallback: string): string {
    return (error.error?.message as string) || fallback;
  }
}
