import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Divider } from 'primeng/divider';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import {
  getQuotationItemTypeLabel,
  getQuotationStatusLabel,
  getQuotationStatusSeverity,
  Quotation
} from '../../../../core/models/quotation.model';
import { PublicQuotationService } from '../../../../core/services/public-quotation.service';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';

@Component({
  selector: 'app-public-quotation',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    DatePipe,
    Card,
    Divider,
    TableModule,
    Tag,
    Button,
    EmptyStateComponent,
    LoadingStateComponent
  ],
  templateUrl: './public-quotation.component.html',
  styleUrl: './public-quotation.component.css'
})
export class PublicQuotationComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly publicQuotationService = inject(PublicQuotationService);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);

  readonly quotation = signal<Quotation | null>(null);
  readonly loading = signal(false);
  readonly deciding = signal(false);
  readonly loadError = signal<string | null>(null);
  readonly statusMessage = computed(() => {
    const quotation = this.quotation();

    if (!quotation) {
      return '';
    }

    switch (quotation.status) {
      case 'APPROVED':
        return 'La cotización ya fue aprobada. Gracias por tu confirmación.';
      case 'REJECTED':
        return 'La cotización fue rechazada. El taller podrá contactarte para revisar alternativas.';
      case 'EXPIRED':
        return 'La cotización ya expiró y necesita revisión del taller.';
      case 'SENT':
        return 'Revisa el detalle y confirma tu decisión cuando estés listo.';
      default:
        return 'La cotización aún no está lista para una decisión pública.';
    }
  });
  readonly canDecide = computed(() => this.quotation()?.status === 'SENT');

  ngOnInit(): void {
    const token = this.route.snapshot.paramMap.get('token');

    if (!token) {
      this.loadError.set('El enlace de la cotización no es válido o está incompleto.');
      return;
    }

    this.loadQuotation(token);
  }

  approve(): void {
    const token = this.route.snapshot.paramMap.get('token');

    if (!token) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Aprobar cotización',
      message: 'Confirmarás la aprobación de esta cotización.',
      acceptLabel: 'Aprobar',
      rejectLabel: 'Cancelar',
      acceptButtonProps: { severity: 'success' },
      rejectButtonProps: { severity: 'secondary', outlined: true },
      accept: () => this.resolveQuotation(token, 'approve')
    });
  }

  reject(): void {
    const token = this.route.snapshot.paramMap.get('token');

    if (!token) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Rechazar cotización',
      message: 'Confirmarás el rechazo de esta cotización.',
      acceptLabel: 'Rechazar',
      rejectLabel: 'Cancelar',
      acceptButtonProps: { severity: 'danger' },
      rejectButtonProps: { severity: 'secondary', outlined: true },
      accept: () => this.resolveQuotation(token, 'reject')
    });
  }

  getStatusLabel(status: Quotation['status']): string {
    return getQuotationStatusLabel(status);
  }

  getStatusSeverity(status: Quotation['status']) {
    return getQuotationStatusSeverity(status);
  }

  getItemTypeLabel(type: Quotation['items'][number]['type']): string {
    return getQuotationItemTypeLabel(type);
  }

  private loadQuotation(token: string): void {
    this.loading.set(true);
    this.loadError.set(null);
    this.publicQuotationService.getQuotationByToken(token).subscribe({
      next: (quotation) => {
        this.quotation.set(quotation);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.loadError.set(
          (error.error?.message as string) || 'No se pudo cargar la cotización pública.'
        );
        this.messageService.add({
          severity: 'error',
          summary: 'Cotización pública',
          detail: (error.error?.message as string) || 'No se pudo cargar la cotización pública.'
        });
      }
    });
  }

  private resolveQuotation(token: string, action: 'approve' | 'reject'): void {
    this.deciding.set(true);

    const request$ =
      action === 'approve'
        ? this.publicQuotationService.approveQuotation(token)
        : this.publicQuotationService.rejectQuotation(token);

    request$.subscribe({
      next: (quotation) => {
        this.quotation.set(quotation);
        this.deciding.set(false);
        this.messageService.add({
          severity: 'success',
          summary: 'Cotización pública',
          detail:
            action === 'approve'
              ? 'Cotización aprobada correctamente.'
              : 'Cotización rechazada correctamente.'
        });
      },
      error: (error: HttpErrorResponse) => {
        this.deciding.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Cotización pública',
          detail:
            (error.error?.message as string) ||
            'No se pudo registrar la decisión de la cotización.'
        });
      }
    });
  }
}
