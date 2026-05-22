import { CommonModule, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { DatePicker } from 'primeng/datepicker';
import { InputText } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { Toolbar } from 'primeng/toolbar';
import { Customer } from '../../../../core/models/customer.model';
import {
  getWorkOrderStatusLabel,
  getWorkOrderStatusSeverity,
  WorkOrder,
  WORK_ORDER_STATUS_OPTIONS,
  WorkOrderStatus
} from '../../../../core/models/work-order.model';
import { CustomerService } from '../../../../core/services/customer.service';
import { WorkOrderService } from '../../../../core/services/work-order.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-work-orders-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DatePipe,
    PageHeaderComponent,
    EmptyStateComponent,
    Card,
    Toolbar,
    TableModule,
    Button,
    InputText,
    Select,
    DatePicker,
    Tag
  ],
  templateUrl: './work-orders-list.component.html',
  styleUrl: './work-orders-list.component.css'
})
export class WorkOrdersListComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly workOrderService = inject(WorkOrderService);
  private readonly customerService = inject(CustomerService);
  private readonly messageService = inject(MessageService);
  private readonly router = inject(Router);

  readonly workOrders = signal<WorkOrder[]>([]);
  readonly customers = signal<Customer[]>([]);
  readonly loading = signal(false);
  readonly statusOptions = [
    { label: 'Todos los estados', value: null as WorkOrderStatus | null },
    ...WORK_ORDER_STATUS_OPTIONS
  ];

  readonly filtersForm = this.formBuilder.group({
    status: [null as WorkOrderStatus | null],
    plate: [''],
    customerId: [null as number | null],
    receptionDateFrom: [null as Date | null],
    receptionDateTo: [null as Date | null]
  });

  ngOnInit(): void {
    this.loadCustomers();
    this.loadWorkOrders();
  }

  loadWorkOrders(): void {
    const rawValue = this.filtersForm.getRawValue();

    this.loading.set(true);
    this.workOrderService
      .listWorkOrders({
        status: rawValue.status ?? undefined,
        plate: rawValue.plate?.trim() || undefined,
        customerId: rawValue.customerId ?? undefined,
        receptionDateFrom: this.formatDate(rawValue.receptionDateFrom),
        receptionDateTo: this.formatDate(rawValue.receptionDateTo)
      })
      .subscribe({
        next: (workOrders) => {
          this.workOrders.set(workOrders);
          this.loading.set(false);
        },
        error: (error: HttpErrorResponse) => {
          this.loading.set(false);
          this.messageService.add({
            severity: 'error',
            summary: 'Órdenes de trabajo',
            detail:
              (error.error?.message as string) ||
              'No se pudo cargar la lista de órdenes de trabajo.'
          });
        }
      });
  }

  search(): void {
    this.loadWorkOrders();
  }

  clearFilters(): void {
    this.filtersForm.reset({
      status: null,
      plate: '',
      customerId: null,
      receptionDateFrom: null,
      receptionDateTo: null
    });
    this.loadWorkOrders();
  }

  createWorkOrder(): void {
    void this.router.navigate(['/work-orders/new']);
  }

  viewWorkOrder(id: number): void {
    void this.router.navigate(['/work-orders', id]);
  }

  getStatusLabel(status: WorkOrderStatus): string {
    return getWorkOrderStatusLabel(status);
  }

  getStatusSeverity(status: WorkOrderStatus) {
    return getWorkOrderStatusSeverity(status);
  }

  private loadCustomers(): void {
    this.customerService.listCustomers().subscribe({
      next: (customers) => this.customers.set(customers),
      error: () => {
        this.messageService.add({
          severity: 'warn',
          summary: 'Órdenes de trabajo',
          detail: 'No se pudo cargar el filtro de clientes.'
        });
      }
    });
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
