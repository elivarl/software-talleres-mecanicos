import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { ProgressSpinner } from 'primeng/progressspinner';

@Component({
  selector: 'app-loading-state',
  standalone: true,
  imports: [CommonModule, ProgressSpinner],
  templateUrl: './loading-state.component.html',
  styleUrl: './loading-state.component.css'
})
export class LoadingStateComponent {
  readonly message = input('Cargando información...');
}
