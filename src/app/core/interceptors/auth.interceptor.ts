import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const authorizationHeader = authService.getAuthorizationHeader();
  const isLoginRequest = req.url.includes('/api/auth/login');

  const authReq =
    authorizationHeader && !isLoginRequest
      ? req.clone({
          setHeaders: {
            Authorization: authorizationHeader
          }
        })
      : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !isLoginRequest) {
        authService.logout('Sesión expirada. Inicia sesión nuevamente.');
      }

      return throwError(() => error);
    })
  );
};
