import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { LoginRequest } from '../models/login-request.model';
import { LoginResponse } from '../models/login-response.model';
import { User } from '../models/user.model';

interface StoredAuthSession {
  accessToken: string;
  tokenType: string;
  expiresAt: number;
  user: User;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly messageService = inject(MessageService);

  private readonly storageKey = 'taller360.auth.session';
  private readonly session = signal<StoredAuthSession | null>(this.readStoredSession());

  readonly currentUser = computed(() => this.session()?.user ?? null);
  readonly isAuthenticated = computed(() => {
    const currentSession = this.session();
    return !!currentSession && currentSession.expiresAt > Date.now();
  });

  login(payload: LoginRequest) {
    return this.http
      .post<LoginResponse>(`${environment.backendApiUrl}/auth/login`, payload)
      .pipe(tap((response) => this.storeSession(response)));
  }

  logout(message?: string): void {
    this.clearSession();

    if (message) {
      this.messageService.add({
        severity: 'info',
        summary: 'Sesión',
        detail: message
      });
    }

    void this.router.navigate(['/login']);
  }

  getAuthorizationHeader(): string | null {
    const currentSession = this.session();

    if (!currentSession) {
      return null;
    }

    if (currentSession.expiresAt <= Date.now()) {
      this.clearSession();
      return null;
    }

    return `${currentSession.tokenType} ${currentSession.accessToken}`;
  }

  hasValidSession(): boolean {
    return this.isAuthenticated();
  }

  private storeSession(response: LoginResponse): void {
    const session: StoredAuthSession = {
      accessToken: response.accessToken,
      tokenType: response.tokenType,
      expiresAt: Date.now() + response.expiresIn * 1000,
      user: response.user
    };

    this.session.set(session);
    localStorage.setItem(this.storageKey, JSON.stringify(session));
  }

  private clearSession(): void {
    this.session.set(null);
    localStorage.removeItem(this.storageKey);
  }

  private readStoredSession(): StoredAuthSession | null {
    const rawSession = localStorage.getItem(this.storageKey);

    if (!rawSession) {
      return null;
    }

    try {
      const parsedSession = JSON.parse(rawSession) as StoredAuthSession;

      if (parsedSession.expiresAt <= Date.now()) {
        localStorage.removeItem(this.storageKey);
        return null;
      }

      return parsedSession;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }
}
