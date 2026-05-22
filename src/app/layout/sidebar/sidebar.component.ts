import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { PanelMenu } from 'primeng/panelmenu';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, PanelMenu],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  readonly menuItems = input<MenuItem[]>([]);
  readonly open = input(false);
  readonly navigate = output<void>();

  onNavigate(): void {
    this.navigate.emit();
  }
}
