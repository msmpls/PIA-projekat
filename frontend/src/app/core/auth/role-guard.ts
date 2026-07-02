import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const allowedRoles = route.data['roles'] as string[];
  const userRole = authService.getRole();

  if (!authService.isLoggedIn() || !userRole || !allowedRoles.includes(userRole)) {
    router.navigate(['/']); // ili na neku "403" stranicu
    return false;
  }

  return true;
};