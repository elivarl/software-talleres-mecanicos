import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputText } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { Toolbar } from 'primeng/toolbar';
import { AuthService } from '../../../../core/auth/auth.service';
import {
  getInventoryStockSeverity,
  InventoryItem,
  isInventoryLowStock
} from '../../../../core/models/inventory.model';
import { InventoryService } from '../../../../core/services/inventory.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    DatePipe,
    PageHeaderComponent,
    EmptyStateComponent,
    Card,
    Toolbar,
    TableModule,
    Button,
    InputText,
    Tag
  ],
  templateUrl: './inventory-list.component.html',
  styleUrl: './inventory-list.component.css'
})
export class InventoryListComponent implements OnInit {
  private readonly inventoryService = inject(InventoryService);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly inventoryItems = signal<InventoryItem[]>([]);
  readonly loading = signal(false);
  readonly deactivatingId = signal<number | null>(null);
  readonly searchTerm = signal('');
  readonly currentUser = this.authService.currentUser;
  readonly canManageInventory = computed(() => this.currentUser()?.role === 'ADMIN');
  readonly lowStockCount = computed(
    () => this.inventoryItems().filter((item) => isInventoryLowStock(item) && item.active).length
  );

  ngOnInit(): void {
    this.loadInventory();
  }

  loadInventory(): void {
    this.loading.set(true);

    this.inventoryService.listInventory(this.searchTerm()).subscribe({
      next: (items) => {
        this.inventoryItems.set(items);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Inventario',
          detail: this.resolveErrorMessage(error, 'No se pudo cargar el inventario.')
        });
      }
    });
  }

  search(): void {
    this.loadInventory();
  }

  clearSearch(): void {
    this.searchTerm.set('');
    this.loadInventory();
  }

  createInventoryItem(): void {
    void this.router.navigate(['/inventory/new']);
  }

  editInventoryItem(id: number): void {
    void this.router.navigate(['/inventory', id, 'edit']);
  }

  confirmDeactivate(item: InventoryItem): void {
    this.confirmationService.confirm({
      header: 'Desactivar repuesto',
      message: `El repuesto ${item.name} dejará de estar disponible para nuevos registros.`,
      acceptLabel: 'Desactivar',
      rejectLabel: 'Cancelar',
      acceptButtonProps: { severity: 'danger' },
      rejectButtonProps: { severity: 'secondary', outlined: true },
      accept: () => this.deactivateInventoryItem(item)
    });
  }

  isLowStock(item: InventoryItem): boolean {
    return isInventoryLowStock(item);
  }

  getStockSeverity(item: InventoryItem) {
    return getInventoryStockSeverity(item);
  }

  private deactivateInventoryItem(item: InventoryItem): void {
    this.deactivatingId.set(item.id);

    this.inventoryService.deactivateInventoryItem(item.id).subscribe({
      next: (updatedItem) => {
        this.inventoryItems.update((items) =>
          items.map((currentItem) => (currentItem.id === updatedItem.id ? updatedItem : currentItem))
        );
        this.deactivatingId.set(null);
        this.messageService.add({
          severity: 'success',
          summary: 'Inventario',
          detail: 'Repuesto desactivado correctamente.'
        });
      },
      error: (error: HttpErrorResponse) => {
        this.deactivatingId.set(null);
        this.messageService.add({
          severity: 'error',
          summary: 'Inventario',
          detail: this.resolveErrorMessage(error, 'No se pudo desactivar el repuesto.')
        });
      }
    });
  }

  private resolveErrorMessage(error: HttpErrorResponse, fallback: string): string {
    return (error.error?.message as string) || fallback;
  }
}
