import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, EmptyStateComponent, PageHeaderComponent],
  templateUrl: './customers.component.html'
})
export class CustomersComponent {}
