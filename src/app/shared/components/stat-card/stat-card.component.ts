import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { Card } from 'primeng/card';
import { Tag } from 'primeng/tag';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule, Card, Tag],
  templateUrl: './stat-card.component.html',
  styleUrl: './stat-card.component.css'
})
export class StatCardComponent {
  readonly label = input.required<string>();
  readonly value = input.required<string>();
  readonly helper = input.required<string>();
  readonly icon = input.required<string>();
  readonly severity = input<'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast'>(
    'info'
  );
}
