import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Tag } from 'primeng/tag';
import { StatCardModel } from '../../../../core/models/stat-card.model';
import { ApiConfigService } from '../../../../core/services/api-config.service';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { StatCardComponent } from '../../../../shared/components/stat-card/stat-card.component';

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, Button, Card, Tag, PageHeaderComponent, StatCardComponent],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.css'
})
export class DashboardHomeComponent {
  private readonly apiConfigService = inject(ApiConfigService);

  readonly backendApiUrl = this.apiConfigService.backendApiUrl;

  readonly stats: StatCardModel[] = [
    {
      label: 'Angular',
      value: '20.x',
      helper: 'Aplicación standalone preparada para escalar por features.',
      icon: 'pi pi-code',
      severity: 'info'
    },
    {
      label: 'PrimeNG',
      value: '20.4',
      helper: 'Tema base, iconos, toast y confirm dialog listos.',
      icon: 'pi pi-palette',
      severity: 'success'
    },
    {
      label: 'Router',
      value: 'Configurado',
      helper: 'Shell principal montada para crecer hacia auth y módulos.',
      icon: 'pi pi-directions',
      severity: 'warn'
    },
    {
      label: 'API backend',
      value: 'Environment',
      helper: 'Base URL centralizada para los próximos servicios.',
      icon: 'pi pi-server',
      severity: 'contrast'
    }
  ];

  readonly nextSteps = [
    'Implementar autenticación JWT y guards.',
    'Conectar servicios HttpClient al backend real.',
    'Crear módulos de clientes, vehículos y órdenes de trabajo.',
    'Agregar manejo de errores y estados vacíos por feature.'
  ];
}
