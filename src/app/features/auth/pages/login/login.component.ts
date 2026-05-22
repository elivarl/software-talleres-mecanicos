import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { InputText } from 'primeng/inputtext';
import { MessageService } from 'primeng/api';
import { Password } from 'primeng/password';
import { ProgressSpinner } from 'primeng/progressspinner';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../../../core/auth/auth.service';
import { LoginRequest } from '../../../../core/models/login-request.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    Card,
    InputText,
    Password,
    Button,
    ProgressSpinner
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly messageService = inject(MessageService);

  readonly isSubmitting = signal(false);

  readonly loginForm = this.formBuilder.nonNullable.group({
    email: ['admin@taller360.com', [Validators.required, Validators.email]],
    password: ['Admin12345*', [Validators.required]]
  });

  constructor() {
    if (this.authService.hasValidSession()) {
      void this.router.navigate(['/dashboard']);
    }
  }

  submit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);

    const payload: LoginRequest = this.loginForm.getRawValue();

    this.authService
      .login(payload)
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: () => {
          const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || '/dashboard';
          void this.router.navigateByUrl(returnUrl);
          this.messageService.add({
            severity: 'success',
            summary: 'Bienvenido',
            detail: 'Inicio de sesión correcto.'
          });
        },
        error: (error: HttpErrorResponse) => {
          this.messageService.add({
            severity: 'error',
            summary: 'No se pudo iniciar sesión',
            detail: error.status === 401
              ? 'Credenciales inválidas.'
              : 'Ocurrió un error al intentar ingresar.'
          });
        }
      });
  }

  hasError(controlName: 'email' | 'password', errorCode: string): boolean {
    const control = this.loginForm.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }
}
