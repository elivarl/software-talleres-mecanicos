import { CommonModule } from '@angular/common';
import { Component, output } from '@angular/core';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule, Button],
  templateUrl: './topbar.component.html',
  styleUrl: './topbar.component.css'
})
export class TopbarComponent {
  readonly menuButtonClick = output<void>();

  onMenuButtonClick(): void {
    this.menuButtonClick.emit();
  }
}
