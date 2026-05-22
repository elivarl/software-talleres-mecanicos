import { inject } from '@angular/core';
import { CanActivateChildFn, CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

function checkAuthentication(url: string) {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.hasValidSession()) {
    return true;
  }

  return router.createUrlTree(['/login'], {
    queryParams: {
      returnUrl: url
    }
  });
}

export const authGuard: CanActivateFn = (_route, state) => checkAuthentication(state.url);

export const authChildGuard: CanActivateChildFn = (_route, state) =>
  checkAuthentication(state.url);
