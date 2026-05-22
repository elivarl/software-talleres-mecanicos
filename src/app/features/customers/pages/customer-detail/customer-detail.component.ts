import { CommonModule, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Customer } from '../../../../core/models/customer.model';
import { CustomerService } from '../../../../core/services/customer.service';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-customer-detail',
  standalone: true,
  imports: [CommonModule, DatePipe, PageHeaderComponent, LoadingStateComponent, Card, Button],
  templateUrl: './customer-detail.component.html',
  styleUrl: './customer-detail.component.css'
})
export class CustomerDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly customerService = inject(CustomerService);
  private readonly messageService = inject(MessageService);

  readonly customer = signal<Customer | null>(null);
  readonly loading = signal(false);

  ngOnInit(): void {
    const customerId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadCustomer(customerId);
  }

  editCustomer(): void {
    void this.router.navigate(['/customers', this.customer()?.id, 'edit']);
  }

  backToList(): void {
    void this.router.navigate(['/customers']);
  }

  private loadCustomer(customerId: number): void {
    this.loading.set(true);

    this.customerService.getCustomerById(customerId).subscribe({
      next: (customer) => {
        this.customer.set(customer);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Clientes',
          detail: (error.error?.message as string) || 'No se pudo cargar el detalle del cliente.'
        });
        void this.router.navigate(['/customers']);
      }
    });
  }
}
