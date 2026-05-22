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
import { Customer } from '../../../../core/models/customer.model';
import { CustomerService } from '../../../../core/services/customer.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-customers-list',
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
  templateUrl: './customers-list.component.html',
  styleUrl: './customers-list.component.css'
})
export class CustomersListComponent implements OnInit {
  private readonly customerService = inject(CustomerService);
  private readonly messageService = inject(MessageService);
  private readonly router = inject(Router);

  readonly customers = signal<Customer[]>([]);
  readonly loading = signal(false);
  readonly searchTerm = signal('');

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading.set(true);

    this.customerService.listCustomers(this.searchTerm()).subscribe({
      next: (customers) => {
        this.customers.set(customers);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Clientes',
          detail: this.resolveErrorMessage(error, 'No se pudo cargar la lista de clientes.')
        });
      }
    });
  }

  search(): void {
    this.loadCustomers();
  }

  clearSearch(): void {
    this.searchTerm.set('');
    this.loadCustomers();
  }

  createCustomer(): void {
    void this.router.navigate(['/customers/new']);
  }

  viewCustomer(id: number): void {
    void this.router.navigate(['/customers', id]);
  }

  editCustomer(id: number): void {
    void this.router.navigate(['/customers', id, 'edit']);
  }

  private resolveErrorMessage(error: HttpErrorResponse, fallback: string): string {
    return (error.error?.message as string) || fallback;
  }
}
