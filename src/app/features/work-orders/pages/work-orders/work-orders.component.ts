import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-work-orders',
  standalone: true,
  imports: [CommonModule, EmptyStateComponent, PageHeaderComponent],
  templateUrl: './work-orders.component.html'
})
export class WorkOrdersComponent {}
