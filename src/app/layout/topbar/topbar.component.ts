import { CommonModule } from '@angular/common';
import { Component, computed, inject, output } from '@angular/core';
import { Button } from 'primeng/button';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule, Button],
  templateUrl: './topbar.component.html',
  styleUrl: './topbar.component.css'
})
export class TopbarComponent {
  private readonly authService = inject(AuthService);

  readonly menuButtonClick = output<void>();
  readonly currentUser = this.authService.currentUser;
  readonly userName = computed(() => this.currentUser()?.fullName ?? 'Usuario');
  readonly userRole = computed(() => {
    const role = this.currentUser()?.role;

    switch (role) {
      case 'ADMIN':
        return 'Administrador';
      case 'RECEPTIONIST':
        return 'Recepción';
      case 'MECHANIC':
        return 'Mecánico';
      default:
        return 'Sin rol';
    }
  });

  onMenuButtonClick(): void {
    this.menuButtonClick.emit();
  }

  logout(): void {
    this.authService.logout('Sesión cerrada correctamente.');
  }
}
